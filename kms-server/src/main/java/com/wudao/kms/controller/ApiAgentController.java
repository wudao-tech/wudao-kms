package com.wudao.kms.controller;

import com.alibaba.fastjson2.JSONObject;
import com.wudao.common.model.vo.R;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.dto.AiShopSourceListReq;
import com.wudao.security.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@RestController
@RequestMapping("/api/agent")
public class ApiAgentController {

    @Value("${aidojo.api-url}")
    private String apiUrl;

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/workflowList")
    public R<Object> workflowList(){
        String url = apiUrl + "/prod-api/agent/v2/assistant/queryList";
        // 定义json header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject body = new JSONObject();
        body.put("createBy", SecurityUtils.getUserId());
        HttpEntity<String> entity = new HttpEntity<>(JSONObject.toJSONString(body), headers);
        Map response = restTemplate.postForObject(url, entity, Map.class);
        Object data = response.get("data");
        return R.ok(data);
    }
}
