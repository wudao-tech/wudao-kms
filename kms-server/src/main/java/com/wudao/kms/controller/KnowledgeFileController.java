package com.wudao.kms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.dto.KnowledgeFilePageDTO;
import com.wudao.kms.dto.knowledgefile.KnowledgeFileUploadTempDTO;
import com.wudao.kms.dto.knowledgefile.KnowledgeTempDTO;
import com.wudao.kms.dto.knowledgefile.KnowledgeFileBatchDownloadDTO;
import com.wudao.kms.dto.knowledgefile.KnowledgeFileOnlineCreateDTO;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.entity.req.KnowledgeFileApproveReq;
import com.wudao.kms.service.FavoriteRecordService;
import com.wudao.kms.service.KnowledgeFileService;
import com.wudao.kms.service.VisitRecordService;
import com.wudao.kms.vo.KnowledgeFileVO;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 知识库文件Controller
 */
@Tag(name = "知识库文件管理", description = "知识库文件相关接口")
@RestController
@RequestMapping("/api/knowledge-file")
@RequiredArgsConstructor
public class KnowledgeFileController {

    private final KnowledgeFileService knowledgeFileService;
    private final VisitRecordService visitRecordService;
    private final FavoriteRecordService favoriteRecordService;

    @Operation(summary = "创建文件")
    @PostMapping
    public R<Boolean> create(@RequestBody KnowledgeFile knowledgeFile) {
        Boolean result = knowledgeFileService.createKnowledgeFile(knowledgeFile);
        return R.ok(result);
    }

    @Operation(summary = "更新文件")
    @PutMapping
    public R<Boolean> update(@RequestBody KnowledgeFile knowledgeFile) {
        Boolean result = knowledgeFileService.updateKnowledgeFile(knowledgeFile);
        return R.ok(result);
    }

    @Operation(summary = "批量采纳")
    @PutMapping("/approveBatch")
    private R<Void> approveBatch(@RequestBody KnowledgeFileApproveReq req){
        knowledgeFileService.approveBatch(req);
        return R.ok();
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "文件ID") @PathVariable Long id) {
        Boolean result = knowledgeFileService.deleteKnowledgeFile(id);
        if (result) {
            favoriteRecordService.deleteFavorite("knowledge_file", id.toString());
        }
        return R.ok(result);
    }

    @Operation(summary = "根据ID查询文件")
    @GetMapping("/{id}")
    public R<KnowledgeFile> getById(@Parameter(description = "文件ID") @PathVariable Long id) {
        KnowledgeFile result = knowledgeFileService.getById(id);
        if (result == null) {
            return R.fail("id出错");
        }
        // 记录访问
        Long viewCount = visitRecordService.recordVisit("knowledge_file", id, result.getFileName(), result.getFilePath(), null, null);
        result.setViewCount(viewCount);
        return R.ok(result);
    }

    @Operation(summary = "分页查询文件")
    @GetMapping("/page")
    public PageDomain<KnowledgeFileVO> page(
            @ParameterObject KnowledgeFilePageDTO knowledgeFilePageDTO,
            @ParameterObject PageDomain<KnowledgeFileVO> page) {
        knowledgeFilePageDTO.setCurrent(page.getPageNum());
        knowledgeFilePageDTO.setSize(page.getPageSize());
        Page<KnowledgeFileVO> list = knowledgeFileService.list(knowledgeFilePageDTO);
        page.setData(list.getRecords());
        page.setTotal(list.getTotal());
        return page;
    }

    @Operation(summary = "根据知识库ID查询文件列表")
    @GetMapping("/list/{knowledgeBaseId}")
    public R<List<KnowledgeFile>> listByKnowledgeBaseId(@Parameter(description = "知识库ID") @PathVariable Long knowledgeBaseId) {
        List<KnowledgeFile> result = knowledgeFileService.listByKnowledgeBaseId(knowledgeBaseId);
        return R.ok(result);
    }

    @Operation(summary = "根据用户查询文件列表")
    @GetMapping("/list/file/{userId}")
    public R<List<KnowledgeFile>> listByUserId(@Parameter(description = "用户id") @PathVariable Long userId) {
        List<KnowledgeFile> result = knowledgeFileService.listByUserId(userId);
        return R.ok(result);
    }

    @Operation(summary = "根据空间ID查询文件列表")
    @GetMapping("/space/{spaceId}")
    public R<List<KnowledgeFile>> listBySpaceId(@Parameter(description = "空间ID") @PathVariable Long spaceId) {
        List<KnowledgeFile> result = knowledgeFileService.listBySpaceId(spaceId);
        return R.ok(result);
    }

    @Operation(summary = "批量上传文件")
    @PostMapping("/batch")
    public R<Boolean> batchCreate(@RequestBody List<KnowledgeFile> files) {
        Boolean result = knowledgeFileService.batchCreateKnowledgeFile(files);
        return R.ok(result);
    }

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public R<KnowledgeFileUploadTempDTO> uploadKnowledgeFile(@RequestParam("file") MultipartFile file, @RequestParam("knowledgeBaseId") Long knowledgeBaseId, @RequestParam("spaceId") Long spaceId) {
        KnowledgeFileUploadTempDTO result = knowledgeFileService.uploadKnowledgeFile(file, knowledgeBaseId, spaceId);
        return R.ok(result);
    }

    @Operation(summary = "获取文件临时信息")
    @GetMapping("/temp/{knowledgeBaseId}/{spaceId}/{createType}")
    public R<KnowledgeTempDTO> getKnowledgeTempFile(@Parameter(description = "知识库ID") @PathVariable Long knowledgeBaseId,
                                                    @Parameter(description = "空间ID") @PathVariable Long spaceId,
                                                    @Parameter(description = "createType") @PathVariable String createType) {
        KnowledgeTempDTO result = knowledgeFileService.getKnowledgeTempFile(knowledgeBaseId, spaceId, SecurityUtils.getUserId(), createType);
        return R.ok(result);
    }

    @Operation(summary = "创建文件临时信息")
    @PostMapping("/temp")
    public R<Boolean> createKnowledgeTempFile(@RequestBody KnowledgeTempDTO knowledgeTempDTO) {
        knowledgeFileService.createKnowledgeTempFile(knowledgeTempDTO);
        return R.ok(true);
    }

    @Operation(summary = "根据MD5删除临时文件")
    @DeleteMapping("/temp/{knowledgeBaseId}/{spaceId}/{fileMd5}")
    public R<Boolean> deleteTempFileByMd5(
            @Parameter(description = "知识库ID") @PathVariable Long knowledgeBaseId,
            @Parameter(description = "空间ID") @PathVariable Long spaceId,
            @Parameter(description = "文件MD5值") @PathVariable String fileMd5) {
        Boolean result = knowledgeFileService.deleteTempFileByMd5(knowledgeBaseId, spaceId, fileMd5);
        return R.ok(result);
    }

    @Operation(summary = "批量新增知识文件并返回ID列表", description = "将临时文件批量保存到知识库，返回生成的文件ID列表用于后续向量化处理")
    @PostMapping("/batch-create")
    public R<List<Long>> batchCreateWithIds(@RequestBody KnowledgeTempDTO knowledgeTempDTO) {
        List<Long> fileIds = knowledgeFileService.batchCreateKnowledgeFilesWithIds(knowledgeTempDTO);
        return R.ok(fileIds);
    }

    @Operation(summary = "批量新增知识文件并实时推送进度", description = "将临时文件批量保存到知识库，实时推送处理进度")
    @PostMapping(value = "/batch_create_with_progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> batchCreateWithProgress(@RequestBody KnowledgeTempDTO knowledgeTempDTO) {
        try {
            // 在跳过Spring Security的情况下，手动验证用户身份
            // 这里可以通过请求头中的token或者其他方式验证用户身份
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return Flux.just("data: {\"type\":\"error\",\"message\":\"用户未认证\",\"progress\":-1}\n\n");
            }

            // 验证用户对知识库的权限
            // 这里可以添加更多的权限检查逻辑

            return knowledgeFileService.batchCreateKnowledgeFilesWithProgress(knowledgeTempDTO);
        } catch (Exception e) {
            return Flux.just(String.format("data: {\"type\":\"error\",\"message\":\"权限验证失败: %s\",\"progress\":-1}\n\n", e.getMessage()));
        }
    }

    @Operation(summary = "批量从URL下载文件", description = "支持同时下载多个文件链接并创建临时文件记录")
    @PostMapping("/batch-download")
    public R<List<KnowledgeFileUploadTempDTO>> batchDownloadFiles(@RequestBody KnowledgeFileBatchDownloadDTO batchDownloadDTO) {
        List<KnowledgeFileUploadTempDTO> result = knowledgeFileService.batchDownloadFilesFromUrls(batchDownloadDTO);
        return R.ok(result);
    }

    @Operation(summary = "在线创建文件", description = "根据前端传过来的文件内容创建文件并上传")
    @PostMapping("/create-online")
    public R<KnowledgeFileUploadTempDTO> createFileOnline(@RequestBody KnowledgeFileOnlineCreateDTO createDTO) {
        KnowledgeFileUploadTempDTO result = knowledgeFileService.createKnowledgeFileFromContent(createDTO);
        return R.ok(result);
    }
}