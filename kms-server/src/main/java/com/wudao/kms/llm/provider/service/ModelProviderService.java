package com.wudao.kms.llm.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.MPJWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.kms.llm.chat.ChatModelStrategy;
import com.wudao.kms.llm.chat.ChatModelStrategyFactory;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.llmmode.mapper.LLMModelMapper;
import com.wudao.kms.llm.provider.domain.ModelProvider;
import com.wudao.kms.llm.provider.dto.ModelProviderListResp;
import com.wudao.kms.llm.provider.mapper.ModelProviderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 模型厂商服务类
 *
 * @author wudao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelProviderService extends MPJBaseServiceImpl<ModelProviderMapper, ModelProvider> {

    private final ChatModelStrategyFactory chatModelStrategyFactory;
    private final LLMModelMapper llmModelMapper;
    private final ModelProviderMapper modelProviderMapper;

    /**
     * 查询列表（带模型数量）
     *
     * @param model 查询条件
     * @param page 分页参数
     * @return 分页结果
     */
    public PageDomain<ModelProviderListResp> queryList(ModelProvider model, PageDomain<ModelProvider> page) {
        MPJLambdaWrapper<ModelProvider> wrapper = MPJWrappers.lambdaJoin(ModelProvider.class)
                .selectAll(ModelProvider.class);
        // 查询条件
        wrapper.selectAs(SysUser::getNickname,ModelProviderListResp::getCreateByName);
        wrapper.selectAs(SysUser::getNickname, ModelProviderListResp::getCreateByName);
        wrapper.like(StringUtils.isNotBlank(model.getName()), ModelProvider::getName, model.getName())
                .eq(StringUtils.isNotBlank(model.getProviderCode()), ModelProvider::getProviderCode, model.getProviderCode())
                .eq(StringUtils.isNotBlank(model.getType()), ModelProvider::getType, model.getType())
                .eq(StringUtils.isNotBlank(model.getStatus()), ModelProvider::getStatus, model.getStatus());
        wrapper.orderByDesc(ModelProvider::getCreatedAt);
        // 分组统计
        wrapper.leftJoin(SysUser.class, SysUser::getId, ModelProvider::getCreatedBy);

        IPage<ModelProviderListResp> pageResult = this.selectJoinListPage(
                new Page<>(page.getPageNum(), page.getPageSize()),
                ModelProviderListResp.class,
                wrapper
        );

        pageResult.getRecords().forEach(item ->
                item.setModelCount(llmModelMapper.queryModelCountByProvider(item.getProviderCode())));

        PageDomain<ModelProviderListResp> result = new PageDomain<>();
        result.setRecords(pageResult.getRecords());
        result.setTotal(pageResult.getTotal());
        result.setPageNum(page.getPageNum());
        result.setPageSize(page.getPageSize());

        return result;
    }

    /**
     * 刷新厂商连接状态
     *
     * @return 更新后的厂商信息
     */
    public ModelProvider refresh(ModelProvider modelProvider) {
        // 1. 查询厂商信息
        if (modelProvider == null) {
            throw new IllegalArgumentException("厂商不存在");
        }

        // 2. 查找该厂商的任意一个模型
        LambdaQueryWrapper<LLMModel> modelWrapper = new LambdaQueryWrapper<>();
        modelWrapper.eq(LLMModel::getProviderCode, modelProvider.getProviderCode());
        modelWrapper.last("limit 1");

        LLMModel llmModel = llmModelMapper.selectOne(modelWrapper);

        if (llmModel == null) {
            log.warn("厂商{}没有关联的模型，无法测试连接", modelProvider.getName());
            modelProvider.setStatus("未配置模型");
            this.updateById(modelProvider);
            return modelProvider;
        }

        // 3. 获取对应的策略并测试连接
        try {
            ChatModelStrategy strategy = chatModelStrategyFactory.getStrategy(modelProvider.getProviderCode());

            // 调用 simpleChat 测试连接
            String testResult = strategy.simpleChat(
                    llmModel.getModel(),
                    "你是一个测试助手",
                    "请回复：测试成功",
                    false
            );

            if (StringUtils.isNotBlank(testResult)) {
                modelProvider.setStatus("连接成功");
                log.info("厂商{}连接测试成功", modelProvider.getName());
            } else {
                modelProvider.setStatus("连接失败");
                log.warn("厂商{}连接测试失败：返回结果为空", modelProvider.getName());
            }
        } catch (Exception e) {
            modelProvider.setStatus("连接失败");
            log.error("厂商{}连接测试失败", modelProvider.getName(), e);
        }
        modelProviderMapper.updateById(modelProvider);
        return modelProvider;
    }
}
