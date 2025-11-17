package com.wudao.kms.service;

import com.wudao.kms.dto.knowledgefile.FileProcessProgressDTO;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.mapper.KnowledgeFileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 知识文件进度更新服务
 * 使用独立事务更新进度，避免阻塞主流程
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeFileProgressService {

    private final KnowledgeFileMapper knowledgeFileMapper;

    /**
     * 更新VL处理进度
     * 使用新事务，立即提交
     *
     * @param fileId      文件ID
     * @param finishCount 已完成数量
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateVlProgress(Long fileId, int finishCount) {
        try {
            KnowledgeFile file = knowledgeFileMapper.selectById(fileId);
            if (file == null) {
                log.warn("文件不存在: fileId={}", fileId);
                return;
            }

            FileProcessProgressDTO progress = file.getProcessProgress();
            if (progress == null) {
                progress = FileProcessProgressDTO.builder().build();
            }
            progress.setVlFinish(finishCount);
            file.setProcessProgress(progress);

            knowledgeFileMapper.updateById(file);
            log.debug("更新文件VL进度成功: fileId={}, vlFinish={}", fileId, finishCount);
        } catch (Exception e) {
            log.error("更新文件VL进度失败: fileId={}, finishCount={}", fileId, finishCount, e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 更新QA提取进度
     * 使用新事务，立即提交
     *
     * @param fileId      文件ID
     * @param finishCount 已完成数量
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateQaProgress(Long fileId, int finishCount) {
        try {
            KnowledgeFile file = knowledgeFileMapper.selectById(fileId);
            if (file == null) {
                log.warn("文件不存在: fileId={}", fileId);
                return;
            }

            FileProcessProgressDTO progress = file.getProcessProgress();
            if (progress == null) {
                progress = FileProcessProgressDTO.builder().build();
            }
            progress.setQaFinish(finishCount);
            file.setProcessProgress(progress);

            knowledgeFileMapper.updateById(file);
            log.debug("更新文件QA进度成功: fileId={}, qaFinish={}", fileId, finishCount);
        } catch (Exception e) {
            log.error("更新文件QA进度失败: fileId={}, finishCount={}", fileId, finishCount, e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 更新向量化进度
     * 使用新事务，立即提交
     *
     * @param fileId      文件ID
     * @param finishCount 已完成数量
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateVectorProgress(Long fileId, int finishCount) {
        try {
            KnowledgeFile file = knowledgeFileMapper.selectById(fileId);
            if (file == null) {
                log.warn("文件不存在: fileId={}", fileId);
                return;
            }

            FileProcessProgressDTO progress = file.getProcessProgress();
            if (progress == null) {
                progress = FileProcessProgressDTO.builder().build();
            }
            progress.setVectorFinish(finishCount);
            file.setProcessProgress(progress);

            knowledgeFileMapper.updateById(file);
            log.debug("更新文件向量化进度成功: fileId={}, vectorFinish={}", fileId, finishCount);
        } catch (Exception e) {
            log.error("更新文件向量化进度失败: fileId={}, finishCount={}", fileId, finishCount, e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 批量重置所有进度为0
     * 用于重新处理文件时重置进度
     *
     * @param fileId 文件ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void resetAllProgress(Long fileId) {
        try {
            KnowledgeFile file = knowledgeFileMapper.selectById(fileId);
            if (file == null) {
                log.warn("文件不存在: fileId={}", fileId);
                return;
            }

            FileProcessProgressDTO progress = FileProcessProgressDTO.builder()
                    .vlFinish(0)
                    .qaFinish(0)
                    .vectorFinish(0)
                    .build();
            file.setProcessProgress(progress);

            knowledgeFileMapper.updateById(file);
            log.debug("重置文件所有进度成功: fileId={}", fileId);
        } catch (Exception e) {
            log.error("重置文件所有进度失败: fileId={}", fileId, e);
        }
    }

    /**
     * 批量更新所有进度为指定值（通常用于完成时）
     *
     * @param fileId 文件ID
     * @param total  总数量
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void completeAllProgress(Long fileId, int total) {
        try {
            KnowledgeFile file = knowledgeFileMapper.selectById(fileId);
            if (file == null) {
                log.warn("文件不存在: fileId={}", fileId);
                return;
            }

            FileProcessProgressDTO progress = FileProcessProgressDTO.builder()
                    .vlFinish(total)
                    .qaFinish(total)
                    .vectorFinish(total)
                    .build();
            file.setProcessProgress(progress);

            knowledgeFileMapper.updateById(file);
            log.debug("完成文件所有进度成功: fileId={}, total={}", fileId, total);
        } catch (Exception e) {
            log.error("完成文件所有进度失败: fileId={}, total={}", fileId, total, e);
        }
    }
}
