package com.wudao.kms.agent.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 更新助手请求DTO
 */
@Data
@Schema(description = "更新助手请求DTO")
public class AssistantUpdateRequest {
    @Schema(description = "助手ID")
    private String uuid;
    @Schema(description = "助手名称")
    private String name;
    @Schema(description = "助手logo")
    private String logo;
    @Schema(description = "助手描述")
    private String description;
    @Schema(description = "助手系统提示词")
    private String systemPrompt;
    @Schema(description = "知识库列表")
    private Long knowledgeList;
    @Schema(description = "知识空间")
    private List<Long> knowledgeSpaceList;
    @Schema(description = "助手提示词")
    private String prompt;
    @Schema(description = "助手引导词")
    private String guideWord;
    @Schema(description = "助手引导问题")
    private List<String> guideQuestions;
    @Schema(description = "助手模型名称")
    private String modelName;
    @Schema(description = "助手温度")
    private Double temperature;
    @Schema(description = "助手最大token")
    private Integer maxToken;
    @Schema(description = "助手对话轮次")
    private Integer dialogRound;
    @Schema(description = "助手状态")
    private Integer status;
    @Schema(description = "助手工具")
    private List<AssistantTool> tools;
    @Schema(description = "agent组件uuid")
    private List<String> agentComponents;
    @Schema(description = "业务环节ID，关联字典表")
    private String businessCategoryId;
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
    @Schema(description = "记忆变量")
    private List<AiAssistantMemoryDTO> memoryVars;
    @Schema(description = "记忆表")
    private List<AiAssistantMemoryTableDTO>  memoryTable;
    @Schema(description = "追问内容")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private AssistantQueryFollowUpDTO followup;
    @Schema(description = "数据库")
    private AssistantDbRequest  datasource;
    @Schema(description = "背景图片")
    private String bgImg;
    @Schema(description = "深度思考")
    private Boolean deepThinkingModel;
    @Schema(description = "文件上传")
    private Boolean multimodal;
    @Schema(description = "联网搜索")
    private Boolean search;
    @Schema(description = "快捷指令")
    private List<AssistantQuickCommandDTO> quickCommands;
    @Schema(description = "推荐")
    private Boolean recommend;
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
    @Schema(description = "工作流uuids")
    private List<String> workflowUuids;
    private String subjectId;
}