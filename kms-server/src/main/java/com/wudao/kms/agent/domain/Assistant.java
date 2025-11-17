package com.wudao.kms.agent.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.wudao.common.model.BaseEntity;
import com.wudao.kms.agent.dto.AssistantQueryFollowUpDTO;
import com.wudao.kms.agent.dto.AssistantQuickCommandDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * AI助手实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "ai_assistant", autoResultMap = true)
public class Assistant extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String uuid;
    private String name;
    private String logo;
    private String description;
    private String systemPrompt;
    private String prompt;
    private String guideWord;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> guideQuestions;
    @Schema(description = "模型名称，如gpt-3.5-turbo")
    private String modelName;
    private Double temperature;
    private Integer dialogRound;
    private Integer maxToken;
    private Integer status;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Object> flowList;
    private Long knowledgeList;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> knowledgeSpaceList;
    @Schema(description = "业务环节ID，关联字典表")
    private String subjectId;
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "标签")
    private List<String> tags;
    @Schema(description = "权限：1-公开，2-私有")
    private Integer permission;
    @Schema(description = "知识库检索类型：语义检索:semantic, 全文检索:fulltext, 混合检索:hybrid")
    private String knowledgeSearchType;
    @Schema(description = "最大召回字数")
    private Integer maxRecallCount;
    @Schema(description = "最大召回分段")
    private Integer maxSegmentCount;
    @Schema(description = "重排模型ID")
    private String rerankModelId;
    @Schema(description = "追问内容")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private AssistantQueryFollowUpDTO followup;
    @Schema(description = "背景图片")
    private String bgImg;
    @Schema(description = "快捷指令")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<AssistantQuickCommandDTO> quickCommands;
    private Boolean deepThinkingModel;
    private Boolean search;
    private Boolean multimodal;
    @Schema(description = "推荐")
    private Boolean recommend;
    @Schema(description = "活跃")
    private Integer active = 0;
    @Schema(description = "收藏")
    private Integer collect = 0;
    @Schema(description = "topP")
    private Double topP;
    @Schema(description = "是否重排")
    private Boolean isRerank = false;
    @Schema(description = "重排模型")
    private String rerankModel;
    @Schema(description = "重排分")
    private Double rerankScore;
    @Schema(description = "重排top")
    private Integer rerankTopK;
    @Schema(description = "rag检索分")
    private Double ragScore;
    @Schema(description = "rag topK")
    private Integer ragTopK;
    @Schema(description = "工作流ids")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> workflowUuids;
}