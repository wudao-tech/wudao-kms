package com.wudao.kms.llm.chat.tool;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.wudao.common.model.vo.R;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.agent.service.AssistantService;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.dto.KnowledgeTestDTO;
import com.wudao.kms.dto.RagResponse;
import com.wudao.kms.llm.chat.factory.ToolContext;
import com.wudao.kms.service.VectorizationService;
import com.wudao.security.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 知识库工具类
 * 提供知识库搜索功能，用于MCP工具调用
 * 支持从ToolContext获取知识库配置参数
 */
@Slf4j
@Component
public class WorkflowTool implements BiFunction<WorkflowTool.Request, ToolContext, WorkflowTool.Response> {

    @Value("${aidojo.api-url}")
    private String apiUrl;

    private final OkHttpClient okHttpClient;

    public WorkflowTool() {
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    /**
     * 知识库搜索请求
     */
    @JsonClassDescription("Knowledge search tool request")
    public record Request(
            @JsonProperty(required = true, value = "query") 
            @JsonPropertyDescription("搜索查询内容") 
            String query
    ) {}

    /**
     * 知识库搜索响应
     */
    public record Response(
            @JsonPropertyDescription("http response flag")
            boolean success,
            @JsonPropertyDescription("http response code")
            String message,
            @JsonPropertyDescription("workflow execute response")
            String msg
    ) {
        /**
         * 创建成功响应
         */
        public static Response success(String message) {
            return new Response(true, "查询成功", message);
        }
        
        /**
         * 创建失败响应
         */
        public static Response error(String message) {
            return new Response(false, "查询失败",message);
        }
    }

    @Override
    public Response apply(Request request, ToolContext context) {
        log.info("工作流工具被调用，查询: {}", request.query());

        try {
            String workflowUuid = context.get("uuid");

            // 使用SpringUtil获取配置，避免@Value注入时机问题
            String currentApiUrl = apiUrl;
            if (currentApiUrl == null || currentApiUrl.isEmpty()) {
                currentApiUrl = SpringUtil.getProperty("aidojo.api-url");
                log.warn("从SpringUtil获取apiUrl: {}", currentApiUrl);
            }

            // 检查apiUrl是否可用
            if (currentApiUrl == null || currentApiUrl.isEmpty()) {
                log.error("apiUrl未配置，请检查配置文件中的aidojo.api-url");
                return Response.error("API地址未配置");
            }

            String url = currentApiUrl + "/agent/v2/api/chatApi";
            log.info("请求URL: {}, workflowUuid: {}", url, workflowUuid);

            // 构建请求body
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("assistantId", workflowUuid);
            bodyJson.put("query", request.query());

            // 使用OkHttp发送POST请求
            RequestBody body = RequestBody.create(
                    bodyJson.toJSONString(),
                    okhttp3.MediaType.parse("application/json; charset=utf-8")
            );

            okhttp3.Request httpRequest = new okhttp3.Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // 执行请求并阻塞等待响应（这会自动处理Mono的subscribe）
            try (okhttp3.Response httpResponse = okHttpClient.newCall(httpRequest).execute()) {
                if (!httpResponse.isSuccessful()) {
                    log.error("工作流调用失败: HTTP {}", httpResponse.code());
                    return Response.error("HTTP请求失败: " + httpResponse.code());
                }

                String responseBody = httpResponse.body().string();
                log.info("工作流响应: {}", responseBody);

                // 解析响应
                JSONObject responseJson = JSONObject.parseObject(responseBody);
                Object data = responseJson.get("data");

                if (data != null) {
                    return Response.success(data.toString());
                } else {
                    return Response.success(responseBody);
                }
            }
        } catch (IOException e) {
            log.error("工作流调用异常", e);
            return Response.error("请求异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("工作流工具执行失败", e);
            return Response.error("执行失败: " + e.getMessage());
        }
    }
}