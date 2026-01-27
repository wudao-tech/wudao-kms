package com.wudao.kms.llm.llmmode.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模型类型枚举
 *
 * @author wudao
 */
@Getter
@AllArgsConstructor
@Schema(description = "模型类型枚举")
public enum ModelTypeEnum {
    @Schema(description = "文本理解")
    text_generation("text_generation", "文本生成"),
    @Schema(description = "深度推理")
    deep_reasoning("deep_reasoning", "深度推理"),
    @Schema(description = "多模态")
    multimodal("multimodal", "多模态"),
    @Schema(description = "图像理解")
    image_understanding("image_understanding", "图像理解"),
    @Schema(description = "音频理解")
    audio_understanding("audio_understanding", "音频理解"),
    @Schema(description = "视频理解")
    video_understanding("video_understanding", "视频理解"),
    @Schema(description = "图像生成")
    image_generation("image_generation", "图像生成"),
    @Schema(description = "视频生成")
    video_generation("video_generation", "视频生成"),
    @Schema(description = "向量模型")
    embedding_model("embedding_model", "向量模型"),
    @Schema(description = "检索重排")
    text_rerank("text_rerank", "检索重排");
    @EnumValue
    private final String code;
    private final String desc;
}