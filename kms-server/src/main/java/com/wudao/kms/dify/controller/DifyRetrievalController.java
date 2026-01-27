package com.wudao.kms.dify.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wudao.common.model.entity.SysApiKey;
import com.wudao.kms.dify.dto.*;
import com.wudao.kms.dify.service.DifyRetrievalService;
import com.wudao.system.mapper.SysApiKeyMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Dify外部知识库检索接口
 * 符合Dify External Knowledge API规范
 *
 * @see <a href="https://docs.dify.ai/en/guides/knowledge-base/external-knowledge-api">Dify External Knowledge API</a>
 */
@Slf4j
@Tag(name = "Dify外部知识库", description = "Dify外部知识库检索接口")
@RestController
@RequestMapping("/dify")
@RequiredArgsConstructor
public class DifyRetrievalController {

    private final DifyRetrievalService difyRetrievalService;

    @Value("${dify.api.key:}")
    private String configuredApiKey;

    private final SysApiKeyMapper apiKeyMapper;

    /**
     * 知识库检索接口
     * POST /api/dify/retrieval
     */
    @Operation(summary = "知识库检索", description = "根据查询内容从知识库中检索相关文档片段")
    @PostMapping("/retrieval")
    public ResponseEntity<?> retrieval(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody DifyRetrievalRequest request) {

        log.info("收到Dify检索请求: knowledgeId={}, query={}", request.getKnowledgeId(), request.getQuery());

        // 验证Authorization header
        Long userId = validateAuthorization(authorization);
        if (userId == null) {
            log.warn("Dify检索请求授权失败");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(DifyErrorResponse.authFailed());
        }
        request.setUserId(userId);

        // 验证知识库是否存在
        if (!difyRetrievalService.validateKnowledgeExists(request.getKnowledgeId())) {
            log.warn("Dify检索请求知识库不存在: {}", request.getKnowledgeId());
            return ResponseEntity.ok(DifyErrorResponse.knowledgeNotFound());
        }

        try {
            // 执行检索
            DifyRetrievalResponse response = difyRetrievalService.retrieval(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Dify检索失败: knowledgeId={}", request.getKnowledgeId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DifyErrorResponse.authFailed());
        }
    }

    /**
     * 验证Authorization header
     *
     * @return 验证通过返回用户ID，验证失败返回null
     */
    private Long validateAuthorization(String authorization) {
        try {
            log.info("收到Dify用户token{}", authorization);
            if (StrUtil.isBlank(authorization)) {
                return null;
            }

            // 验证Bearer token格式
            if (!authorization.startsWith("Bearer ")) {
                return null;
            }

            String token = authorization.substring(7);

            // 使用 MyBatis-Plus 查询，同时校验 token 和过期时间
            LambdaQueryWrapper<SysApiKey> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysApiKey::getSecretKey, token)
                    .ge(SysApiKey::getExpiryTime, LocalDateTime.now());
            SysApiKey apiKey = apiKeyMapper.selectOne(wrapper);

            return apiKey != null ? apiKey.getCreatedBy() : null;
        } catch (Exception e) {
            log.error("验证token失败", e);
            return null;
        }
    }
}
