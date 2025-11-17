package com.wudao.kms.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 网络搜索服务
 * 集成多个搜索源：Google Serper、Bing、知乎、36kr等
 */
@Slf4j
@Service
public class WebSearchService {

    private final RestTemplate restTemplate;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public WebSearchService(RestTemplate restTemplate, OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    @Value("${web-search.serper.api-key:9cf850a92ede239ac6bec8dff3e7f455e2c60f55}")
    private String serperApiKey;

    @Value("${web-search.serper.endpoint:https://google.serper.dev/search}")
    private String serperEndpoint;

    @Value("${web-search.bing.api-key:}")
    private String bingApiKey;

    @Value("${web-search.bing.endpoint:https://api.bing.microsoft.com/v7.0/search}")
    private String bingEndpoint;

    @Value("${web-search.timeout:5000}")
    private Integer searchTimeout;

    /**
     * 搜索结果
     */
    @Data
    public static class SearchResult {
        private String title;
        private String url;
        private String snippet;
        private String source;
        private Double relevanceScore;
    }

    /**
     * 综合搜索结果
     */
    @Data
    public static class WebSearchResponse {
        private String query;
        private List<SearchResult> results;
        private Integer totalResults;
        private Long searchTimeMs;
        private List<String> sources;

        public static WebSearchResponse empty(String query) {
            WebSearchResponse response = new WebSearchResponse();
            response.query = query;
            response.results = List.of();
            response.totalResults = 0;
            response.searchTimeMs = 0L;
            response.sources = List.of();
            return response;
        }
    }

    /**
     * 执行网络搜索 - 优先使用Google Serper API
     */
    public WebSearchResponse search(String query, Integer maxResults) {
        if (query == null || query.trim().isEmpty()) {
            return WebSearchResponse.empty(query);
        }

        final Integer finalMaxResults = maxResults != null ? Math.min(maxResults, 20) : 10;
        long startTime = System.currentTimeMillis();

        try {
            log.info("开始网络搜索，查询: {}, 最大结果数: {}", query, finalMaxResults);

            // 优先使用Google Serper API
            List<SearchResult> serperResults = searchWithSerper(query, finalMaxResults);
            if (!serperResults.isEmpty()) {
                WebSearchResponse response = new WebSearchResponse();
                response.setQuery(query);
                response.setResults(serperResults);
                response.setTotalResults(serperResults.size());
                response.setSearchTimeMs(System.currentTimeMillis() - startTime);
                response.setSources(List.of("Google"));

                log.info("Google Serper搜索完成，查询: {}, 结果数: {}, 耗时: {}ms", 
                        query, serperResults.size(), response.getSearchTimeMs());
                return response;
            }

            // 如果Serper失败，回退到原有的多源搜索
            return performFallbackSearch(query, finalMaxResults, startTime);

        } catch (Exception e) {
            log.error("网络搜索失败: {}", query, e);
            return WebSearchResponse.empty(query);
        }
    }

    /**
     * 使用Google Serper API进行搜索
     */
    private List<SearchResult> searchWithSerper(String query, Integer maxResults) {
        try {
            if (serperApiKey == null || serperApiKey.trim().isEmpty()) {
                log.warn("Google Serper API Key未配置，跳过Serper搜索");
                return List.of();
            }

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("q", query);
            requestBody.put("num", Math.min(maxResults, 10)); // Serper API最多返回10个结果

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // 构建请求
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonBody);
            Request request = new Request.Builder()
                    .url(serperEndpoint)
                    .method("POST", body)
                    .addHeader("X-API-KEY", serperApiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            // 执行请求
            Response response = okHttpClient.newCall(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return parseSerperResponse(responseBody);
            } else {
                log.error("Google Serper API请求失败，状态码: {}", response.code());
            }

        } catch (Exception e) {
            log.error("Google Serper搜索失败: {}", query, e);
        }
        return List.of();
    }

    /**
     * 解析Google Serper API响应
     */
    private List<SearchResult> parseSerperResponse(String responseBody) {
        List<SearchResult> results = new ArrayList<>();
        
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode organicNode = rootNode.get("organic");
            
            if (organicNode != null && organicNode.isArray()) {
                for (JsonNode item : organicNode) {
                    SearchResult result = new SearchResult();
                    result.setTitle(item.has("title") ? item.get("title").asText() : "");
                    result.setUrl(item.has("link") ? item.get("link").asText() : "");
                    result.setSnippet(item.has("snippet") ? item.get("snippet").asText() : "");
                    result.setSource("Google");
                    
                    // 根据在结果中的位置计算相关性分数
                    result.setRelevanceScore(1.0 - (results.size() * 0.1));
                    
                    results.add(result);
                }
            }
            
            log.info("成功解析Google Serper响应，获得{}条结果", results.size());
            
        } catch (Exception e) {
            log.error("解析Google Serper响应失败", e);
        }
        
        return results;
    }

    /**
     * 备用搜索方法 - 使用原有的多源搜索
     */
    private WebSearchResponse performFallbackSearch(String query, Integer maxResults, long startTime) {
        try {
            log.info("执行备用搜索策略");
            
            List<CompletableFuture<List<SearchResult>>> searchTasks = new ArrayList<>();

            // Bing搜索
            if (bingApiKey != null && !bingApiKey.trim().isEmpty()) {
                searchTasks.add(CompletableFuture.supplyAsync(() -> searchBing(query, maxResults / 2)));
            }

            // 知乎搜索  
            searchTasks.add(CompletableFuture.supplyAsync(() -> searchZhihu(query, maxResults / 4)));

            // 36kr搜索
            searchTasks.add(CompletableFuture.supplyAsync(() -> search36kr(query, maxResults / 4)));

            // 等待所有搜索完成
            CompletableFuture<Void> allSearches = CompletableFuture.allOf(
                    searchTasks.toArray(new CompletableFuture[0])
            );

            // 设置超时
            try {
                allSearches.get(searchTimeout, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.warn("网络搜索超时或部分失败: {}", e.getMessage());
            }

            // 收集结果
            List<SearchResult> allResults = new ArrayList<>();
            List<String> sources = new ArrayList<>();

            for (CompletableFuture<List<SearchResult>> task : searchTasks) {
                try {
                    List<SearchResult> results = task.get();
                    if (results != null && !results.isEmpty()) {
                        allResults.addAll(results);
                        sources.add(results.get(0).getSource());
                    }
                } catch (Exception e) {
                    log.warn("获取搜索结果失败: {}", e.getMessage());
                }
            }

            // 按相关性排序并限制结果数
            allResults.sort((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()));
            if (allResults.size() > maxResults) {
                allResults = allResults.subList(0, maxResults);
            }

            WebSearchResponse response = new WebSearchResponse();
            response.setQuery(query);
            response.setResults(allResults);
            response.setTotalResults(allResults.size());
            response.setSearchTimeMs(System.currentTimeMillis() - startTime);
            response.setSources(sources);

            log.info("备用搜索完成，查询: {}, 结果数: {}, 耗时: {}ms", 
                    query, allResults.size(), response.getSearchTimeMs());

            return response;

        } catch (Exception e) {
            log.error("备用搜索失败: {}", query, e);
            return WebSearchResponse.empty(query);
        }
    }

    /**
     * Bing搜索
     */
    private List<SearchResult> searchBing(String query, Integer count) {
        try {
            if (bingApiKey == null || bingApiKey.trim().isEmpty()) {
                log.warn("Bing API Key未配置，跳过Bing搜索");
                return List.of();
            }

            String url = bingEndpoint + "?q=" + query + "&count=" + count + "&mkt=zh-CN";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Ocp-Apim-Subscription-Key", bingApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseBingResponse(response.getBody());
            }

        } catch (Exception e) {
            log.error("Bing搜索失败: {}", query, e);
        }
        return List.of();
    }

    /**
     * 知乎搜索（模拟实现）
     */
    private List<SearchResult> searchZhihu(String query, Integer count) {
        try {
            // 这里可以实现知乎的API调用或爬虫逻辑
            // 由于知乎没有公开API，这里提供模拟实现
            log.info("模拟知乎搜索: {}", query);
            
            List<SearchResult> results = new ArrayList<>();
            
            // 模拟结果
            for (int i = 0; i < Math.min(count, 3); i++) {
                SearchResult result = new SearchResult();
                result.setTitle("知乎 - " + query + " 相关问题 " + (i + 1));
                result.setUrl("https://www.zhihu.com/search?q=" + query);
                result.setSnippet("这是知乎上关于 " + query + " 的讨论内容...");
                result.setSource("知乎");
                result.setRelevanceScore(0.8 - i * 0.1);
                results.add(result);
            }
            
            return results;

        } catch (Exception e) {
            log.error("知乎搜索失败: {}", query, e);
        }
        return List.of();
    }

    /**
     * 36kr搜索（模拟实现）
     */
    private List<SearchResult> search36kr(String query, Integer count) {
        try {
            // 这里可以实现36kr的API调用或RSS解析逻辑
            log.info("模拟36kr搜索: {}", query);
            
            List<SearchResult> results = new ArrayList<>();
            
            // 模拟结果
            for (int i = 0; i < Math.min(count, 2); i++) {
                SearchResult result = new SearchResult();
                result.setTitle("36氪 - " + query + " 行业分析 " + (i + 1));
                result.setUrl("https://36kr.com/search/articles/" + query);
                result.setSnippet("这是36氪关于 " + query + " 的行业分析和新闻报道...");
                result.setSource("36氪");
                result.setRelevanceScore(0.7 - i * 0.1);
                results.add(result);
            }
            
            return results;

        } catch (Exception e) {
            log.error("36kr搜索失败: {}", query, e);
        }
        return List.of();
    }

    /**
     * 解析Bing搜索响应
     */
    private List<SearchResult> parseBingResponse(Map<String, Object> responseBody) {
        List<SearchResult> results = new ArrayList<>();
        
        try {
            Map<String, Object> webPages = (Map<String, Object>) responseBody.get("webPages");
            if (webPages != null) {
                List<Map<String, Object>> values = (List<Map<String, Object>>) webPages.get("value");
                if (values != null) {
                    for (Map<String, Object> item : values) {
                        SearchResult result = new SearchResult();
                        result.setTitle((String) item.get("name"));
                        result.setUrl((String) item.get("url"));
                        result.setSnippet((String) item.get("snippet"));
                        result.setSource("Bing");
                        
                        // 计算相关性分数（简单实现）
                        result.setRelevanceScore(0.9);
                        
                        results.add(result);
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析Bing响应失败", e);
        }
        
        return results;
    }

    /**
     * 格式化搜索结果为文本
     */
    public String formatSearchResults(WebSearchResponse searchResponse) {
        if (searchResponse == null || searchResponse.getResults().isEmpty()) {
            return "未找到相关的网络搜索结果。";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("网络搜索结果（").append(searchResponse.getTotalResults()).append("条）：\n\n");

        for (int i = 0; i < searchResponse.getResults().size(); i++) {
            SearchResult result = searchResponse.getResults().get(i);
            sb.append((i + 1)).append(". ");
            sb.append("【").append(result.getSource()).append("】");
            sb.append(result.getTitle()).append("\n");
            sb.append("   ").append(result.getSnippet()).append("\n");
            sb.append("   链接: ").append(result.getUrl()).append("\n\n");
        }

        return sb.toString();
    }
} 