package com.wudao.kms.chunk.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wsj
 * @description DTO
 * @date 2023/6/16
 */
@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@Data
public class FileChunkUploadDTO extends FileChunkUpload {
    private MultipartFile file;
}
