package com.wudao.kms.vo;

import com.wudao.kms.entity.SystemDictionary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统词库VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "系统词库VO")
public class SystemDictionaryVO extends SystemDictionary {

    @Schema(description = "创建人姓名")
    private String createdByName;

    @Schema(description = "修改人姓名")
    private String updatedByName;

    @Schema(description = "词库类型名称")
    private String dictTypeName;
}