package com.wudao.kms.agent.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.id.NanoId;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.entity.SysUser;
import com.wudao.kms.agent.domain.AiAssistantMemory;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.agent.dto.AiAssistantMemoryDTO;
import com.wudao.kms.agent.dto.AssistantCreateRequest;
import com.wudao.kms.agent.dto.AssistantListRequest;
import com.wudao.kms.agent.dto.AssistantListResponse;
import com.wudao.kms.agent.dto.AssistantPublishRequest;
import com.wudao.kms.agent.dto.AssistantUpdateRequest;
import com.wudao.kms.agent.dto.ImprovePromptRequest;
import com.wudao.kms.agent.dto.LLMModelTestReq;
import com.wudao.kms.agent.mapper.AiAssistantMemoryMapper;
import com.wudao.kms.agent.mapper.AssistantMapper;
import com.wudao.kms.agent.service.AssistantService;
import com.wudao.kms.llm.chat.ChatModelStrategy;
import com.wudao.kms.llm.chat.ChatModelStrategyFactory;
import com.wudao.kms.llm.llmmode.service.LLMModelService;
import com.wudao.security.utils.SecurityUtils;
import com.xkcoding.http.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AI助手服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AssistantServiceImpl extends MPJBaseServiceImpl<AssistantMapper, Assistant> implements AssistantService {
    private final AssistantMapper assistantMapper;
    private final ChatModelStrategyFactory chatModelStrategyFactory;
    private final LLMModelService llmModelService;
    private final AiAssistantMemoryMapper aiAssistantMemoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Assistant createAssistant(AssistantCreateRequest request) {
        Assistant assistant = new Assistant();
        // 复制相同字段
        BeanUtils.copyProperties(request, assistant);
        // 生成UUID
        assistant.setUuid(NanoId.randomNanoId(10));
        // 设置默认值
        assistant.setCreatedAt(LocalDateTime.now());
        assistant.setCreatedBy(SecurityUtils.getUserId());
        // 保存到数据库
        assistantMapper.insert(assistant);
        log.info("创建AI助手成功: uuid={}, name={}, type={}, createBy={}",
                assistant.getUuid(), assistant.getName(), assistant.getCreatedBy());
        return assistant;
    }

    @Override
    public Assistant updateAssistant(AssistantUpdateRequest request) {

        // 通过UUID查找助手
        Assistant assistant = assistantMapper.selectOne(
                new QueryWrapper<Assistant>().eq("uuid", request.getUuid())
        );
        if (assistant == null) {
            throw new RuntimeException("助手不存在");
        }

        if (request.getRecommend() != null && request.getRecommend() && SecurityUtils.getUserId() != 1L) {
            throw new RuntimeException("只能管理员操作。");
        }

        // 创建人不服务就放开
        if (!assistant.getCreatedBy().equals(SecurityUtils.getUserId()) && SecurityUtils.getUserId() != 1L) {
            return assistant;
        }

        // 复制属性 (排除BaseEntity字段，避免覆盖创建信息)
        BeanUtils.copyProperties(request, assistant,
                "uuid", "id", "userId", "createTime", "isDelete",
                "createBy", "updateBy", "updateTime", "delFlag");
        // 设置更新信息
        assistant.setUpdatedBy(SecurityUtils.getUserId());
        assistant.setUpdatedAt(LocalDateTime.now());
        // 更新到数据库
        assistantMapper.updateById(assistant);
        log.info("更新AI助手成功: uuid={}, name={}, updateBy={}", assistant.getUuid(), assistant.getName(), assistant.getCreatedBy());
        return assistant;
    }


    @Override
    public Flux<ServerSentEvent<String>> optimizePrompt(ImprovePromptRequest request) {
        String systemPrompt = """
                你是一个提示词优化专家，请把用户输入的内容优化一下输出以下内容，请直接输出优化后的prompt，
                ## 角色
                你是**,你可以干**
                ## 技能
                ### 技能1
                ### 技能2
                ## 限制
                - 只回答与什么什么相关的问题。
                - 确保所有建议和步骤符合安全标准，避免对用户造成伤害。
                - 如果需要特定的专业工具或技术支持，明确指出并建议用户寻求专业维修服务。
                - 不得提供涉及非法或不安全的操作建议。
                - 如果引用了知识库内容，请标明出处
                
                以上为你可以输出的内容，除了这些以外，请不要输出额外内容
                """;
        ChatModelStrategy factoryStrategy = chatModelStrategyFactory.getStrategy(request.getProviderCode());
        LLMModelTestReq improvePromptReq = new LLMModelTestReq();
        improvePromptReq.setSystemPrompt(systemPrompt);
        improvePromptReq.setPrompt(request.getPrompt());
        // 根据modelId获取对应的模型
        improvePromptReq.setLlmModel(llmModelService.getById(request.getModelId()));
        return factoryStrategy.optimizePrompt(improvePromptReq);

    }

    @Override
    public Assistant getAssistantByUuid(String uuid) {
        QueryWrapper<Assistant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", uuid);
        // 优先使用del_flag查询，兼容旧的is_delete字段
        queryWrapper.and(wrapper -> wrapper
                .eq("del_flag", false)
                .or()
                .isNull("del_flag").eq("is_delete", 0)
        );
        return assistantMapper.selectOne(queryWrapper);
    }

    @Override
    public IPage<AssistantListResponse> getAssistantList(AssistantListRequest request) {
        // 创建分页对象
        IPage<AssistantListResponse> page = new Page<>(request.getPageNum(), request.getPageSize());
        // 构建查询条件
        MPJLambdaWrapper<Assistant> queryWrapper = new MPJLambdaWrapper<>();
        // 优先使用del_flag查询，兼容旧的is_delete字段
        // 按类型查询
        queryWrapper.selectAs("t3.id is not null", AssistantListResponse::getCollected);
        queryWrapper.eq(request.getStatus() != null, Assistant::getStatus, request.getStatus());
        queryWrapper.eq(request.getOnlyMe(), Assistant::getCreatedBy, SecurityUtils.getUserId());
        queryWrapper.eq(StringUtil.isNotEmpty(request.getSubjectId()), Assistant::getSubjectId, request.getSubjectId());
        queryWrapper.eq(request.getPermission() != null, Assistant::getPermission, request.getPermission());
        queryWrapper.eq(request.getRecommend() != null, Assistant::getRecommend, request.getRecommend());
        // 权限校验：管理员可以看到所有，普通用户只能看到自己创建的或公开的
        if (SecurityUtils.getUserId() != 1L) {
            queryWrapper.and(wrapper -> wrapper
                    .eq(Assistant::getCreatedBy, SecurityUtils.getUserId())  // 自己创建的
                    .or()
                    .eq(Assistant::getPermission, 1)  // 或者公开的
            );
        }
        if (StringUtil.isNotEmpty(request.getSource())) {
            queryWrapper.eq(request.getSource().equals("official"), Assistant::getCreatedBy, 1L);
            queryWrapper.eq(request.getSource().equals("myself"), Assistant::getCreatedBy, SecurityUtils.getUserId());
            queryWrapper.apply(request.getSource().equals("collected"), "t3.id is not null");
        }
        // 按名称模糊查询
        if (StringUtils.hasText(request.getName())) {
            queryWrapper.like(Assistant::getName, request.getName())
                    .or()
                    .like(Assistant::getDescription, request.getName())
                    .or()
                    .like(Assistant::getTags, request.getName());
        }
        queryWrapper.selectAll(Assistant.class);
        queryWrapper.selectAs(SysUser::getNickname, AssistantListResponse::getCreateByName);
        // 排序
        queryWrapper.orderByDesc(Assistant::getCreatedAt);
        queryWrapper.leftJoin(SysUser.class, SysUser::getId, Assistant::getCreatedBy);
        queryWrapper.leftJoin("favorite_record t3 on t3.target_id = t.uuid and t3.target_type = 'assistant' and t3.created_by = {0}", SecurityUtils.getUserId());
        return assistantMapper.selectJoinPage(page, AssistantListResponse.class, queryWrapper);
    }

    @Override
    public boolean deleteAssistant(String uuid) {
        // 判断是否为自己创建的
        Assistant assistant = getAssistantByUuid(uuid);
        if (!Objects.equals(assistant.getCreatedBy(), SecurityUtils.getUserId())) {
            return false;
        }
        return this.removeById(assistant.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(AssistantPublishRequest request) {
        try {
            LambdaQueryWrapper<Assistant> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Assistant::getUuid, request.getAssistantUuid());
            Assistant assistant = this.getOne(queryWrapper);
            if (assistant == null) {
                throw new RuntimeException("助手不存在");
            }
            // 更新助手状态
            assistant.setStatus(1);
            assistant.setUpdatedBy(SecurityUtils.getUserId());
            this.updateById(assistant);
        } catch (Exception e) {
            log.error("发布失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Assistant copyAssistant(String uuid) {
        LambdaQueryWrapper<Assistant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Assistant::getUuid, uuid);
        Assistant assistant = this.getOne(queryWrapper);
        if (assistant == null) {
            throw new RuntimeException("助手不存在");
        }
        assistant.setId(null);
        assistant.setName(assistant.getName() + "副本");
        assistant.setUuid(NanoId.randomNanoId(10));
        assistant.setCreatedBy(SecurityUtils.getUserId());
        assistant.setCreatedAt(LocalDateTime.now());
        assistant.setUpdatedAt(LocalDateTime.now());
        assistant.setUpdatedBy(SecurityUtils.getUserId());
        assistantMapper.insert(assistant);
        return assistant;
    }

}