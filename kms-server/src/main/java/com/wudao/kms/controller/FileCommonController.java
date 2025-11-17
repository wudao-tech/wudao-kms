package com.wudao.kms.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.wudao.common.model.vo.R;
import com.wudao.oss.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.util.Date;

@RestController
@RequestMapping("/api/file-common")
@RequiredArgsConstructor
@Tag(name = "文件上传功能", description = "通用文档上传")
public class FileCommonController {

    private final OssService  ossService;

    /**
     * 文件上传接口
     * 支持AWS S3、MinIO、阿里云OSS、腾讯云COS等S3兼容存储
     *
     * @param file 上传的文件
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件到S3兼容存储")
    public R<String> uploadFile(@RequestParam MultipartFile file) {
        try {
            String key = "uploads/" + DateUtil.format(new Date(), "yyyy/MM/dd") + "/"
                    + IdUtil.fastSimpleUUID() + "_" + file.getOriginalFilename();
            // 使用S3Config上传文件
            ossService.putObject(file.getInputStream().readAllBytes(), key);
            // 生成文件访问URL
            String filePath = ossService.getHttpUrl(key);
            return R.ok(filePath);
        } catch (Exception e) {
            // 处理异常并返回错误信息
            return R.fail("文件上传失败: " + e.getMessage());
        }
    }
}
