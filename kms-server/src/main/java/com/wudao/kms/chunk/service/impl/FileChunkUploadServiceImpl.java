package com.wudao.kms.chunk.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wudao.common.utils.DateFormats;
import com.wudao.kms.chunk.domain.FileChunkUpload;
import com.wudao.kms.chunk.mapper.FileChunkUploadMapper;
import com.wudao.kms.chunk.service.FileChunkUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class FileChunkUploadServiceImpl extends ServiceImpl<FileChunkUploadMapper, FileChunkUpload> implements FileChunkUploadService {

    @Value("${basic.profile}")
    private String basicProfile;

    @Override
    public void saveFileChunk(FileChunkUpload chunkUpload, MultipartFile file) {
        try {
            String fileMd5 = chunkUpload.getFileMd5();
            Integer chunkIndex = chunkUpload.getChunkIndex();
            
            // 创建存储目录
            Path chunkDir = Paths.get(basicProfile, "uploads", "chunks", fileMd5);
            Files.createDirectories(chunkDir);
            
            // 分片文件路径
            String chunkFileName = fileMd5 + "_" + chunkIndex + ".chunk";
            Path chunkFilePath = chunkDir.resolve(chunkFileName);
            
            // 保存分片文件
            file.transferTo(chunkFilePath.toFile());
            
            // 保存分片记录到数据库
            chunkUpload.setChunkFilePath(chunkFilePath.toString());
            chunkUpload.setMergeStatus(0);
            this.save(chunkUpload);
            
            log.info("分片上传成功: {}, 分片序号: {}", fileMd5, chunkIndex);
            
        } catch (IOException e) {
            log.error("分片上传失败", e);
            throw new RuntimeException("分片上传失败: " + e.getMessage());
        }
    }

    @Override
    public String mergeFile(String fileMd5) {
        try {
            // 查询所有分片记录
            List<FileChunkUpload> chunkList = this.list(
                Wrappers.lambdaQuery(FileChunkUpload.class)
                    .eq(FileChunkUpload::getFileMd5, fileMd5)
                    .orderByAsc(FileChunkUpload::getChunkIndex)
            );
            
            if (chunkList.isEmpty()) {
                throw new RuntimeException("未找到分片文件: " + fileMd5);
            }
            
            // 验证分片完整性
            FileChunkUpload firstChunk = chunkList.get(0);
            if (chunkList.size() != firstChunk.getChunkCount()) {
                this.removeByIds(chunkList.stream().map(FileChunkUpload::getId).toList());
                throw new RuntimeException("分片数量不完整");
            }
            
            // 创建合并目录
            Path mergeDir = Paths.get(basicProfile, "uploads", "merged",LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss));
            Files.createDirectories(mergeDir);
            
            // 合并文件路径
            String mergedFileName = firstChunk.getFileName();
            Path mergedFilePath = mergeDir.resolve(mergedFileName);
            
            // 合并分片文件
            try (FileOutputStream fos = new FileOutputStream(mergedFilePath.toFile());
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                
                for (FileChunkUpload chunk : chunkList) {
                    File chunkFile = new File(chunk.getChunkFilePath());
                    if (!chunkFile.exists()) {
                        throw new RuntimeException("分片文件不存在: " + chunk.getChunkFilePath());
                    }
                    
                    try (FileInputStream fis = new FileInputStream(chunkFile);
                         BufferedInputStream bis = new BufferedInputStream(fis)) {
                        
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
            
            // 更新合并状态
            this.update(
                Wrappers.lambdaUpdate(FileChunkUpload.class)
                    .eq(FileChunkUpload::getFileMd5, fileMd5)
                    .set(FileChunkUpload::getMergeStatus, 1)
            );
            
            // 删除分片文件和记录
            cleanupChunks(fileMd5, chunkList);
            
            log.info("文件合并成功: {}", mergedFilePath);
            return mergedFilePath.toString();
            
        } catch (IOException e) {
            log.error("文件合并失败", e);
            throw new RuntimeException("文件合并失败: " + e.getMessage());
        }
    }

    @Override
    public void cancelMergeFile(String fileMd5) {
        // 查询分片记录
        List<FileChunkUpload> chunkList = this.list(
            Wrappers.lambdaQuery(FileChunkUpload.class)
                .eq(FileChunkUpload::getFileMd5, fileMd5)
        );
        
        // 删除分片文件和记录
        cleanupChunks(fileMd5, chunkList);
        
        log.info("取消合并成功: {}", fileMd5);
    }

    private void cleanupChunks(String fileMd5, List<FileChunkUpload> chunkList) {
        // 删除分片文件
        for (FileChunkUpload chunk : chunkList) {
            try {
                File chunkFile = new File(chunk.getChunkFilePath());
                if (chunkFile.exists()) {
                    chunkFile.delete();
                }
            } catch (Exception e) {
                log.warn("删除分片文件失败: {}", chunk.getChunkFilePath(), e);
            }
        }
        
        // 删除分片目录
        try {
            Path chunkDir = Paths.get(basicProfile, "uploads", "chunks", fileMd5);
            if (Files.exists(chunkDir)) {
                Files.deleteIfExists(chunkDir);
            }
        } catch (IOException e) {
            log.warn("删除分片目录失败: {}", fileMd5, e);
        }
        
        // 删除数据库记录
        this.remove(Wrappers.lambdaQuery(FileChunkUpload.class).eq(FileChunkUpload::getFileMd5, fileMd5));
    }
}