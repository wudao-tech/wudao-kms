package com.wudao.kms.llm.llmmode.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.MPJWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.llmmode.mapper.LLMModelMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LLMModelService extends MPJBaseServiceImpl<LLMModelMapper, LLMModel> {
    public PageDomain<LLMModel> queryList(LLMModel model, PageDomain<LLMModel> page){
        LambdaQueryWrapper<LLMModel> queryWrapper = new LambdaQueryWrapper<>();

        // 名称模糊查询
        if (StringUtils.isNotBlank(model.getName())) {
            queryWrapper.like(LLMModel::getName, model.getName());
        }

        // 模型类型筛选
        if (model.getModelType() != null) {
            queryWrapper.eq(LLMModel::getModelType, model.getModelType());
        }

        // 供应商筛选
        if (StringUtils.isNotBlank(model.getProviderCode())) {
            queryWrapper.eq(LLMModel::getProviderCode, model.getProviderCode());
        }

        // 状态筛选
        if (model.getStatus() != null) {
            queryWrapper.eq(LLMModel::getStatus, model.getStatus());
        }

        if (model.getStreamFlag() != null) {
            queryWrapper.eq(LLMModel::getStreamFlag, model.getStreamFlag());
        }

        if (model.getTokenType() != null) {
            queryWrapper.eq(LLMModel::getTokenType, model.getTokenType());
        }

        IPage<LLMModel> pageResult = this.page(new Page<>(page.getPageNum(), page.getPageSize()), queryWrapper);
        page.setRecords(pageResult.getRecords());
        page.setTotal(pageResult.getTotal());
        return page;
    }

    public LLMModel queryEmbeddingModelByKnowledgeId(Long knowledgeId){
        return getOne(MPJWrappers.lambdaJoin(LLMModel.class).leftJoin(KnowledgeBase.class,KnowledgeBase::getEmbeddingModel, LLMModel::getModel)
                .eq(KnowledgeBase::getId, knowledgeId).last("limit 1"));
    }

    public LLMModel queryVlModelByKnowledgeId(Long knowledgeId){
        return getOne(MPJWrappers.lambdaJoin(LLMModel.class).leftJoin(KnowledgeBase.class,KnowledgeBase::getMultimodalModel, LLMModel::getModel)
                .eq(KnowledgeBase::getId, knowledgeId).last("limit 1"));
    }

    public LLMModel queryTextModelByKnowledgeId(Long knowledgeId){
        return getOne(MPJWrappers.lambdaJoin(LLMModel.class).leftJoin(KnowledgeBase.class,KnowledgeBase::getTextModel, LLMModel::getModel)
                        .eq(KnowledgeBase::getId, knowledgeId).last("limit 1"));
    }

    public List<LLMModel> queryEmbeddingModelByKnowledgeId(List<Long> knowledgeIds){
        MPJLambdaWrapper<LLMModel> wrapper = new MPJLambdaWrapper<>();
        wrapper.leftJoin(KnowledgeBase.class, KnowledgeBase::getEmbeddingModel, LLMModel::getModel);
        wrapper.in(KnowledgeBase::getId, knowledgeIds);
        return list(wrapper);
    }

    public LLMModel queryLLMByModel(String model) {
        LambdaQueryWrapper<LLMModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LLMModel::getModel, model);
        queryWrapper.last("limit 1");
        return getOne(queryWrapper);
    }
}
