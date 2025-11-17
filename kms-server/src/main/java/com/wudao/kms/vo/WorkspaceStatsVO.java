package com.wudao.kms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 工作台统计VO
 */
@Data
@Accessors(chain = true)
@Schema(description = "工作台统计VO")
public class WorkspaceStatsVO {
    @Schema(description = "被访问量")
    private Long visitedByCount;
    @Schema(description = "被收藏量")
    private Long favoriteByCount;
    @Schema(description = "被评论量")
    private Long commentByCount;
}