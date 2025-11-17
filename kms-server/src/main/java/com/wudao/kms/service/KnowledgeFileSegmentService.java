package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.vo.R;
import com.wudao.kms.dto.KnowledgeFileSegmentPageDTO;
import com.wudao.kms.entity.KnowledgeFileSegment;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.llm.chat.ChatModelStrategy;
import com.wudao.kms.llm.chat.ChatModelStrategyFactory;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.llmmode.service.LLMModelService;
import com.wudao.kms.mapper.KnowledgeFileSegmentMapper;
import com.wudao.kms.vo.KnowledgeFileSegmentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件分段Service
 */
@Service
@RequiredArgsConstructor
public class KnowledgeFileSegmentService extends MPJBaseServiceImpl<KnowledgeFileSegmentMapper, KnowledgeFileSegment> {
    private final ChatModelStrategyFactory chatModelStrategyFactory;
    private final LLMModelService llmModelService;
    public R<List<KnowledgeFileSegment>> queryList(Long knowledgeId, Long knowledgeSpaceId, String fileMd5){
        LambdaQueryWrapper<KnowledgeFileSegment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeFileSegment::getFileMd5, fileMd5);
        wrapper.eq(KnowledgeFileSegment::getKnowledgeSpaceId, knowledgeSpaceId);
        wrapper.eq(KnowledgeFileSegment::getKnowledgeBaseId, knowledgeId);
        wrapper.isNull(KnowledgeFileSegment::getFileId);
        wrapper.orderByAsc(KnowledgeFileSegment::getId);
        return R.ok(this.list(wrapper));
    }

    public R<List<KnowledgeFileSegment>> queryList(Long fileId){
        LambdaQueryWrapper<KnowledgeFileSegment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeFileSegment::getFileId, fileId);
        wrapper.orderByAsc(KnowledgeFileSegment::getId);
        return R.ok(this.list(wrapper));
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Void> vector(Long fileId){
        LambdaQueryWrapper<KnowledgeFileSegment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeFileSegment::getFileId, fileId);
        wrapper.eq(KnowledgeFileSegment::getVectorFlag, false);
        List<KnowledgeFileSegment> fileSegments = this.list(wrapper);
        if (CollUtil.isNotEmpty(fileSegments)) {
            // 通过文件id找到对应知识库的向量模型
            MPJLambdaWrapper<LLMModel> llmWrapper = new MPJLambdaWrapper<>();
            llmWrapper.leftJoin(KnowledgeBase.class, KnowledgeBase::getEmbeddingModel, LLMModel::getModel);
            llmWrapper.leftJoin(KnowledgeFile.class, KnowledgeFile::getKnowledgeBaseId, KnowledgeBase::getId);
            llmWrapper.eq(KnowledgeFileSegment::getFileId, fileId);
            llmWrapper.last("limit 1");
            LLMModel llmModel = llmModelService.getOne(llmWrapper);
            ChatModelStrategy strategyFactoryStrategy = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode());
            // 向量化处理（由 Strategy 内部处理分段）
            List<String> contents = fileSegments.stream().map(KnowledgeFileSegment::getSegmentContent).toList();
            List<float[]> embeddings = strategyFactoryStrategy.embedding(llmModel.getModel(), contents);
            for (int i = 0; i < fileSegments.size(); i++) {
                fileSegments.get(i).setVector(embeddings.get(i));
            }
            this.updateBatchById(fileSegments);
        }
        return R.ok();
    }
} 