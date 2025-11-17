package com.wudao.kms.chunk.controller;

import com.wudao.common.model.vo.R;
import com.wudao.kms.chunk.domain.FileChunkUpload;
import com.wudao.kms.chunk.service.FileChunkUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wsj
 * @description 接口映射层
 * @date 2023/6/16
 */
@RestController
@RequestMapping("/chunk")
@Slf4j
@Tag(name = "文件分片上传", description = "文件分片上传相关接口")
public class FileChunkUploadController {
    @Resource
    private FileChunkUploadService uploadService;

    @PostMapping("/chunkUpload")
    @Operation(summary = "分片上传文件", description = "接收文件分片并保存")
    public R chunkUpload(@RequestParam("file") MultipartFile file, FileChunkUpload chunkUpload){
        uploadService.saveFileChunk(chunkUpload, file);
        return R.ok();
    }

    @Operation(summary = "合并文件分片", description = "合并已上传的文件分片")
    @GetMapping("/mergeFile")
    public R mergeFile(String fileMd5){
        return R.ok(uploadService.mergeFile(fileMd5));
    }

    @GetMapping("/cancelMergeFile")
    @Operation(summary = "取消文件分片合并", description = "取消正在进行的文件分片合并操作")
    public R cancelMergeFile(String fileMd5){
        uploadService.cancelMergeFile(fileMd5);
        return R.ok();
    }
}
