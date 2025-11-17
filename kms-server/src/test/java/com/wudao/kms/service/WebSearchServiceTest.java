package com.wudao.kms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WebSearchService测试类
 */
@SpringBootTest
public class WebSearchServiceTest {

    @Autowired
    private WebSearchService webSearchService;

    @Test
    public void testGoogleSerperSearch() {
        // 测试Google Serper API搜索
        String query = "苹果公司";
        Integer maxResults = 5;
        
        WebSearchService.WebSearchResponse response = webSearchService.search(query, maxResults);
        
        assertNotNull(response);
        assertNotNull(response.getResults());
        assertEquals(query, response.getQuery());
        
        System.out.println("搜索查询: " + response.getQuery());
        System.out.println("结果数量: " + response.getTotalResults());
        System.out.println("搜索耗时: " + response.getSearchTimeMs() + "ms");
        System.out.println("数据源: " + response.getSources());
        
        // 打印搜索结果
        for (WebSearchService.SearchResult result : response.getResults()) {
            System.out.println("标题: " + result.getTitle());
            System.out.println("URL: " + result.getUrl());
            System.out.println("摘要: " + result.getSnippet());
            System.out.println("来源: " + result.getSource());
            System.out.println("相关性分数: " + result.getRelevanceScore());
            System.out.println("---");
        }
    }

    @Test
    public void testFormatSearchResults() {
        // 测试搜索结果格式化
        String query = "人工智能";
        WebSearchService.WebSearchResponse response = webSearchService.search(query, 3);
        
        String formattedResults = webSearchService.formatSearchResults(response);
        
        assertNotNull(formattedResults);
        assertFalse(formattedResults.isEmpty());
        
        System.out.println("格式化搜索结果:");
        System.out.println(formattedResults);
    }
} 