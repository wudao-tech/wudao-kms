package com.wudao.kms.vo;

import com.wudao.kms.entity.KnowledgeSpace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 知识库空间树形结构VO
 */
@Data
@Schema(description = "知识库空间树形结构")
public class KnowledgeSpaceTreeVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "知识库ID")
    private Long knowledgeBaseId;

    @Schema(description = "父级ID，0表示根目录")
    private Long parentId;

    @Schema(description = "空间名称")
    private String name;

    @Schema(description = "类型：1-文件夹，2-文件")
    private Integer type;

    @Schema(description = "完整路径")
    private String path;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "文件数量")
    private Integer fileCount;

    @Schema(description = "子空间列表")
    private List<KnowledgeSpaceTreeVO> children;

    /**
     * 从KnowledgeSpace转换为KnowledgeSpaceTreeVO
     */
    public static KnowledgeSpaceTreeVO fromEntity(KnowledgeSpace space) {
        if (space == null) {
            return null;
        }
        
        KnowledgeSpaceTreeVO vo = new KnowledgeSpaceTreeVO();
        vo.setId(space.getId());
        vo.setKnowledgeBaseId(space.getKnowledgeBaseId());
        vo.setParentId(space.getParentId());
        vo.setName(space.getName());
        vo.setType(space.getType());
        vo.setPath(space.getPath());
        vo.setLevel(space.getLevel());
        vo.setSortOrder(space.getSortOrder());
        vo.setFileCount(space.getFileCount());
        
        return vo;
    }
} 