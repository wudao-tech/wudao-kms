# Change Log - wudao-kms 开源版本更新

## 版本: 1.0.2
## 更新日期: 2026-01-27

---

## 一、依赖版本更新

| 依赖项 | 旧版本 | 新版本 |
|--------|--------|--------|
| wudao-parent | 1.0.0 | 1.0.2 |
| spring-ai | 1.0.0 | 1.1.2 |
| spring-ai-alibaba | 1.0.0.3 | 1.1.0.0-RC2 |
| spring-ai-starter-model-zhipuai | 1.0.3 | 1.1.2 |
| spring-ai-starter-model-ollama | 1.0.3 | 1.1.2 |

### 新增依赖
- `com.aliyun:alibabacloud-pai_dlc20201203:1.2.26` - 阿里云PAI-DLC深度学习容器服务

### 移除依赖
- `org.springaicommunity:moonshot-core:1.1.0-SNAPSHOT` - 月之暗面模型支持（改为使用通用OpenAI兼容方式）

---

## 二、新增功能模块

### 2.1 Agent 智能助手模块 (后端)
**路径**: `kms-server/src/main/java/com/wudao/kms/agent/`

新增文件 (100+ 个):
- `AgentController.java` - Agent管理控制器
- `ApiAgentController.java` - Agent API控制器
- `node/` - Agent节点定义
  - `Assistant*.java` - 智能助手相关实体和服务
  - `Prompt*.java` - 提示词管理
  - `Tool*.java` - 工具注册和管理
  - `MessageNode*.java` - 消息节点服务
  - `ConversationView*.java` - 对话视图

### 2.2 Dify 集成模块 (后端)
**路径**: `kms-server/src/main/java/com/wudao/kms/dify/`

新增文件:
- `DifyRetrievalController.java` - Dify检索控制器
- `DifyRetrievalService.java` - Dify检索服务
- `DifyRetrievalRequest.java` / `DifyRetrievalResponse.java` - 请求响应DTO
- `DifyRecord.java` / `DifyMetadataCondition.java` - 数据模型

### 2.3 Token 配额管理模块 (后端)
**路径**: `kms-server/src/main/java/com/wudao/kms/llm/token/`

新增文件:
- `TokenQuotaService.java` - Token配额检查服务
- `TokenUsageLogService.java` - Token使用记录服务
- `TokenUsageLog.java` - Token使用日志实体
- `TokenUsageLogMapper.java` - Mapper
- `TokenQuotaResp.java` - 配额响应DTO
- `QuotaExceededException.java` - 配额超限异常

### 2.4 模型厂商管理模块 (后端)
**路径**: `kms-server/src/main/java/com/wudao/kms/llm/provider/`

新增文件:
- `ModelProviderController.java` - 厂商管理控制器
- `ModelProviderService.java` - 厂商管理服务
- `ModelProvider.java` - 厂商实体
- `ModelProviderMapper.java` - Mapper
- `ModelProviderListResp.java` - 列表响应DTO

### 2.5 工具类模块 (后端)
**路径**: `kms-server/src/main/java/com/wudao/kms/util/`

新增文件:
- `pai/` - 阿里云PAI-DLC集成工具

### 2.6 其他后端新增
- `DeepThinkingService.java` - 深度思考服务
- `QwenAiService.java` - 通义千问AI服务
- `WorkflowTool.java` / `WorkflowToolStrategy.java` - 工作流工具
- `llm/chat/advisor/` - Chat Advisor模块

### 2.7 前端新增模块

| 模块 | 路径 | 说明 |
|------|------|------|
| 厂商管理 | `web/src/views/manufacturer/` | 模型厂商配置界面 |
| API密钥 | `web/src/views/system/apiKey/` | API密钥管理界面 |
| API接口 | `web/src/api/manufacturer.js` | 厂商管理API |
| API接口 | `web/src/api/system/apiKey/` | API密钥管理API |
| 图标资源 | `web/src/assets/fileIcon/qa.svg` | QA图标 |
| 图标资源 | `web/src/assets/images/copy.svg` | 复制图标 |
| 图标资源 | `web/src/assets/images/redo.svg` | 重做图标 |

---

## 三、数据库变更 (SQL)

### 3.1 新增表

#### token_usage_log - Token使用记录表
```sql
create table public.token_usage_log
(
    id              bigserial primary key,
    user_id         bigint       not null,           -- 用户ID
    operation_type  varchar(20)  not null,           -- 操作类型：CHAT/EMBEDDING/RERANK/ASR
    model           varchar(64)  not null,           -- 模型名称
    input_tokens    integer      default 0,          -- 输入token数
    output_tokens   integer      default 0,          -- 输出token数
    total_tokens    integer      default 0,          -- 总token数
    latency_ms      integer,                         -- 耗时（毫秒）
    status          varchar(20)  default 'SUCCESS',  -- 状态：SUCCESS/FAILED
    error_message   varchar(500),                    -- 错误信息
    created_by      varchar(100),
    created_at      timestamp(6) default CURRENT_TIMESTAMP,
    updated_by      varchar(100),
    updated_at      timestamp(6) default CURRENT_TIMESTAMP,
    remark          text,
    del_flag        boolean      default false not null,
    dept_id         integer
);

-- 索引
create index idx_token_usage_log_user_id on public.token_usage_log (user_id);
create index idx_token_usage_log_operation_type on public.token_usage_log (operation_type);
create index idx_token_usage_log_created_at on public.token_usage_log (created_at);
```

#### provider - 模型厂商配置表
```sql
create table public.provider
(
    id                bigserial primary key,
    name              varchar(100) not null,         -- 厂商名称
    provider_code     varchar(40)  not null,         -- 厂商代码：qwen/deepseek/zhipu/moonshot/openai/ollama
    type              varchar(50),                   -- 厂商类型
    api_key           varchar(500),                  -- API密钥
    endpoint          varchar(500),                  -- 端点地址/baseUrl
    completions_path  varchar(200),                  -- API路径后缀
    status            varchar(20),                   -- 连接测试状态
    created_by        varchar(100),
    created_at        timestamp(6) default CURRENT_TIMESTAMP,
    updated_by        varchar(100),
    updated_at        timestamp(6) default CURRENT_TIMESTAMP,
    remark            text,
    del_flag          boolean      default false not null,
    dept_id           integer
);

-- 唯一索引
create unique index uk_provider_code on public.provider (provider_code) where (del_flag = false);
```

### 3.2 表结构变更

#### knowledge_file 表新增字段
```sql
ALTER TABLE knowledge_file ADD COLUMN IF NOT EXISTS process_progress JSONB
    DEFAULT '{"vl_finish":0,"qa_finish":0,"vector_finish":0}'::jsonb;
COMMENT ON COLUMN knowledge_file.process_progress IS '文件处理进度，JSON格式';
```

#### qa_improve 表新增字段
```sql
ALTER TABLE qa_improve ADD COLUMN IF NOT EXISTS feedback_id bigint;
COMMENT ON COLUMN qa_improve.feedback_id IS '关联的反馈ID';
CREATE INDEX IF NOT EXISTS idx_qa_improve_feedback_id ON qa_improve(feedback_id);
```

### 3.3 wudao-parent 新增表 (sys_api_key)
```sql
create table sys_api_key
(
    id           bigserial    not null,
    secret_key   char(32)     not null,              -- 密钥
    expiry_time  timestamp,                          -- 过期时间
    last_used_at timestamp,                          -- 最后使用时间
    remark       varchar(255) null,
    status       smallint     not null default 0,    -- 状态（0正常 1停用）
    updated_at   timestamp    not null default current_timestamp,
    updated_by   bigint       not null default 0,
    created_at   timestamp    not null default current_timestamp,
    created_by   bigint       not null default 0,
    dept_id      bigint       not null default 0,
    primary key (id)
);
```

---

## 四、配置变更

### 4.1 系统配置项 (sys_config 表)

| 配置键 | 说明 | 示例值 |
|--------|------|--------|
| `daily.max.token` | 每日Token使用上限 | 1000000 |

### 4.2 配置说明
- Token配额通过 `sys_config` 表的 `daily.max.token` 配置项控制
- 用户每日Token使用达到上限后将抛出 `QuotaExceededException`

---

## 五、修改的文件列表

### 5.1 后端修改 (41个文件)

**Controller层**:
- `ChatController.java`
- `FeedbackController.java`
- `KnowledgeFileController.java`
- `KnowledgeFileSegmentController.java`
- `QaImproveController.java`

**Service层**:
- `ChatService.java`
- `ChatFileUploadService.java`
- `ChatSessionQaService.java`
- `CoverGenerationService.java`
- `FavoriteRecordService.java`
- `FeedbackService.java`
- `KnowledgeBaseService.java`
- `KnowledgeFileService.java`
- `KnowledgeFileSegmentService.java`
- `MarkdownSplitService.java`
- `QaImproveService.java`
- `SearchHistoryService.java`
- `VectorizationService.java`

**LLM模块**:
- `ChatModelStrategy.java`
- `KnowledgeChatController.java`
- `DashScopeStrategy.java`
- `DeepSeekStrategy.java`
- `MoonshotChatModelStrategy.java`
- `OllamaStrategy.java`
- `ZhipuAiChatModelStrategy.java`
- `ModelTypeEnum.java`
- `LLMModelMapper.java`
- `ChangeMediaTool.java`

**DTO/Entity**:
- `KnowledgeBaseQueryDTO.java`
- `KnowledgeFilePageDTO.java`
- `KnowledgeSearchResult.java`
- `Feedback.java`
- `KnowledgeFile.java`
- `QaImprove.java`

**其他**:
- `CommentNotificationAspect.java`
- `DocumentToMarkdownTool.java`
- `MarkdownSplitter.java`
- `MessageUtils.java`
- `WebClientConfig.java`
- `MinerUService.java`

### 5.2 前端修改 (30个文件)

**API层**:
- `api/base/index.js`
- `api/model/index.js`
- `api/system/notice/index.ts`

**组件**:
- `components/Organization/index.vue`
- `components/RetrieveConfig.vue`
- `components/WdAgent.vue`
- `components/WdCard/index.vue`
- `components/comment/index.vue`
- `components/noticeDrawer/index.vue`

**路由**:
- `router/index.ts`

**视图**:
- `views/index.vue`
- `views/knowledge/index.vue`
- `views/knowledge/components/detail.vue`
- `views/knowledge/components/fileUpload.vue`
- `views/knowledge/components/hittesting.vue`
- `views/knowledge/components/parseMd.vue`
- `views/knowledge/components/qapair.vue`
- `views/modelOpt/index.vue`
- `views/qa/index.vue`
- `views/retrieve/index.vue`
- `views/retrieve/detail.vue`
- `views/specialist/config.vue`
- `views/specialist/detail.vue`
- `views/system/role/index.vue`
- `views/system/tenant/index.vue`
- `views/system/user/profile/index.vue`
- `views/system/user/profile/onlineDevice.vue`

---

## 六、升级步骤

1. **更新 wudao-parent 到 1.0.2**
   ```bash
   # 确保 wudao-parent 1.0.2 已发布到 Maven 仓库
   ```

2. **执行数据库迁移**
   ```sql
   -- 1. 创建 token_usage_log 表
   -- 2. 创建 provider 表
   -- 3. 修改 knowledge_file 表
   -- 4. 修改 qa_improve 表
   -- 5. 创建 sys_api_key 表 (wudao-parent)
   ```

3. **配置 Token 配额**
   ```sql
   INSERT INTO sys_config (code, value, name, type)
   VALUES ('daily.max.token', '1000000', '每日Token上限', 0);
   ```

4. **重新构建项目**
   ```bash
   # 后端
   mvn clean compile -DskipTests

   # 前端
   npm install && npm run build:prod
   ```

---

## 七、注意事项

1. **API密钥模块**: 需要配合 wudao-parent 1.0.2 使用，包含 `sys_api_key` 表和相关服务
2. **Token配额**: 通过 `sys_config` 表配置 `daily.max.token`，超限会抛出异常
3. **模型厂商**: 新增 `provider` 表支持多厂商配置，包括 API Key、Endpoint 等
4. **Moonshot支持**: 移除了独立的 moonshot-core 依赖，改为通过 OpenAI 兼容方式调用
