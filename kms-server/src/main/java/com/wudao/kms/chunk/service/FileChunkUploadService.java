package com.wudao.kms.chunk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wudao.kms.chunk.domain.FileChunkUpload;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wsj
 * @description 文件分片上传业务层
 * @date 2023/6/15
 */
public interface FileChunkUploadService extends IService<FileChunkUpload> {

    /**
     * 保存分片文件
     * @param chunkUpload 分片对象
     * @param file 分片文件内容
     */
    void saveFileChunk(FileChunkUpload chunkUpload, MultipartFile  file);

    /**
     * 合并分片文件
     * @param fileMd5 文件独有的MD5
     * @return 文件存储路径
     */
    String mergeFile(String fileMd5);

    /**
     * 取消合并分片文件
     * @param fileMd5 文件独有的MD5
     */
    void cancelMergeFile(String fileMd5);
}
