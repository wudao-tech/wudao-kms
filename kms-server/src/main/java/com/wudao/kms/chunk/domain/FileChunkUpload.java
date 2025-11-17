package com.wudao.kms.chunk.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author wsj
 * @description 文件分片上传
 * @date 2023/6/15
 */
@Data
@TableName("ai_file_chunk_record")
@NoArgsConstructor
public class FileChunkUpload {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 文件md5 */
    private String fileMd5;

    /** 分片大小 */
    private Integer chunkSize;

    /** 分片数量 */
    private Integer chunkCount;

    /** 分片序号 */
    private Integer chunkIndex;

    /** 分片文件地址 */
    private String chunkFilePath;

    /** 文件名称 */
    private String fileName;

    /** 合并状态 */
    private Integer mergeStatus;
}
