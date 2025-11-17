package com.wudao.kms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wudao.common.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统词库实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("system_dictionary")
@Schema(description = "系统词库")
public class SystemDictionary extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "词库类型：PROPER_NOUN-专有名词，SYNONYM-同义词，CORRECTION-纠错词，SENSITIVE-敏感词，SUGGESTION-搜索联想词")
    private String dictType;

    @Schema(description = "词库键（纠错词的错误词，其他类型的词本身）")
    private String dictKey;

    @Schema(description = "词库值（纠错词的正确词，同义词的同义词组，其他类型可为空）")
    private String dictValue;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态：1-启用，0-禁用")
    private Integer status;

    @Schema(description = "删除标记：false-未删除，true-已删除")
    private Boolean deleteFlag;

    @Schema(description = "空间id")
    private Long spaceId;

    /**
     * 词库类型枚举
     */
    public static class DictType {
        public static final String PROPER_NOUN = "PROPER_NOUN";
        public static final String SYNONYM = "SYNONYM";
        public static final String CORRECTION = "CORRECTION";
        public static final String SENSITIVE = "SENSITIVE";
        public static final String SUGGESTION = "SUGGESTION";
    }
} 