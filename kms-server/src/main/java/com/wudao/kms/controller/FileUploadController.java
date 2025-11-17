package com.wudao.kms.controller;

import cn.hutool.core.io.FileUtil;
import com.wudao.common.model.vo.R;
import com.wudao.common.utils.DateFormats;
import com.wudao.kms.dto.knowledgefile.KnowledgeFileUploadTempDTO;
import com.wudao.oss.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.time.LocalDateTime;

@Tag(name = "文件上传管理", description = "文件上传相关接口")
@RestController
@RequestMapping("/api/file-upload")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {


    private final OssService ossService;

    @Operation(summary = "单文件上传", description = "上传单个文件到指定知识库")
    @PostMapping("/single")
    public R<KnowledgeFileUploadTempDTO> uploadSingleFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "知识库ID") @RequestParam("knowledgeBaseId") Long knowledgeBaseId) {
        
        try {
            if (file.isEmpty()) {
                return R.fail("文件不能为空");
            }

            String ossFilePath = String.format("knowledge:%s%s%s",knowledgeBaseId, LocalDateTime.now()
                    .format(DateFormats.yyyyMMddHHmmss), file.getOriginalFilename());
            
            // 上传知识库文件
            ossService.putObject((FileInputStream) file.getInputStream(), ossFilePath);
            
            // 构建返回结果
            KnowledgeFileUploadTempDTO result = new KnowledgeFileUploadTempDTO();
            result.setFileName(file.getOriginalFilename());
            result.setS3Path(ossFilePath);
            result.setFileSize(file.getSize());
            result.setFileType(FileUtil.getSuffix(file.getName()));
            result.setFileMd5(DigestUtils.md5DigestAsHex(file.getBytes()));
            result.setParseResult("解析中");
            result.setSummary("暂未生成摘要");
            result.setTags("文档");
            
            return R.ok(result);
            
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return R.fail("文件上传失败: " + e.getMessage());
        }
    }
} 