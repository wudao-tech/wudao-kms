package com.wudao.kms.controller;

import com.alibaba.fastjson.JSON;
import com.wudao.common.model.vo.R;
import com.wudao.common.utils.Threads;
import com.wudao.framework.service.SSEService;
import com.wudao.kms.dto.knowledgefile.KnowledgeTempDTO;
import com.wudao.kms.entity.KnowledgeFileSegment;
import com.wudao.kms.service.KnowledgeFileSegmentService;
import com.wudao.kms.service.KnowledgeFileService;
import com.wudao.kms.vo.DocumentSegmentProgressVO;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件分段Controller
 */
@Slf4j
@Tag(name = "文件分段管理", description = "文件分段相关接口")
@RestController
@RequestMapping("/api/knowledge-file-segment")
@RequiredArgsConstructor
public class KnowledgeFileSegmentController {

    private final KnowledgeFileSegmentService knowledgeFileSegmentService;
    private final KnowledgeFileService knowledgeFileService;
    private final SSEService sseService;

    @Operation(description = "查询临时的向量")
    @GetMapping("/queryList")
    public R<List<KnowledgeFileSegment>> queryList(@RequestParam Long knowledgeId,
                                                   @RequestParam Long knowledgeSpaceId,
                                                   @RequestParam String fileMd5) {
        return knowledgeFileSegmentService.queryList(knowledgeId, knowledgeSpaceId, fileMd5);
    }

    @Operation(description = "根据文件名查询分段列表")
    @GetMapping("/querySegmentByFileUuid")
    public R<List<KnowledgeFileSegment>> querySegmentByFileUuid(@RequestParam Long fileId) {
        return knowledgeFileSegmentService.queryList(fileId);
    }

    @Operation(description = "更新分段")
    @PutMapping
    public R<Void> updateSegmentContent(@RequestBody KnowledgeFileSegment knowledgeFileSegment) {
        knowledgeFileSegmentService.updateById(knowledgeFileSegment);
        return R.ok();
    }

    /**
     * 处理文档分段
     */
    @GetMapping(value = "/process_segment/{knowledgeBaseId}/{spaceId}/{sseUser}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "处理文档分段", description = "根据临时文件列表和分段配置处理文档分段，通过SSE流式返回处理进度")
    public R<Void> processDocumentSegment(@PathVariable Long knowledgeBaseId,
                                          @PathVariable Long spaceId,
                                          @PathVariable String sseUser) {
        Long userId = SecurityUtils.getUserId();
        DocumentSegmentProgressVO.FileProcessInfo fileInfo = new DocumentSegmentProgressVO.FileProcessInfo();
        Threads.newThread(() -> {
            try {
                KnowledgeTempDTO knowledgeTempDTO = knowledgeFileService.getKnowledgeTempFile(knowledgeBaseId, spaceId, userId);
                log.info("开始流式处理文档分段，文件数量: {}",
                        knowledgeTempDTO.getKnowledgeFileUploadTempDTOList() != null ?
                                knowledgeTempDTO.getKnowledgeFileUploadTempDTOList().size() : 0);
                knowledgeTempDTO.setUserId(userId);
                // 异步处理分段
                knowledgeFileService.processDocumentSegmentationWithProgress(knowledgeTempDTO);


                fileInfo.setStatus("completed");
                fileInfo.setSegmentStatus("分段完成");
                fileInfo.setFileProgress(100);
                fileInfo.setEndTime(LocalDateTime.now());
                sseService.send(sseUser,"process-document", JSON.toJSONString(fileInfo));
            } catch (Exception e) {
                log.error("运行失败",e);
                fileInfo.setStatus("error");
                fileInfo.setSegmentStatus("分段失败");
                fileInfo.setFileProgress(100);
                fileInfo.setEndTime(LocalDateTime.now());
                sseService.send(sseUser ,"process-document", JSON.toJSONString(fileInfo));
            }
        }).start();

        return R.ok();
    }

    @Operation(description = "更新之后重新向量")
    @GetMapping("/vector/{fileId}")
    public R<Void> vector(@PathVariable Long fileId){
        return knowledgeFileSegmentService.vector(fileId);
    }
} 