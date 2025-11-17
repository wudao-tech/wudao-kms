package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.common.utils.DateFormats;
import com.wudao.common.utils.ServletUtils;
import com.wudao.common.utils.StringUtils;
import com.wudao.common.utils.edoc.CsvUtils;
import com.wudao.common.utils.edoc.ExcelUtils;
import com.wudao.common.utils.edoc.Getters;
import com.wudao.common.utils.edoc.Setters;
import com.wudao.kms.dto.QaImproveAddReq;
import com.wudao.kms.entity.Feedback;
import com.wudao.kms.entity.QaImprove;
import com.wudao.kms.llm.chat.ChatModelStrategyFactory;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.llmmode.service.LLMModelService;
import com.wudao.kms.mapper.QaImproveMapper;
import com.wudao.security.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QaImproveService extends MPJBaseServiceImpl<QaImproveMapper, QaImprove> {
    private final ChatModelStrategyFactory chatModelStrategyFactory;
    private final FeedbackService feedbackService;
    private final LLMModelService llmModelService;

    public R<Void> addQa(QaImprove qaImprove) {
        qaImprove.setCreateBy(SecurityUtils.getUserId());
        LLMModel llmModel = llmModelService.queryEmbeddingModelByKnowledgeId(qaImprove.getKnowledgeId());
        List<float[]> embeddinged = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode()).embedding(llmModel.getModel(),List.of(qaImprove.getQuestion()));
        qaImprove.setQuestionVector(embeddinged.getFirst());
        qaImprove.setGenerateType("manual");
        this.save(qaImprove);
        return R.ok();
    }

    /**
     * 生成向量批量保存
     * @param qaImproves qa优化
     * @param knowledgeBaseId 知识库
     */
    @Transactional(rollbackFor = Exception.class)
    public void addBatch(List<QaImprove> qaImproves,Long knowledgeBaseId) {
        if (CollUtil.isEmpty(qaImproves)) {
            return;
        }
        LLMModel llmModel = llmModelService.queryEmbeddingModelByKnowledgeId(knowledgeBaseId);
        List<String> contents = qaImproves.stream().map(QaImprove::getQuestion).toList();
        List<float[]> embeddinged = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode()).embedding(llmModel.getModel(),contents);
        for (int i = 0; i < qaImproves.size(); i++) {
            qaImproves.get(i).setQuestionVector(embeddinged.get(i));
        }
        this.saveBatch(qaImproves);
    }

    public R<Void> batchPass(List<Long> qaIds){
        LambdaUpdateWrapper<QaImprove>  updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(QaImprove::getReviewStatus,"pass");
        updateWrapper.in(QaImprove::getId,qaIds);
        this.update(updateWrapper);
        return R.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Void> batchAddQa(QaImproveAddReq improveAddReq) {
        if (CollUtil.isEmpty(improveAddReq.getFeedbackIds())) {
            return R.ok();
        }
        List<QaImprove> qaImproves = new ArrayList<>();
        LLMModel llmModel = llmModelService.queryEmbeddingModelByKnowledgeId(improveAddReq.getKnowledgeId());
        feedbackService.listByIds(improveAddReq.getFeedbackIds()).forEach(feedback -> {
            QaImprove qaImprove = QaImprove.builder().question(feedback.getUserMessage())
                    .createBy(SecurityUtils.getUserId()).sourceId(feedback.getCreatedBy())
                    .answer(feedback.getAgent()).generateType("feedback").reviewStatus("reviewing")
                    .knowledgeId(improveAddReq.getKnowledgeId()).knowledgeSpaceId(improveAddReq.getKnowledgeSpaceId()).createTime(LocalDateTime.now()).build();
            qaImproves.add(qaImprove);
        });
        // 批量处理向量化（由 Strategy 内部处理分段）
        List<float[]> embeddings = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode())
                .embedding(llmModel.getModel(), qaImproves.stream().map(QaImprove::getQuestion).toList());
        for (int i = 0; i < qaImproves.size(); i++) {
            qaImproves.get(i).setQuestionVector(embeddings.get(i));
        }

        if (improveAddReq.getDelAfterUse()) {
            feedbackService.removeBatchByIds(improveAddReq.getFeedbackIds());
        } else {
            // 修改采纳状态
            LambdaUpdateWrapper<Feedback> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Feedback::getAdoptionStatus, "adopted");
            updateWrapper.in(Feedback::getId, improveAddReq.getFeedbackIds());
            feedbackService.update(updateWrapper);
        }

        this.saveBatch(qaImproves);
        return R.ok();
    }

    public R<Void> updateQa(QaImprove qaImprove) {
        LLMModel llmModel = llmModelService.queryEmbeddingModelByKnowledgeId(qaImprove.getKnowledgeId());
        List<float[]> embedding = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode()).embedding(llmModel.getModel(),List.of(qaImprove.getQuestion()));
        qaImprove.setQuestionVector(embedding.getFirst());
        this.updateById(qaImprove);
        return R.ok();
    }

    public PageDomain<QaImprove> queryList(QaImprove qaImprove, PageDomain<QaImprove> page) {
        MPJLambdaWrapper<QaImprove> wrapper = new MPJLambdaWrapper<>();
        wrapper.orderByDesc(QaImprove::getCreateTime);
        if (StringUtils.isNotEmpty(qaImprove.getQuestion())) {
            wrapper.like(QaImprove::getQuestion, qaImprove.getQuestion());
        }
        if (qaImprove.getStartTime() != null) {
            wrapper.ge(QaImprove::getCreateTime, qaImprove.getStartTime());
            wrapper.le(QaImprove::getCreateTime, qaImprove.getStartTime());
        }
        wrapper.eq(QaImprove::getKnowledgeId, qaImprove.getKnowledgeId());
        wrapper.eq(QaImprove::getKnowledgeSpaceId, qaImprove.getKnowledgeSpaceId());
        wrapper.eq(StringUtils.isNotEmpty(qaImprove.getReviewStatus()),QaImprove::getReviewStatus,qaImprove.getReviewStatus());
        wrapper.selectAll(QaImprove.class);
        wrapper.selectAs(SysUser::getNickname,QaImprove::getCreateByName);
        wrapper.leftJoin(SysUser.class,SysUser::getId, QaImprove::getCreateBy);
        wrapper.orderByDesc(QaImprove::getCreateTime);
        Page<QaImprove> qaImprovePage = new Page<>(page.getPageNum(), page.getPageSize());
        List<QaImprove> qaImproves = list(qaImprovePage, wrapper);
        page.setRecords(qaImproves);
        page.setTotal(qaImprovePage.getTotal());
        return page;
    }

    public void exportData(QaImprove qaImprove, HttpServletResponse response) throws Exception {
        MPJLambdaWrapper<QaImprove> wrapper = new MPJLambdaWrapper<>();
        wrapper.eq(QaImprove::getCreateBy, SecurityUtils.getUserId());
        wrapper.orderByDesc(QaImprove::getCreateTime);
        if (StringUtils.isNotEmpty(qaImprove.getQuestion())) {
            wrapper.like(QaImprove::getQuestion, qaImprove.getQuestion());
        }
        if (qaImprove.getStartTime() != null) {
            wrapper.ge(QaImprove::getCreateTime, qaImprove.getStartTime());
            wrapper.le(QaImprove::getCreateTime, qaImprove.getStartTime());
        }
        wrapper.selectAll(QaImprove.class);
        wrapper.selectAs(SysUser::getNickname, QaImprove::getSourceName);
        wrapper.leftJoin(SysUser.class, SysUser::getId, QaImprove::getSourceId);
        List<QaImprove> qaImproves = this.list(wrapper);

        String fileName = "QA调优_" + LocalDateTime.now().format(DateFormats.yyyyMMdd_HHmmss) + ".csv";
        Getters<QaImprove> getters = new Getters<>();
        getters.addText("序号", QaImprove::getId);
        getters.addText("问题", QaImprove::getQuestion);
        getters.addText("问答", QaImprove::getAnswer);
        getters.addText("来源", QaImprove::getSourceName);
        DateTimeFormatter dateTimeFormatter = DateFormats.STANDARD;
        getters.addText("提问时间", QaImprove::getCreateTime, dateTimeFormatter::format);
        ServletUtils.setFileName(response, fileName);
        CsvUtils.write(qaImproves, getters, response.getOutputStream());
    }

    /**
     * 导入
     *
     * @param file 文件
     * @param knowledgeId 知识库id
     * @param knowledgeSpaceId 知识空间id
     */
    @Transactional(rollbackFor = Exception.class)
    public void importData(MultipartFile file, Long knowledgeId, Long knowledgeSpaceId) throws Exception {
        Setters<QaImprove> setters = new Setters<>(QaImprove::new);
        setters.add("问题", QaImprove::setQuestion);
        setters.add("答案", QaImprove::setAnswer);
        List<QaImprove> qaImproves = ExcelUtils.read(file.getInputStream(), setters, 1);
        qaImproves.forEach(item -> {
            item.setKnowledgeId(knowledgeId);
            item.setKnowledgeSpaceId(knowledgeSpaceId);
            item.setCreateBy(SecurityUtils.getUserId());
            item.setCreateTime(LocalDateTime.now());
            item.setReviewStatus("reviewing");
            item.setGenerateType("upload");
        });
        LLMModel llmModel = llmModelService.queryEmbeddingModelByKnowledgeId(knowledgeId);
        //  查询知识库的embedding模型进行向量（由 Strategy 内部处理分段）
        List<float[]> embeddings = chatModelStrategyFactory.getStrategy(llmModel.getProviderCode())
                .embedding(llmModel.getModel(), qaImproves.stream().map(QaImprove::getQuestion).toList());
        for (int i = 0; i < qaImproves.size(); i++) {
            qaImproves.get(i).setQuestionVector(embeddings.get(i));
        }
        this.saveBatch(qaImproves);
    }
}
