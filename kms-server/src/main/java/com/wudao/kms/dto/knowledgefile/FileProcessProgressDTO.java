package com.wudao.kms.dto.knowledgefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件处理进度DTO
 * 用于跟踪文件在VL处理、QA提取、向量化三个阶段的进度
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件处理进度")
public class FileProcessProgressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "VL处理完成数量")
    @Builder.Default
    private Integer vlFinish = 0;

    @Schema(description = "QA提取完成数量")
    @Builder.Default
    private Integer qaFinish = 0;

    @Schema(description = "向量化完成数量")
    @Builder.Default
    private Integer vectorFinish = 0;

    /**
     * 重置所有进度为0
     */
    public void reset() {
        this.vlFinish = 0;
        this.qaFinish = 0;
        this.vectorFinish = 0;
    }

    /**
     * 设置所有进度为指定值
     */
    public void setAll(Integer value) {
        this.vlFinish = value;
        this.qaFinish = value;
        this.vectorFinish = value;
    }
}
