package com.wudao.kms.controller;

import com.alibaba.fastjson2.JSONObject;
import com.wudao.common.model.vo.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.wudao.kms.dto.AiShopSourceListReq;

import jakarta.annotation.Resource;

import java.util.Map;


@RestController
@RequestMapping("agent")
public class AgentController {

    @Value("${aidojo.api-url}")
    private String apiUrl;

    @Resource
    private RestTemplate restTemplate;

    @PostMapping("/list")
    public R<Object> list(@RequestBody AiShopSourceListReq req) {
        String url = apiUrl + "/ai/shop/sourceList";
        // 定义json header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(JSONObject.toJSONString(req), headers);
        Map response = restTemplate.postForObject(url, entity, Map.class);
        Object data = response.get("data");
        return R.ok(data);
    }
}
