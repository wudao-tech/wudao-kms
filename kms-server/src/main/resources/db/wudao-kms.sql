
-- ----------------------------
-- Sequence structure for agent_message_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."agent_message_id_seq";
CREATE SEQUENCE "public"."agent_message_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."agent_message_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for agent_session_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."agent_session_id_seq";
CREATE SEQUENCE "public"."agent_session_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."agent_session_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for ai_assistant_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_assistant_id_seq";
CREATE SEQUENCE "public"."ai_assistant_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."ai_assistant_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for ai_feedback_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_feedback_id_seq";
CREATE SEQUENCE "public"."ai_feedback_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."ai_feedback_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for ai_file_chunk_record_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ai_file_chunk_record_id_seq";
CREATE SEQUENCE "public"."ai_file_chunk_record_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."ai_file_chunk_record_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for chat_file_upload_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."chat_file_upload_id_seq";
CREATE SEQUENCE "public"."chat_file_upload_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."chat_file_upload_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for chat_session_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."chat_session_id_seq";
CREATE SEQUENCE "public"."chat_session_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."chat_session_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for chat_session_qa_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."chat_session_qa_id_seq";
CREATE SEQUENCE "public"."chat_session_qa_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."chat_session_qa_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for comment_record_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."comment_record_id_seq";
CREATE SEQUENCE "public"."comment_record_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."comment_record_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for favorite_record_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."favorite_record_id_seq";
CREATE SEQUENCE "public"."favorite_record_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."favorite_record_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for file_upload_record_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."file_upload_record_id_seq";
CREATE SEQUENCE "public"."file_upload_record_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."file_upload_record_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for knowledge_base_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."knowledge_base_id_seq";
CREATE SEQUENCE "public"."knowledge_base_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."knowledge_base_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for knowledge_base_permission_application_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."knowledge_base_permission_application_id_seq";
CREATE SEQUENCE "public"."knowledge_base_permission_application_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."knowledge_base_permission_application_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for knowledge_base_permission_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."knowledge_base_permission_id_seq";
CREATE SEQUENCE "public"."knowledge_base_permission_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."knowledge_base_permission_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for knowledge_file_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."knowledge_file_id_seq";
CREATE SEQUENCE "public"."knowledge_file_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."knowledge_file_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for knowledge_file_segment_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."knowledge_file_segment_id_seq";
CREATE SEQUENCE "public"."knowledge_file_segment_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."knowledge_file_segment_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for knowledge_space_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."knowledge_space_id_seq";
CREATE SEQUENCE "public"."knowledge_space_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."knowledge_space_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for knowledge_tag_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."knowledge_tag_id_seq";
CREATE SEQUENCE "public"."knowledge_tag_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."knowledge_tag_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for knowledge_vector_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."knowledge_vector_id_seq";
CREATE SEQUENCE "public"."knowledge_vector_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."knowledge_vector_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for llm_model_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."llm_model_id_seq";
CREATE SEQUENCE "public"."llm_model_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."llm_model_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for miner_u_task_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."miner_u_task_id_seq";
CREATE SEQUENCE "public"."miner_u_task_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."miner_u_task_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for notification_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."notification_id_seq";
CREATE SEQUENCE "public"."notification_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."notification_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for qa_improve_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."qa_improve_id_seq";
CREATE SEQUENCE "public"."qa_improve_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."qa_improve_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for search_dictionary_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."search_dictionary_id_seq";
CREATE SEQUENCE "public"."search_dictionary_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."search_dictionary_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for search_history_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."search_history_id_seq";
CREATE SEQUENCE "public"."search_history_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."search_history_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for search_logs_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."search_logs_id_seq";
CREATE SEQUENCE "public"."search_logs_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."search_logs_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for search_suggestion_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."search_suggestion_id_seq";
CREATE SEQUENCE "public"."search_suggestion_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."search_suggestion_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_comment_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_comment_id_seq";
CREATE SEQUENCE "public"."sys_comment_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_comment_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_comment_reply_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_comment_reply_id_seq";
CREATE SEQUENCE "public"."sys_comment_reply_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_comment_reply_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_dept_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_dept_id_seq";
CREATE SEQUENCE "public"."sys_dept_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_dept_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_dict_data_code_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_dict_data_code_seq";
CREATE SEQUENCE "public"."sys_dict_data_code_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_dict_data_code_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_job_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_job_id_seq";
CREATE SEQUENCE "public"."sys_job_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_job_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_job_log_job_log_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_job_log_job_log_id_seq";
CREATE SEQUENCE "public"."sys_job_log_job_log_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_job_log_job_log_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_menu_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_menu_id_seq";
CREATE SEQUENCE "public"."sys_menu_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_menu_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_notice_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_notice_id_seq";
CREATE SEQUENCE "public"."sys_notice_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_notice_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_post_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_post_id_seq";
CREATE SEQUENCE "public"."sys_post_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_post_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_role_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_role_id_seq";
CREATE SEQUENCE "public"."sys_role_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_role_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for sys_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_user_id_seq";
CREATE SEQUENCE "public"."sys_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."sys_user_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for system_dictionary_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."system_dictionary_id_seq";
CREATE SEQUENCE "public"."system_dictionary_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."system_dictionary_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for visit_record_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."visit_record_id_seq";
CREATE SEQUENCE "public"."visit_record_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;
ALTER SEQUENCE "public"."visit_record_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for agent_message
-- ----------------------------
DROP TABLE IF EXISTS "public"."agent_message";
CREATE TABLE "public"."agent_message" (
  "id" int8 NOT NULL DEFAULT nextval('agent_message_id_seq'::regclass),
  "session_uuid" varchar(32) COLLATE "pg_catalog"."default",
  "user_message" text COLLATE "pg_catalog"."default",
  "agent" text COLLATE "pg_catalog"."default",
  "updated_at" timestamp(6),
  "updated_by" int2,
  "created_at" timestamp(6),
  "created_by" int2,
  "dept_id" int4,
  "quote_list" jsonb,
  "feedback_type" varchar(10) COLLATE "pg_catalog"."default",
  "chat_uuid" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."agent_message" OWNER TO "postgres";
COMMENT ON COLUMN "public"."agent_message"."quote_list" IS '引用List';
COMMENT ON COLUMN "public"."agent_message"."feedback_type" IS '反馈类型 good/bad';
COMMENT ON COLUMN "public"."agent_message"."chat_uuid" IS '消息uuid';

-- ----------------------------
-- Records of agent_message
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for agent_session
-- ----------------------------
DROP TABLE IF EXISTS "public"."agent_session";
CREATE TABLE "public"."agent_session" (
  "id" int8 NOT NULL DEFAULT nextval('agent_session_id_seq'::regclass),
  "session_name" varchar(200) COLLATE "pg_catalog"."default",
  "uuid" varchar(32) COLLATE "pg_catalog"."default",
  "updated_at" timestamp(6),
  "updated_by" int2,
  "created_at" timestamp(6),
  "created_by" int2,
  "dept_id" int4,
  "agent_uuid" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."agent_session" OWNER TO "postgres";
COMMENT ON COLUMN "public"."agent_session"."agent_uuid" IS 'agent的id';
COMMENT ON TABLE "public"."agent_session" IS 'agent session';

-- ----------------------------
-- Records of agent_session
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ai_assistant
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_assistant";
CREATE TABLE "public"."ai_assistant" (
  "id" int8 NOT NULL DEFAULT nextval('ai_assistant_id_seq'::regclass),
  "uuid" varchar(32) COLLATE "pg_catalog"."default" NOT NULL DEFAULT replace((gen_random_uuid())::text, '-'::text, ''::text),
  "name" varchar(100) COLLATE "pg_catalog"."default",
  "logo" varchar(500) COLLATE "pg_catalog"."default",
  "description" text COLLATE "pg_catalog"."default",
  "system_prompt" text COLLATE "pg_catalog"."default",
  "prompt" text COLLATE "pg_catalog"."default",
  "guide_word" varchar(500) COLLATE "pg_catalog"."default",
  "guide_questions" text COLLATE "pg_catalog"."default",
  "model_name" varchar(50) COLLATE "pg_catalog"."default",
  "temperature" numeric(3,2) DEFAULT 0.5,
  "max_token" int4 DEFAULT 32000,
  "status" int4 DEFAULT 0,
  "user_id" int8,
  "is_delete" int4 DEFAULT 0,
  "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "tools" text COLLATE "pg_catalog"."default",
  "flow_list" text COLLATE "pg_catalog"."default",
  "knowledge_list" int2,
  "created_by" int4,
  "updated_by" int4,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "workflow_uuid" varchar(50) COLLATE "pg_catalog"."default",
  "dialog_round" int4,
  "agent_components" varchar(200) COLLATE "pg_catalog"."default",
  "subject_id" varchar(30) COLLATE "pg_catalog"."default",
  "tags" varchar(200) COLLATE "pg_catalog"."default",
  "permission" int4 DEFAULT 1,
  "knowledge_search_type" varchar(20) COLLATE "pg_catalog"."default",
  "max_recall_count" int4,
  "max_segment_count" int4,
  "rerank_model_id" varchar(30) COLLATE "pg_catalog"."default",
  "followup" jsonb,
  "bg_img" varchar(200) COLLATE "pg_catalog"."default",
  "quick_commands" jsonb,
  "shelves_status" varchar(5) COLLATE "pg_catalog"."default",
  "graph_config" jsonb,
  "deep_thinking_model" bool,
  "search" bool,
  "multimodal" bool,
  "dept_id" int4,
  "knowledge_space_list" jsonb,
  "recommend" bool,
  "active" int2,
  "collect" int2,
  "top_p" numeric,
  "is_rerank" bool,
  "rerank_model" varchar(100) COLLATE "pg_catalog"."default",
  "rerank_score" numeric,
  "rerank_top_k" int2,
  "rag_score" numeric,
  "rag_top_k" int2,
  "workflow_uuids" jsonb,
  "del_flag" bool DEFAULT false
)
;
ALTER TABLE "public"."ai_assistant" OWNER TO "postgres";
COMMENT ON COLUMN "public"."ai_assistant"."id" IS '主键ID(UUID)';
COMMENT ON COLUMN "public"."ai_assistant"."name" IS '助手名称';
COMMENT ON COLUMN "public"."ai_assistant"."logo" IS '助手头像URL';
COMMENT ON COLUMN "public"."ai_assistant"."description" IS '助手描述';
COMMENT ON COLUMN "public"."ai_assistant"."system_prompt" IS '系统提示词';
COMMENT ON COLUMN "public"."ai_assistant"."prompt" IS '用户提示词';
COMMENT ON COLUMN "public"."ai_assistant"."guide_word" IS '引导语';
COMMENT ON COLUMN "public"."ai_assistant"."guide_questions" IS '引导问题(JSON数组格式)';
COMMENT ON COLUMN "public"."ai_assistant"."model_name" IS '模型名称';
COMMENT ON COLUMN "public"."ai_assistant"."temperature" IS '温度参数(0.0-1.0)';
COMMENT ON COLUMN "public"."ai_assistant"."max_token" IS '最大token数';
COMMENT ON COLUMN "public"."ai_assistant"."status" IS '状态: 0-禁用, 1-启用';
COMMENT ON COLUMN "public"."ai_assistant"."user_id" IS '创建用户ID';
COMMENT ON COLUMN "public"."ai_assistant"."is_delete" IS '删除标志: 0-正常, 1-删除';
COMMENT ON COLUMN "public"."ai_assistant"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."ai_assistant"."updated_at" IS '更新时间';
COMMENT ON COLUMN "public"."ai_assistant"."tools" IS '关联工具列表(JSON数组格式)';
COMMENT ON COLUMN "public"."ai_assistant"."flow_list" IS '关联流程列表(JSON数组格式)';
COMMENT ON COLUMN "public"."ai_assistant"."knowledge_list" IS '关联知识库列表(JSON数组格式)';
COMMENT ON COLUMN "public"."ai_assistant"."created_by" IS '创建者';
COMMENT ON COLUMN "public"."ai_assistant"."updated_by" IS '修改者';
COMMENT ON COLUMN "public"."ai_assistant"."workflow_uuid" IS '工作流uuid';
COMMENT ON COLUMN "public"."ai_assistant"."dialog_round" IS '最大论述';
COMMENT ON COLUMN "public"."ai_assistant"."agent_components" IS 'agent组件列表';
COMMENT ON COLUMN "public"."ai_assistant"."subject_id" IS '业务环节id';
COMMENT ON COLUMN "public"."ai_assistant"."tags" IS '标签list';
COMMENT ON COLUMN "public"."ai_assistant"."permission" IS '权限：1-公开，2-私有';
COMMENT ON COLUMN "public"."ai_assistant"."knowledge_search_type" IS '知识库检索类型：语义检索:semantic, 全文检索:fulltext, 混合检索:hybrid';
COMMENT ON COLUMN "public"."ai_assistant"."max_recall_count" IS '最大召回字数';
COMMENT ON COLUMN "public"."ai_assistant"."max_segment_count" IS '最大召回分段';
COMMENT ON COLUMN "public"."ai_assistant"."rerank_model_id" IS '重排模型ID';
COMMENT ON COLUMN "public"."ai_assistant"."followup" IS '追问内容';
COMMENT ON COLUMN "public"."ai_assistant"."bg_img" IS '背景图片';
COMMENT ON COLUMN "public"."ai_assistant"."quick_commands" IS '快捷指令';
COMMENT ON COLUMN "public"."ai_assistant"."shelves_status" IS '组件的上下架状态';
COMMENT ON COLUMN "public"."ai_assistant"."graph_config" IS '存储画布的自定义配置';
COMMENT ON COLUMN "public"."ai_assistant"."deep_thinking_model" IS '深度思考开关';
COMMENT ON COLUMN "public"."ai_assistant"."search" IS '搜索开关';
COMMENT ON COLUMN "public"."ai_assistant"."multimodal" IS '多模态开关';
COMMENT ON COLUMN "public"."ai_assistant"."knowledge_space_list" IS '知识空间id';
COMMENT ON COLUMN "public"."ai_assistant"."recommend" IS '推荐';
COMMENT ON COLUMN "public"."ai_assistant"."active" IS '活跃';
COMMENT ON COLUMN "public"."ai_assistant"."collect" IS '收藏';
COMMENT ON COLUMN "public"."ai_assistant"."top_p" IS 'topP';
COMMENT ON COLUMN "public"."ai_assistant"."is_rerank" IS '是否重排';
COMMENT ON COLUMN "public"."ai_assistant"."rerank_model" IS '重排模型';
COMMENT ON COLUMN "public"."ai_assistant"."rerank_score" IS '重排分';
COMMENT ON COLUMN "public"."ai_assistant"."rerank_top_k" IS '重排top';
COMMENT ON COLUMN "public"."ai_assistant"."rag_score" IS '检索分';
COMMENT ON COLUMN "public"."ai_assistant"."rag_top_k" IS '检索topK';
COMMENT ON COLUMN "public"."ai_assistant"."workflow_uuids" IS '工作流id';
COMMENT ON TABLE "public"."ai_assistant" IS 'AI助手配置表';

-- ----------------------------
-- Records of ai_assistant
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ai_feedback
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_feedback";
CREATE TABLE "public"."ai_feedback" (
  "id" int8 NOT NULL DEFAULT nextval('ai_feedback_id_seq'::regclass),
  "type" varchar(10) COLLATE "pg_catalog"."default",
  "user_message" varchar(500) COLLATE "pg_catalog"."default",
  "agent" text COLLATE "pg_catalog"."default",
  "created_by" int2,
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "chat_uuid" varchar(32) COLLATE "pg_catalog"."default",
  "agent_uuid" varchar(32) COLLATE "pg_catalog"."default",
  "adoption_status" varchar(15) COLLATE "pg_catalog"."default" DEFAULT 'unadopted'::character varying,
  "optimization_flag" bool
)
;
ALTER TABLE "public"."ai_feedback" OWNER TO "postgres";
COMMENT ON COLUMN "public"."ai_feedback"."type" IS '反馈类型 agree disagree';
COMMENT ON COLUMN "public"."ai_feedback"."user_message" IS '用户消息';
COMMENT ON COLUMN "public"."ai_feedback"."agent" IS 'ai回答';
COMMENT ON COLUMN "public"."ai_feedback"."created_by" IS '创建人';
COMMENT ON COLUMN "public"."ai_feedback"."chat_uuid" IS '消息id';
COMMENT ON COLUMN "public"."ai_feedback"."agent_uuid" IS '知识专家的uuid';
COMMENT ON COLUMN "public"."ai_feedback"."adoption_status" IS '采纳状态';
COMMENT ON COLUMN "public"."ai_feedback"."optimization_flag" IS '优化标识';
COMMENT ON TABLE "public"."ai_feedback" IS 'ai问答回馈';

-- ----------------------------
-- Records of ai_feedback
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ai_file_chunk_record
-- ----------------------------
DROP TABLE IF EXISTS "public"."ai_file_chunk_record";
CREATE TABLE "public"."ai_file_chunk_record" (
  "id" int8 NOT NULL DEFAULT nextval('ai_file_chunk_record_id_seq'::regclass),
  "file_md5" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "chunk_size" int4,
  "chunk_count" int4,
  "chunk_index" int4,
  "chunk_file_path" varchar(255) COLLATE "pg_catalog"."default",
  "tenant_id" varchar(6) COLLATE "pg_catalog"."default",
  "merge_status" int4,
  "file_name" varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."ai_file_chunk_record" OWNER TO "postgres";

-- ----------------------------
-- Records of ai_file_chunk_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for chat_file_upload
-- ----------------------------
DROP TABLE IF EXISTS "public"."chat_file_upload";
CREATE TABLE "public"."chat_file_upload" (
  "id" int8 NOT NULL DEFAULT nextval('chat_file_upload_id_seq'::regclass),
  "user_id" int8 NOT NULL,
  "session_id" int8 NOT NULL,
  "original_file_name" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "storage_file_name" varchar(500) COLLATE "pg_catalog"."default",
  "file_path" varchar(1000) COLLATE "pg_catalog"."default",
  "file_url" varchar(1000) COLLATE "pg_catalog"."default",
  "file_size" int8,
  "file_type" varchar(50) COLLATE "pg_catalog"."default",
  "mime_type" varchar(100) COLLATE "pg_catalog"."default",
  "file_category" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "delete_flag" bool NOT NULL DEFAULT false,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "dept_id" int4
)
;
ALTER TABLE "public"."chat_file_upload" OWNER TO "postgres";
COMMENT ON COLUMN "public"."chat_file_upload"."id" IS '主键ID';
COMMENT ON COLUMN "public"."chat_file_upload"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."chat_file_upload"."session_id" IS '会话ID';
COMMENT ON COLUMN "public"."chat_file_upload"."original_file_name" IS '原始文件名';
COMMENT ON COLUMN "public"."chat_file_upload"."storage_file_name" IS '存储文件名';
COMMENT ON COLUMN "public"."chat_file_upload"."file_path" IS '文件路径';
COMMENT ON COLUMN "public"."chat_file_upload"."file_url" IS '文件URL';
COMMENT ON COLUMN "public"."chat_file_upload"."file_size" IS '文件大小（字节）';
COMMENT ON COLUMN "public"."chat_file_upload"."file_type" IS '文件类型';
COMMENT ON COLUMN "public"."chat_file_upload"."mime_type" IS 'MIME类型';
COMMENT ON COLUMN "public"."chat_file_upload"."file_category" IS '文件分类：image-图片，document-文档';
COMMENT ON COLUMN "public"."chat_file_upload"."delete_flag" IS '删除标记：false-未删除，true-已删除';
COMMENT ON COLUMN "public"."chat_file_upload"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."chat_file_upload"."created_by" IS '创建人';
COMMENT ON COLUMN "public"."chat_file_upload"."updated_at" IS '更新时间';
COMMENT ON COLUMN "public"."chat_file_upload"."updated_by" IS '更新人';
COMMENT ON TABLE "public"."chat_file_upload" IS '聊天文件上传表';

-- ----------------------------
-- Records of chat_file_upload
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for chat_session
-- ----------------------------
DROP TABLE IF EXISTS "public"."chat_session";
CREATE TABLE "public"."chat_session" (
  "id" int8 NOT NULL DEFAULT nextval('chat_session_id_seq'::regclass),
  "user_id" int8 NOT NULL,
  "session_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "session_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '新会话'::character varying,
  "delete_flag" bool NOT NULL DEFAULT false,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "dept_id" int4
)
;
ALTER TABLE "public"."chat_session" OWNER TO "postgres";
COMMENT ON COLUMN "public"."chat_session"."id" IS '主键ID';
COMMENT ON COLUMN "public"."chat_session"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."chat_session"."session_id" IS '会话标识';
COMMENT ON COLUMN "public"."chat_session"."session_name" IS '会话名称';
COMMENT ON COLUMN "public"."chat_session"."delete_flag" IS '删除标记：false-未删除，true-已删除';
COMMENT ON COLUMN "public"."chat_session"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."chat_session"."created_by" IS '创建人';
COMMENT ON COLUMN "public"."chat_session"."updated_at" IS '更新时间';
COMMENT ON COLUMN "public"."chat_session"."updated_by" IS '更新人';
COMMENT ON COLUMN "public"."chat_session"."dept_id" IS '部门id';
COMMENT ON TABLE "public"."chat_session" IS '聊天会话表';

-- ----------------------------
-- Records of chat_session
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for chat_session_qa
-- ----------------------------
DROP TABLE IF EXISTS "public"."chat_session_qa";
CREATE TABLE "public"."chat_session_qa" (
  "id" int8 NOT NULL DEFAULT nextval('chat_session_qa_id_seq'::regclass),
  "user_id" int8 NOT NULL,
  "session_id" int8 NOT NULL,
  "question" text COLLATE "pg_catalog"."default" NOT NULL,
  "answer" text COLLATE "pg_catalog"."default",
  "file_upload_ids" text COLLATE "pg_catalog"."default",
  "delete_flag" bool NOT NULL DEFAULT false,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "dept_id" int4
)
;
ALTER TABLE "public"."chat_session_qa" OWNER TO "postgres";
COMMENT ON COLUMN "public"."chat_session_qa"."id" IS '主键ID';
COMMENT ON COLUMN "public"."chat_session_qa"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."chat_session_qa"."session_id" IS '会话ID';
COMMENT ON COLUMN "public"."chat_session_qa"."question" IS '问题';
COMMENT ON COLUMN "public"."chat_session_qa"."answer" IS '答案';
COMMENT ON COLUMN "public"."chat_session_qa"."file_upload_ids" IS '文件上传记录ID列表，逗号分隔';
COMMENT ON COLUMN "public"."chat_session_qa"."delete_flag" IS '删除标记：false-未删除，true-已删除';
COMMENT ON COLUMN "public"."chat_session_qa"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."chat_session_qa"."created_by" IS '创建人';
COMMENT ON COLUMN "public"."chat_session_qa"."updated_at" IS '更新时间';
COMMENT ON COLUMN "public"."chat_session_qa"."updated_by" IS '更新人';
COMMENT ON TABLE "public"."chat_session_qa" IS '聊天问答表';

-- ----------------------------
-- Records of chat_session_qa
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for comment_record
-- ----------------------------
DROP TABLE IF EXISTS "public"."comment_record";
CREATE TABLE "public"."comment_record" (
  "id" int8 NOT NULL DEFAULT nextval('comment_record_id_seq'::regclass),
  "dept_id" int8 NOT NULL DEFAULT 1,
  "user_id" int8 NOT NULL,
  "target_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "target_id" int8 NOT NULL,
  "target_name" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "comment_content" text COLLATE "pg_catalog"."default" NOT NULL,
  "rating" int4 DEFAULT 0,
  "comment_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "delete_flag" bool NOT NULL DEFAULT false
)
;
ALTER TABLE "public"."comment_record" OWNER TO "postgres";

-- ----------------------------
-- Records of comment_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for favorite_record
-- ----------------------------
DROP TABLE IF EXISTS "public"."favorite_record";
CREATE TABLE "public"."favorite_record" (
  "id" int8 NOT NULL DEFAULT nextval('favorite_record_id_seq'::regclass),
  "dept_id" int8 NOT NULL DEFAULT 1,
  "user_id" int8 NOT NULL,
  "target_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "target_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "target_name" varchar(500) COLLATE "pg_catalog"."default",
  "target_url" varchar(1000) COLLATE "pg_catalog"."default",
  "favorite_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "delete_flag" bool NOT NULL DEFAULT false
)
;
ALTER TABLE "public"."favorite_record" OWNER TO "postgres";
COMMENT ON COLUMN "public"."favorite_record"."id" IS '主键ID';
COMMENT ON COLUMN "public"."favorite_record"."dept_id" IS '部门ID';
COMMENT ON COLUMN "public"."favorite_record"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."favorite_record"."target_type" IS '收藏目标类型：knowledge_base, knowledge_file, knowledge_space';
COMMENT ON COLUMN "public"."favorite_record"."target_id" IS '目标ID';
COMMENT ON COLUMN "public"."favorite_record"."target_name" IS '目标名称';
COMMENT ON COLUMN "public"."favorite_record"."target_url" IS '收藏URL';
COMMENT ON COLUMN "public"."favorite_record"."favorite_time" IS '收藏时间';
COMMENT ON TABLE "public"."favorite_record" IS '收藏记录表';

-- ----------------------------
-- Records of favorite_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for file_upload_record
-- ----------------------------
DROP TABLE IF EXISTS "public"."file_upload_record";
CREATE TABLE "public"."file_upload_record" (
  "id" int8 NOT NULL DEFAULT nextval('file_upload_record_id_seq'::regclass),
  "batch_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "knowledge_base_id" int8 NOT NULL,
  "space_id" int8 NOT NULL,
  "file_name" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "file_size" int8 NOT NULL,
  "file_type" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "upload_status" int2 NOT NULL DEFAULT 1,
  "parse_status" int2 NOT NULL DEFAULT 1,
  "parse_result" text COLLATE "pg_catalog"."default",
  "error_message" text COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "dept_id" int8 NOT NULL
)
;
ALTER TABLE "public"."file_upload_record" OWNER TO "postgres";
COMMENT ON COLUMN "public"."file_upload_record"."batch_id" IS '批次ID';
COMMENT ON COLUMN "public"."file_upload_record"."upload_status" IS '上传状态：1-上传中，2-上传成功，3-上传失败';
COMMENT ON COLUMN "public"."file_upload_record"."parse_status" IS '解析状态：1-待解析，2-解析中，3-解析成功，4-解析失败';
COMMENT ON COLUMN "public"."file_upload_record"."parse_result" IS '解析结果';
COMMENT ON COLUMN "public"."file_upload_record"."error_message" IS '错误信息';
COMMENT ON COLUMN "public"."file_upload_record"."dept_id" IS '部门ID';
COMMENT ON TABLE "public"."file_upload_record" IS '文件上传记录表';

-- ----------------------------
-- Records of file_upload_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for knowledge_base
-- ----------------------------
DROP TABLE IF EXISTS "public"."knowledge_base";
CREATE TABLE "public"."knowledge_base" (
  "id" int8 NOT NULL DEFAULT nextval('knowledge_base_id_seq'::regclass),
  "name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "description" text COLLATE "pg_catalog"."default",
  "tags" varchar(500) COLLATE "pg_catalog"."default",
  "permission_type" int2 NOT NULL DEFAULT 1,
  "status" int2 NOT NULL DEFAULT 1,
  "file_count" int4 NOT NULL DEFAULT 0,
  "total_size" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "delete_flag" bool NOT NULL DEFAULT false,
  "dept_id" int8 NOT NULL,
  "segment_count" int4 DEFAULT 0,
  "word_count" int4 DEFAULT 0,
  "text_model" varchar(80) COLLATE "pg_catalog"."default",
  "embedding_model" varchar(80) COLLATE "pg_catalog"."default",
  "multimodal_model" varchar(80) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."knowledge_base" OWNER TO "postgres";
COMMENT ON COLUMN "public"."knowledge_base"."id" IS '主键ID';
COMMENT ON COLUMN "public"."knowledge_base"."name" IS '知识库名称';
COMMENT ON COLUMN "public"."knowledge_base"."description" IS '知识库描述';
COMMENT ON COLUMN "public"."knowledge_base"."tags" IS '标签，多个用逗号分隔';
COMMENT ON COLUMN "public"."knowledge_base"."permission_type" IS '权限类型：1-公开，2-受限';
COMMENT ON COLUMN "public"."knowledge_base"."status" IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN "public"."knowledge_base"."file_count" IS '文件数量';
COMMENT ON COLUMN "public"."knowledge_base"."total_size" IS '总大小（字节）';
COMMENT ON COLUMN "public"."knowledge_base"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_base"."created_by" IS '创建者ID';
COMMENT ON COLUMN "public"."knowledge_base"."updated_at" IS '更新时间';
COMMENT ON COLUMN "public"."knowledge_base"."updated_by" IS '更新者ID';
COMMENT ON COLUMN "public"."knowledge_base"."delete_flag" IS '删除标记：false-未删除，true-已删除';
COMMENT ON COLUMN "public"."knowledge_base"."dept_id" IS '部门ID';
COMMENT ON COLUMN "public"."knowledge_base"."segment_count" IS '分段数量';
COMMENT ON COLUMN "public"."knowledge_base"."word_count" IS '总字数';
COMMENT ON COLUMN "public"."knowledge_base"."text_model" IS '文本模型';
COMMENT ON COLUMN "public"."knowledge_base"."embedding_model" IS '向量模型';
COMMENT ON COLUMN "public"."knowledge_base"."multimodal_model" IS '多模态模型';
COMMENT ON TABLE "public"."knowledge_base" IS '知识库表';

-- ----------------------------
-- Records of knowledge_base
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for knowledge_base_permission
-- ----------------------------
DROP TABLE IF EXISTS "public"."knowledge_base_permission";
CREATE TABLE "public"."knowledge_base_permission" (
  "id" int8 NOT NULL DEFAULT nextval('knowledge_base_permission_id_seq'::regclass),
  "knowledge_base_id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "user_name" varchar(100) COLLATE "pg_catalog"."default",
  "user_email" varchar(200) COLLATE "pg_catalog"."default",
  "user_role" varchar(50) COLLATE "pg_catalog"."default",
  "permission_type" int2 NOT NULL DEFAULT 1,
  "status" int2 NOT NULL DEFAULT 1,
  "remark" text COLLATE "pg_catalog"."default",
  "delete_flag" bool NOT NULL DEFAULT false,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" varchar(100) COLLATE "pg_catalog"."default",
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" varchar(100) COLLATE "pg_catalog"."default",
  "dept_id" int4
)
;
ALTER TABLE "public"."knowledge_base_permission" OWNER TO "postgres";
COMMENT ON COLUMN "public"."knowledge_base_permission"."id" IS '主键ID';
COMMENT ON COLUMN "public"."knowledge_base_permission"."knowledge_base_id" IS '知识库ID';
COMMENT ON COLUMN "public"."knowledge_base_permission"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."knowledge_base_permission"."user_name" IS '用户姓名';
COMMENT ON COLUMN "public"."knowledge_base_permission"."user_email" IS '用户邮箱';
COMMENT ON COLUMN "public"."knowledge_base_permission"."user_role" IS '用户角色：超级管理员、管通用户、管理员等';
COMMENT ON COLUMN "public"."knowledge_base_permission"."permission_type" IS '权限类型：1-只读，2-读写，3-管理';
COMMENT ON COLUMN "public"."knowledge_base_permission"."status" IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN "public"."knowledge_base_permission"."remark" IS '备注';
COMMENT ON COLUMN "public"."knowledge_base_permission"."delete_flag" IS '删除标记：false-未删除，true-已删除';
COMMENT ON COLUMN "public"."knowledge_base_permission"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_base_permission"."created_by" IS '创建人';
COMMENT ON COLUMN "public"."knowledge_base_permission"."updated_at" IS '更新时间';
COMMENT ON COLUMN "public"."knowledge_base_permission"."updated_by" IS '更新人';
COMMENT ON COLUMN "public"."knowledge_base_permission"."dept_id" IS '部门Id';
COMMENT ON TABLE "public"."knowledge_base_permission" IS '知识库权限表';

-- ----------------------------
-- Records of knowledge_base_permission
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for knowledge_base_permission_application
-- ----------------------------
DROP TABLE IF EXISTS "public"."knowledge_base_permission_application";
CREATE TABLE "public"."knowledge_base_permission_application" (
  "id" int8 NOT NULL DEFAULT nextval('knowledge_base_permission_application_id_seq'::regclass),
  "knowledge_base_id" int8 NOT NULL,
  "applicant_user_id" int8 NOT NULL,
  "applicant_user_name" varchar(100) COLLATE "pg_catalog"."default",
  "applicant_user_email" varchar(200) COLLATE "pg_catalog"."default",
  "current_role" varchar(50) COLLATE "pg_catalog"."default",
  "target_role" varchar(50) COLLATE "pg_catalog"."default",
  "target_permission_type" int2,
  "application_reason" text COLLATE "pg_catalog"."default",
  "application_status" int2 NOT NULL DEFAULT 0,
  "approver_user_id" int8,
  "approver_user_name" varchar(100) COLLATE "pg_catalog"."default",
  "approve_time" timestamp(6),
  "approve_reason" text COLLATE "pg_catalog"."default",
  "delete_flag" bool NOT NULL DEFAULT false,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" varchar(100) COLLATE "pg_catalog"."default",
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" varchar(100) COLLATE "pg_catalog"."default",
  "dept_id" int4
)
;
ALTER TABLE "public"."knowledge_base_permission_application" OWNER TO "postgres";
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."id" IS '主键ID';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."knowledge_base_id" IS '知识库ID';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."applicant_user_id" IS '申请人用户ID';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."applicant_user_name" IS '申请人姓名';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."applicant_user_email" IS '申请人邮箱';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."current_role" IS '当前角色';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."target_role" IS '目标角色';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."target_permission_type" IS '目标权限类型：1-只读，2-读写，3-管理';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."application_reason" IS '申请理由';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."application_status" IS '申请状态：0-待审批，1-已通过，2-已拒绝，3-已撤回';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."approver_user_id" IS '审批人用户ID';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."approver_user_name" IS '审批人姓名';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."approve_time" IS '审批时间';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."approve_reason" IS '审批意见';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."delete_flag" IS '删除标记：false-未删除，true-已删除';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."created_by" IS '创建人';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."updated_at" IS '更新时间';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."updated_by" IS '更新人';
COMMENT ON COLUMN "public"."knowledge_base_permission_application"."dept_id" IS '部门';
COMMENT ON TABLE "public"."knowledge_base_permission_application" IS '知识库权限申请表';

-- ----------------------------
-- Records of knowledge_base_permission_application
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for knowledge_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."knowledge_file";
CREATE TABLE "public"."knowledge_file" (
  "id" int8 NOT NULL DEFAULT nextval('knowledge_file_id_seq'::regclass),
  "knowledge_base_id" int8 NOT NULL,
  "space_id" int8,
  "file_name" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "original_name" varchar(500) COLLATE "pg_catalog"."default",
  "file_path" varchar(1000) COLLATE "pg_catalog"."default" NOT NULL,
  "file_size" int8 NOT NULL,
  "file_type" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "mime_type" varchar(200) COLLATE "pg_catalog"."default",
  "content" text COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 1,
  "processing_status" varchar(100) COLLATE "pg_catalog"."default",
  "word_count" int4 DEFAULT 0,
  "page_count" int4 DEFAULT 0,
  "upload_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "process_time" timestamp(6),
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "delete_flag" bool NOT NULL DEFAULT false,
  "dept_id" int8 NOT NULL,
  "segmented_doc_path" varchar(255) COLLATE "pg_catalog"."default",
  "summary" varchar(500) COLLATE "pg_catalog"."default",
  "tags" varchar(200) COLLATE "pg_catalog"."default",
  "cover_path" varchar(500) COLLATE "pg_catalog"."default",
  "file_uuid" varchar(50) COLLATE "pg_catalog"."default",
  "create_type" varchar(10) COLLATE "pg_catalog"."default",
  "chunk_type" varchar(10) COLLATE "pg_catalog"."default",
  "oss_path" varchar(255) COLLATE "pg_catalog"."default",
  "extra_type" varchar(7) COLLATE "pg_catalog"."default" DEFAULT 'SEGMENT'::character varying,
  "uuid" varchar(32) COLLATE "pg_catalog"."default",
  "knowledge_segment_config" jsonb,
  "file_md5" varchar(32) COLLATE "pg_catalog"."default",
  "process_progress" jsonb DEFAULT '{"qa_finish": 0, "vl_finish": 0, "vector_finish": 0}'::jsonb
)
;
ALTER TABLE "public"."knowledge_file" OWNER TO "postgres";
COMMENT ON COLUMN "public"."knowledge_file"."status" IS '状态：1-待处理，2-处理中，3-已可用，4-处理失败';
COMMENT ON COLUMN "public"."knowledge_file"."processing_status" IS '处理状态描述';
COMMENT ON COLUMN "public"."knowledge_file"."word_count" IS '字数统计';
COMMENT ON COLUMN "public"."knowledge_file"."page_count" IS '页数统计';
COMMENT ON COLUMN "public"."knowledge_file"."upload_time" IS '上传时间';
COMMENT ON COLUMN "public"."knowledge_file"."process_time" IS '处理完成时间';
COMMENT ON COLUMN "public"."knowledge_file"."dept_id" IS '部门ID';
COMMENT ON COLUMN "public"."knowledge_file"."segmented_doc_path" IS '分段后的文档地址';
COMMENT ON COLUMN "public"."knowledge_file"."summary" IS '摘要';
COMMENT ON COLUMN "public"."knowledge_file"."tags" IS '标签，分割';
COMMENT ON COLUMN "public"."knowledge_file"."cover_path" IS '封面地址';
COMMENT ON COLUMN "public"."knowledge_file"."file_uuid" IS '文件uuid';
COMMENT ON COLUMN "public"."knowledge_file"."create_type" IS 'UPLOAD上传 URL ONLINE在线';
COMMENT ON COLUMN "public"."knowledge_file"."chunk_type" IS '处理形式分块处理/问答对提取';
COMMENT ON COLUMN "public"."knowledge_file"."oss_path" IS 'oss地址';
COMMENT ON COLUMN "public"."knowledge_file"."extra_type" IS '解析类型 SEGMENT/QA';
COMMENT ON COLUMN "public"."knowledge_file"."knowledge_segment_config" IS '分段配置';
COMMENT ON COLUMN "public"."knowledge_file"."file_md5" IS '文件md5';
COMMENT ON COLUMN "public"."knowledge_file"."process_progress" IS '文件处理进度，JSON格式：{"vl_finish":已完成数,"qa_finish":已完成数,"vector_finish":已完成数}';
COMMENT ON TABLE "public"."knowledge_file" IS '知识库文件表';

-- ----------------------------
-- Records of knowledge_file
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for knowledge_file_segment
-- ----------------------------
DROP TABLE IF EXISTS "public"."knowledge_file_segment";
CREATE TABLE "public"."knowledge_file_segment" (
  "id" int8 NOT NULL DEFAULT nextval('knowledge_file_segment_id_seq'::regclass),
  "file_id" int8,
  "knowledge_base_id" int8,
  "segment_index" int4,
  "segment_content" text COLLATE "pg_catalog"."default",
  "word_count" int4 DEFAULT 0,
  "start_position" int4,
  "end_position" int4,
  "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8,
  "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "delete_flag" bool DEFAULT false,
  "dept_id" int8,
  "knowledge_space_id" int4,
  "file_md5" varchar(32) COLLATE "pg_catalog"."default",
  "vector_flag" bool,
  "vl_content" text COLLATE "pg_catalog"."default",
  "vector" "public"."vector",
  "title" varchar(200) COLLATE "pg_catalog"."default",
  "segment_content_tsv" tsvector
)
;
ALTER TABLE "public"."knowledge_file_segment" OWNER TO "postgres";
COMMENT ON COLUMN "public"."knowledge_file_segment"."id" IS '主键ID';
COMMENT ON COLUMN "public"."knowledge_file_segment"."file_id" IS '所属文件ID';
COMMENT ON COLUMN "public"."knowledge_file_segment"."knowledge_base_id" IS '知识库ID';
COMMENT ON COLUMN "public"."knowledge_file_segment"."segment_index" IS '分段序号';
COMMENT ON COLUMN "public"."knowledge_file_segment"."segment_content" IS '分段内容';
COMMENT ON COLUMN "public"."knowledge_file_segment"."word_count" IS '分段字数';
COMMENT ON COLUMN "public"."knowledge_file_segment"."start_position" IS '在原文中的起始位置';
COMMENT ON COLUMN "public"."knowledge_file_segment"."end_position" IS '在原文中的结束位置';
COMMENT ON COLUMN "public"."knowledge_file_segment"."delete_flag" IS '删除标记：false-未删除，true-已删除';
COMMENT ON COLUMN "public"."knowledge_file_segment"."dept_id" IS '部门ID';
COMMENT ON COLUMN "public"."knowledge_file_segment"."vector_flag" IS '向量标识';
COMMENT ON COLUMN "public"."knowledge_file_segment"."vl_content" IS 'vl识别图片之后的结果';
COMMENT ON COLUMN "public"."knowledge_file_segment"."title" IS 'qa标题';
COMMENT ON TABLE "public"."knowledge_file_segment" IS '文件分段表';

-- ----------------------------
-- Records of knowledge_file_segment
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for knowledge_space
-- ----------------------------
DROP TABLE IF EXISTS "public"."knowledge_space";
CREATE TABLE "public"."knowledge_space" (
  "id" int8 NOT NULL DEFAULT nextval('knowledge_space_id_seq'::regclass),
  "knowledge_base_id" int8 NOT NULL,
  "parent_id" int8 NOT NULL DEFAULT 0,
  "name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "type" int2 NOT NULL DEFAULT 1,
  "path" varchar(1000) COLLATE "pg_catalog"."default" NOT NULL,
  "level" int4 NOT NULL DEFAULT 1,
  "sort_order" int4 NOT NULL DEFAULT 0,
  "file_count" int4 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "delete_flag" bool NOT NULL DEFAULT false,
  "dept_id" int8 NOT NULL
)
;
ALTER TABLE "public"."knowledge_space" OWNER TO "postgres";
COMMENT ON COLUMN "public"."knowledge_space"."id" IS '主键ID';
COMMENT ON COLUMN "public"."knowledge_space"."knowledge_base_id" IS '知识库ID';
COMMENT ON COLUMN "public"."knowledge_space"."parent_id" IS '父级ID，0表示根目录';
COMMENT ON COLUMN "public"."knowledge_space"."name" IS '空间名称';
COMMENT ON COLUMN "public"."knowledge_space"."type" IS '类型：1-文件夹，2-文件';
COMMENT ON COLUMN "public"."knowledge_space"."path" IS '完整路径';
COMMENT ON COLUMN "public"."knowledge_space"."level" IS '层级';
COMMENT ON COLUMN "public"."knowledge_space"."sort_order" IS '排序';
COMMENT ON COLUMN "public"."knowledge_space"."file_count" IS '文件数量';
COMMENT ON COLUMN "public"."knowledge_space"."dept_id" IS '部门ID';
COMMENT ON TABLE "public"."knowledge_space" IS '知识库空间表';

-- ----------------------------
-- Records of knowledge_space
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for knowledge_tag
-- ----------------------------
DROP TABLE IF EXISTS "public"."knowledge_tag";
CREATE TABLE "public"."knowledge_tag" (
  "id" int8 NOT NULL DEFAULT nextval('knowledge_tag_id_seq'::regclass),
  "tag_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "tag_color" varchar(20) COLLATE "pg_catalog"."default",
  "description" text COLLATE "pg_catalog"."default",
  "use_count" int4 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "delete_flag" bool NOT NULL DEFAULT false,
  "dept_id" int8 NOT NULL
)
;
ALTER TABLE "public"."knowledge_tag" OWNER TO "postgres";
COMMENT ON COLUMN "public"."knowledge_tag"."tag_name" IS '标签名称';
COMMENT ON COLUMN "public"."knowledge_tag"."tag_color" IS '标签颜色';
COMMENT ON COLUMN "public"."knowledge_tag"."description" IS '标签描述';
COMMENT ON COLUMN "public"."knowledge_tag"."use_count" IS '使用次数';
COMMENT ON COLUMN "public"."knowledge_tag"."dept_id" IS '部门ID';
COMMENT ON TABLE "public"."knowledge_tag" IS '标签表';

-- ----------------------------
-- Records of knowledge_tag
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for knowledge_vector
-- ----------------------------
DROP TABLE IF EXISTS "public"."knowledge_vector";
CREATE TABLE "public"."knowledge_vector" (
  "id" int8 NOT NULL DEFAULT nextval('knowledge_vector_id_seq'::regclass),
  "knowledge_base_id" int8 NOT NULL,
  "space_id" int8,
  "knowledge_file_id" int8 NOT NULL,
  "segment_id" int4 DEFAULT 0,
  "segment_type" varchar(30) COLLATE "pg_catalog"."default" DEFAULT 'text'::character varying,
  "content" text COLLATE "pg_catalog"."default" NOT NULL,
  "vector" "public"."vector" NOT NULL,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL
)
;
ALTER TABLE "public"."knowledge_vector" OWNER TO "postgres";
COMMENT ON COLUMN "public"."knowledge_vector"."knowledge_base_id" IS '知识库ID，关联knowledge_base.id';
COMMENT ON COLUMN "public"."knowledge_vector"."space_id" IS '知识空间ID，关联knowledge_space.id';
COMMENT ON COLUMN "public"."knowledge_vector"."knowledge_file_id" IS '知识文件ID，关联knowledge_file.id';
COMMENT ON COLUMN "public"."knowledge_vector"."segment_id" IS '分段序号，同一文件内从0开始';
COMMENT ON COLUMN "public"."knowledge_vector"."segment_type" IS '分段类型：text-文本分段，image-图片分段';
COMMENT ON COLUMN "public"."knowledge_vector"."content" IS '分段原始内容：文本内容或图片描述信息';
COMMENT ON COLUMN "public"."knowledge_vector"."vector" IS '1024维向量数据，用于相似度计算';
COMMENT ON TABLE "public"."knowledge_vector" IS '知识库向量存储表 - 存储文档分段的向量化数据';

-- ----------------------------
-- Records of knowledge_vector
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for llm_model
-- ----------------------------
DROP TABLE IF EXISTS "public"."llm_model";
CREATE TABLE "public"."llm_model" (
  "id" int8 NOT NULL DEFAULT nextval('llm_model_id_seq'::regclass),
  "name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "model" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "description" text COLLATE "pg_catalog"."default",
  "provider" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "model_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "api_url" varchar(500) COLLATE "pg_catalog"."default",
  "need_user_token_flag" bool,
  "status" int4 DEFAULT 1,
  "sort_order" int4 DEFAULT 0,
  "created_by" varchar(100) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6),
  "updated_by" varchar(100) COLLATE "pg_catalog"."default",
  "updated_at" timestamp(6),
  "remark" text COLLATE "pg_catalog"."default",
  "del_flag" bool DEFAULT false,
  "model_icon" varchar(300) COLLATE "pg_catalog"."default",
  "deep_thinking_model" bool DEFAULT false,
  "multimodal" bool DEFAULT false,
  "provider_code" varchar(40) COLLATE "pg_catalog"."default",
  "token_type" int4,
  "document" text COLLATE "pg_catalog"."default",
  "max_tokens" int4,
  "stream_flag" bool DEFAULT true,
  "dept_id" int4,
  "web_search" bool DEFAULT false
)
;
ALTER TABLE "public"."llm_model" OWNER TO "postgres";
COMMENT ON COLUMN "public"."llm_model"."id" IS '主键ID';
COMMENT ON COLUMN "public"."llm_model"."name" IS '模型名称';
COMMENT ON COLUMN "public"."llm_model"."model" IS '模型标识';
COMMENT ON COLUMN "public"."llm_model"."description" IS '模型描述';
COMMENT ON COLUMN "public"."llm_model"."provider" IS '提供方';
COMMENT ON COLUMN "public"."llm_model"."model_type" IS '模型类型';
COMMENT ON COLUMN "public"."llm_model"."api_url" IS 'API地址';
COMMENT ON COLUMN "public"."llm_model"."need_user_token_flag" IS '是否需要用户token(0-不需要,1-需要)';
COMMENT ON COLUMN "public"."llm_model"."status" IS '状态(0-禁用,1-启用,2-维护中)';
COMMENT ON COLUMN "public"."llm_model"."sort_order" IS '排序顺序';
COMMENT ON COLUMN "public"."llm_model"."model_icon" IS '大模型图标';
COMMENT ON COLUMN "public"."llm_model"."deep_thinking_model" IS '深度思考模型';
COMMENT ON COLUMN "public"."llm_model"."multimodal" IS '多模态模型';
COMMENT ON COLUMN "public"."llm_model"."provider_code" IS '厂商code';
COMMENT ON COLUMN "public"."llm_model"."token_type" IS 'token长度：1、2k以下 2、4k-8k 3、8k-32k、4、32k以上';
COMMENT ON COLUMN "public"."llm_model"."document" IS 'MD文档';
COMMENT ON COLUMN "public"."llm_model"."max_tokens" IS '最大token长度';
COMMENT ON COLUMN "public"."llm_model"."stream_flag" IS '流式输入标识';
COMMENT ON COLUMN "public"."llm_model"."web_search" IS '联网搜索';
COMMENT ON TABLE "public"."llm_model" IS '大模型配置表';

-- ----------------------------
-- Records of llm_model
-- ----------------------------
BEGIN;
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (55, 'kimi-k2', 'kimi-k2-turbo-preview', 'kimi-k2 是一款具备超强代码和 Agent 能力的 MoE 架构基础模型，总参数 1T，激活参数 32B。在通用知识推理、编程、数学、Agent 等主要类别的基准性能测试中，K2 模型的性能超过其他主流开源模型', 'KIMI', 'text_generation', NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/moonshot.png', 'f', 'f', 'moonshot', 5, NULL, NULL, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (5, 'qwen3-max', 'qwen3-max', '通义千问3系列Max模型，相较preview版本在智能体编程与工具调用方向进行了专项升级。本次发布的正式版模型达到领域SOTA水平，适配场景更加复杂的智能体需求。', '千问', 'text_generation', 'https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation', 't', 1, 5, '1', '2025-07-12 03:48:13.39548', '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/tongyi.png', 'f', 'f', 'qwen', NULL, NULL, 16384, 't', NULL, 't');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (59, 'moonshot-vl', 'moonshot-v1-128k-vision-preview', 'moonshot-v1-128k 视觉模型', '月之暗面', 'multimodal', NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/moonshot.png', 'f', 't', 'moonshot', 5, NULL, NULL, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (58, 'moonshot-v1', 'moonshot-v1-128k', 'moonshot-v1-128k 对话模型', '月之暗面', 'image_generation', NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/moonshot.png', 'f', 't', 'moonshot', 5, NULL, NULL, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (61, 'kimi-thinking-preview', 'kimi-thinking-preview', 'kimi-thinking-preview 模型是月之暗面提供的具有多模态推理能力和通用推理能力的多模态思考模型，它擅长深度推理，帮助解决更多更难的事情', '月之暗面', 'deep_reasoning', NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/moonshot.png', 'f', 't', 'moonshot', 5, NULL, NULL, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (30, 'tongyi-intent-detect-v3', 'tongyi-intent-detect-v3', '意图识别模型', '千问', 'text_generation', '', NULL, NULL, NULL, '1', NULL, '1', NULL, NULL, NULL, 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/tongyi.png', 'f', 'f', 'qwen', NULL, NULL, 1024, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (56, 'kimi-vl', 'kimi-latest-128k', 'kimi-latest 模型总是使用 Kimi 智能助手产品使用最新的 Kimi 大模型版本，可能包含尚未稳定的特性', 'KIMI', 'multimodal', NULL, NULL, 1, 0, NULL, NULL, NULL, NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/moonshot.png', 'f', 't', 'moonshot', 5, NULL, NULL, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (40, 'deepseek-v3', 'deepseek-chat', '685B 满血版模型，2025年8月20日发布', 'DeepSeek', 'text_generation', NULL, NULL, 1, 0, '1', NULL, '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/deepseek.png', 'f', 'f', 'deepseek', 5, NULL, 8192, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (41, 'deepseek-thinking-v3', 'deepseek-reasoner', 'DeepSeek 思考模型，使用工具自动变为 deepseek-v3.1', 'DeepSeek', 'deep_reasoning', NULL, NULL, 1, 0, '1', NULL, '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/deepseek.png', 't', 'f', 'deepseek', 5, NULL, 65536, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (106, 'embedding-3', 'embedding-3', 'Embedding-3 在架构和训练数据上都进行了重大升级，显著提升了语义理解的准确性和泛化能力。新模型在多个评测基准上都取得了显著的性能提升。', '智普', 'embedding_model', NULL, NULL, 1, 0, '1', NULL, '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/zhipu.png', 'f', 'f', 'zhipu', 5, NULL, 4000, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (39, 'qwen3-vl-plus', 'qwen3-vl-plus', 'Qwen3系列视觉理解模型，实现思考模式和非思考模式的有效融合，视觉智能体能力在OS World等公开测试集上达到世界顶尖水平。此版本在视觉coding、空间感知、多模态思考等方向全面升级；视觉感知与识别能力大幅提升，支持超长视频理解。', '千问', 'multimodal', NULL, NULL, 1, 0, '1', NULL, '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/tongyi.png', 'f', 't', 'qwen', NULL, NULL, 8192, 't', NULL, 't');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (105, 'text-embedding-v3', 'text-embedding-v3', '通用文本向量，是通义实验室基于LLM底座的多语言文本统一向量模型，面向全球多个主流语种，提供高水准的向量服务，帮助开发者将文本数据快速转换为高质量的向量数据。

', '千问', 'embedding_model', NULL, NULL, 1, 0, '1', NULL, '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/tongyi.png', 'f', 'f', 'qwen', 2, NULL, 30000, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (38, 'glm-4.6', 'glm-4.6', 'GLM-4.6 是智谱最新的旗舰模型，其总参数量 355B，激活参数 32B。GLM-4.6 所有核心能力上均完成了对 GLM-4.5 的超越，具体如下：
高级编码能力：在公开基准与真实编程任务中，GLM-4.6 的代码能力对齐 Claude Sonnet 4，是国内已知的最好的 Coding 模型。
上下文长度：上下文窗口由 128K→200K，适应更长的代码和智能体任务。
推理能力：推理能力提升，并支持在推理过程中调用工具。
搜索能力：增强了模型在工具调用和搜索智能体上的表现，在智能体框架中表现更好。
写作能力：在文风、可读性与角色扮演场景中更符合人类偏好。
多语言翻译：进一步增强跨语种任务的处理效果。', '智普', 'text_generation', NULL, NULL, 1, 0, '1', NULL, '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/zhipu.png', 'f', 'f', 'zhipu', 4, NULL, 4000, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (108, 'glm-4.5v', 'glm-4.5v', 'GLM-4.5V 是智谱新一代基于 MOE 架构的视觉推理模型，以 106B 的总参数量和 12B 激活参数量，在各类基准测试中达到全球同级别开源多模态模型 SOTA，涵盖图像、视频、文档理解及 GUI 任务等常见任务。', '智普', 'multimodal', NULL, NULL, 1, 0, '1', NULL, '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/zhipu.png', 'f', 'f', 'zhipu', 5, NULL, 4000, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (101, 'qwen3-rerank', 'qwen3-rerank', '基于Qwen LLM底座训练的文本排序模型，对输入的Query和候选Docs进行相关性排序，支持100+语种和长文本输入，适用于文本检索、RAG等场景，效果对齐开源Qwen3-Rerank系列模型', '千问', 'text_rerank', NULL, NULL, 1, 0, '1', NULL, '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/tongyi.png', 'f', 'f', 'qwen', 2, NULL, 30000, 't', NULL, 'f');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (102, 'qwen-plus', 'qwen-plus', 'Qwen3系列Plus模型，实现思考模式和非思考模式的有效融合，可在对话中切换模式。推理能力显著超过QwQ、通用能力显著超过Qwen2.5-Plus，达到同规模业界SOTA水平。', '千问', 'text_generation', 'https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation', 't', 1, 5, '1', '2025-07-12 03:48:13.39548', '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/tongyi.png', 't', 'f', 'qwen', NULL, NULL, 16384, 't', NULL, 't');
INSERT INTO "public"."llm_model" ("id", "name", "model", "description", "provider", "model_type", "api_url", "need_user_token_flag", "status", "sort_order", "created_by", "created_at", "updated_by", "updated_at", "remark", "del_flag", "model_icon", "deep_thinking_model", "multimodal", "provider_code", "token_type", "document", "max_tokens", "stream_flag", "dept_id", "web_search") VALUES (103, 'text-embedding-v4', 'text-embedding-v4', '是通义实验室基于Qwen3训练的多语言文本统一向量模型，相较V3版本在文本检索、聚类、分类性能大幅提升；在MTEB多语言、中英、Code检索等评测任务上效果提升15%~40%；支持64~2048维用户自定义向量维度。', '千问', 'embedding_model', NULL, NULL, 1, 0, '1', NULL, '1', NULL, NULL, 'f', 'https://oss-aidojo-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icon/tongyi.png', 'f', 'f', 'qwen', 2, NULL, 30000, 't', NULL, 'f');
COMMIT;

-- ----------------------------
-- Table structure for miner_u_task
-- ----------------------------
DROP TABLE IF EXISTS "public"."miner_u_task";
CREATE TABLE "public"."miner_u_task" (
  "id" int8 NOT NULL DEFAULT nextval('miner_u_task_id_seq'::regclass),
  "file_uuid" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "original_file_url" text COLLATE "pg_catalog"."default" NOT NULL,
  "original_file_name" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "parse_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'pdf'::character varying,
  "state" varchar(20) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'pending'::character varying,
  "task_id" varchar(100) COLLATE "pg_catalog"."default",
  "trace_id" varchar(100) COLLATE "pg_catalog"."default",
  "full_zip_url" text COLLATE "pg_catalog"."default",
  "result_content" text COLLATE "pg_catalog"."default",
  "error_message" text COLLATE "pg_catalog"."default",
  "extracted_pages" int4 DEFAULT 0,
  "total_pages" int4 DEFAULT 0,
  "start_time" timestamp(6),
  "complete_time" timestamp(6),
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "dept_id" int8,
  "delete_flag" bool NOT NULL DEFAULT false,
  "local_result_path" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."miner_u_task" OWNER TO "postgres";
COMMENT ON COLUMN "public"."miner_u_task"."id" IS '主键ID';
COMMENT ON COLUMN "public"."miner_u_task"."file_uuid" IS '文件UUID，关联原始文件';
COMMENT ON COLUMN "public"."miner_u_task"."original_file_url" IS '原始文件URL';
COMMENT ON COLUMN "public"."miner_u_task"."original_file_name" IS '原始文件名';
COMMENT ON COLUMN "public"."miner_u_task"."parse_type" IS '解析类型(pdf, word, ppt等)';
COMMENT ON COLUMN "public"."miner_u_task"."state" IS '任务处理状态，完成:done，pending: 排队中，running: 正在解析，failed：解析失败，converting：格式转换中';
COMMENT ON COLUMN "public"."miner_u_task"."task_id" IS 'MinerU返回的任务ID';
COMMENT ON COLUMN "public"."miner_u_task"."trace_id" IS '链路追踪ID';
COMMENT ON COLUMN "public"."miner_u_task"."full_zip_url" IS '完整结果压缩包URL';
COMMENT ON COLUMN "public"."miner_u_task"."result_content" IS '解析结果内容';
COMMENT ON COLUMN "public"."miner_u_task"."error_message" IS '错误信息';
COMMENT ON COLUMN "public"."miner_u_task"."extracted_pages" IS '已解析页数';
COMMENT ON COLUMN "public"."miner_u_task"."total_pages" IS '总页数';
COMMENT ON COLUMN "public"."miner_u_task"."start_time" IS '任务开始时间';
COMMENT ON COLUMN "public"."miner_u_task"."complete_time" IS '任务完成时间';
COMMENT ON COLUMN "public"."miner_u_task"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."miner_u_task"."created_by" IS '创建人';
COMMENT ON COLUMN "public"."miner_u_task"."updated_at" IS '更新时间';
COMMENT ON COLUMN "public"."miner_u_task"."updated_by" IS '更新人';
COMMENT ON COLUMN "public"."miner_u_task"."dept_id" IS '部门ID';
COMMENT ON COLUMN "public"."miner_u_task"."delete_flag" IS '删除标志（false代表存在 true代表删除）';
COMMENT ON COLUMN "public"."miner_u_task"."local_result_path" IS '本地处理结果';
COMMENT ON TABLE "public"."miner_u_task" IS 'MinerU文件解析任务表，用于存放对应文件的解析任务记录';

-- ----------------------------
-- Records of miner_u_task
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS "public"."notification";
CREATE TABLE "public"."notification" (
  "id" int8 NOT NULL DEFAULT nextval('notification_id_seq'::regclass),
  "type" varchar(10) COLLATE "pg_catalog"."default",
  "title" varchar(200) COLLATE "pg_catalog"."default",
  "content" text COLLATE "pg_catalog"."default",
  "sender_id" int2,
  "receiver_id" int2,
  "behavior" varchar(10) COLLATE "pg_catalog"."default",
  "jump_type" varchar(30) COLLATE "pg_catalog"."default",
  "jump_url" varchar(200) COLLATE "pg_catalog"."default",
  "target_id" varchar(32) COLLATE "pg_catalog"."default",
  "read_flag" bool DEFAULT false,
  "create_time" timestamp(6),
  "read_time" timestamp(6)
)
;
ALTER TABLE "public"."notification" OWNER TO "postgres";
COMMENT ON COLUMN "public"."notification"."type" IS 'system/trigger';
COMMENT ON COLUMN "public"."notification"."sender_id" IS '发送人id';
COMMENT ON COLUMN "public"."notification"."receiver_id" IS '接受人id';
COMMENT ON COLUMN "public"."notification"."behavior" IS '行为 LIKE、COMMENT、COLLECT';
COMMENT ON COLUMN "public"."notification"."jump_type" IS '跳转类型';
COMMENT ON COLUMN "public"."notification"."jump_url" IS '跳转url可为空';
COMMENT ON COLUMN "public"."notification"."target_id" IS '跳转uuid，可为uuid/id';
COMMENT ON COLUMN "public"."notification"."read_flag" IS '阅读标识';
COMMENT ON COLUMN "public"."notification"."read_time" IS '阅读时间';
COMMENT ON TABLE "public"."notification" IS '通知模块';

-- ----------------------------
-- Records of notification
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qa_improve
-- ----------------------------
DROP TABLE IF EXISTS "public"."qa_improve";
CREATE TABLE "public"."qa_improve" (
  "id" int8 NOT NULL DEFAULT nextval('qa_improve_id_seq'::regclass),
  "question" text COLLATE "pg_catalog"."default",
  "answer" text COLLATE "pg_catalog"."default",
  "create_by" int4,
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "source_id" int2,
  "knowledge_id" int4,
  "knowledge_space_id" int4,
  "review_status" varchar(15) COLLATE "pg_catalog"."default" DEFAULT 'reviewing'::character varying,
  "generate_type" varchar(20) COLLATE "pg_catalog"."default",
  "question_vector" "public"."vector"
)
;
ALTER TABLE "public"."qa_improve" OWNER TO "postgres";
COMMENT ON COLUMN "public"."qa_improve"."question" IS '问题';
COMMENT ON COLUMN "public"."qa_improve"."source_id" IS '来源人id';
COMMENT ON COLUMN "public"."qa_improve"."knowledge_id" IS '知识库ID';
COMMENT ON COLUMN "public"."qa_improve"."knowledge_space_id" IS '知识空间ID';
COMMENT ON COLUMN "public"."qa_improve"."review_status" IS '审核状态';
COMMENT ON COLUMN "public"."qa_improve"."generate_type" IS '生成方式 upload/feedback/extra';
COMMENT ON TABLE "public"."qa_improve" IS 'QA优化';

-- ----------------------------
-- Records of qa_improve
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for search_dictionary
-- ----------------------------
DROP TABLE IF EXISTS "public"."search_dictionary";
CREATE TABLE "public"."search_dictionary" (
  "id" int8 NOT NULL DEFAULT nextval('search_dictionary_id_seq'::regclass),
  "word_type" int2 NOT NULL,
  "word" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "synonym" varchar(500) COLLATE "pg_catalog"."default",
  "correct_word" varchar(200) COLLATE "pg_catalog"."default",
  "weight" int4 NOT NULL DEFAULT 1,
  "status" int2 NOT NULL DEFAULT 1,
  "description" text COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "delete_flag" bool NOT NULL DEFAULT false,
  "dept_id" int8 NOT NULL
)
;
ALTER TABLE "public"."search_dictionary" OWNER TO "postgres";
COMMENT ON COLUMN "public"."search_dictionary"."word_type" IS '词库类型：1-专有名词，2-同义词，3-纠错词，4-敏感词，5-联想搜索词';
COMMENT ON COLUMN "public"."search_dictionary"."word" IS '词汇';
COMMENT ON COLUMN "public"."search_dictionary"."synonym" IS '同义词，多个用逗号分隔';
COMMENT ON COLUMN "public"."search_dictionary"."correct_word" IS '纠正词';
COMMENT ON COLUMN "public"."search_dictionary"."weight" IS '权重';
COMMENT ON COLUMN "public"."search_dictionary"."status" IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN "public"."search_dictionary"."description" IS '描述说明';
COMMENT ON COLUMN "public"."search_dictionary"."dept_id" IS '部门ID';
COMMENT ON TABLE "public"."search_dictionary" IS '搜索词库表';

-- ----------------------------
-- Records of search_dictionary
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for search_history
-- ----------------------------
DROP TABLE IF EXISTS "public"."search_history";
CREATE TABLE "public"."search_history" (
  "id" int8 NOT NULL DEFAULT nextval('search_history_id_seq'::regclass),
  "content" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "user_id" int8 NOT NULL,
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "doc" jsonb,
  "source" varchar(15) COLLATE "pg_catalog"."default",
  "knowledge_space_id" int4
)
;
ALTER TABLE "public"."search_history" OWNER TO "postgres";
COMMENT ON COLUMN "public"."search_history"."id" IS '主键ID';
COMMENT ON COLUMN "public"."search_history"."content" IS '搜索内容';
COMMENT ON COLUMN "public"."search_history"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."search_history"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."search_history"."doc" IS '搜索选中的文档';
COMMENT ON COLUMN "public"."search_history"."source" IS 'test_search测试搜索 vector_search检索搜索';
COMMENT ON COLUMN "public"."search_history"."knowledge_space_id" IS '知识库空间id';
COMMENT ON TABLE "public"."search_history" IS '用户搜索历史记录表';

-- ----------------------------
-- Records of search_history
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for search_logs
-- ----------------------------
DROP TABLE IF EXISTS "public"."search_logs";
CREATE TABLE "public"."search_logs" (
  "id" int8 NOT NULL DEFAULT nextval('search_logs_id_seq'::regclass),
  "user_id" varchar(100) COLLATE "pg_catalog"."default",
  "session_id" varchar(100) COLLATE "pg_catalog"."default",
  "query" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "knowledge_base_id" jsonb,
  "knowledge_space_id" int8,
  "suffix" varchar(50) COLLATE "pg_catalog"."default",
  "search_type" varchar(20) COLLATE "pg_catalog"."default" DEFAULT 'semantic'::character varying,
  "result_count" int4 DEFAULT 0,
  "response_time_ms" int4 DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."search_logs" OWNER TO "postgres";

-- ----------------------------
-- Records of search_logs
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for search_suggestion
-- ----------------------------
DROP TABLE IF EXISTS "public"."search_suggestion";
CREATE TABLE "public"."search_suggestion" (
  "id" int8 NOT NULL DEFAULT nextval('search_suggestion_id_seq'::regclass),
  "keyword" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "suggestion" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "search_count" int4 NOT NULL DEFAULT 0,
  "weight" int4 NOT NULL DEFAULT 1,
  "status" int2 NOT NULL DEFAULT 1,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "dept_id" int8 NOT NULL
)
;
ALTER TABLE "public"."search_suggestion" OWNER TO "postgres";
COMMENT ON COLUMN "public"."search_suggestion"."keyword" IS '关键词';
COMMENT ON COLUMN "public"."search_suggestion"."suggestion" IS '联想词';
COMMENT ON COLUMN "public"."search_suggestion"."search_count" IS '搜索次数';
COMMENT ON COLUMN "public"."search_suggestion"."weight" IS '权重';
COMMENT ON COLUMN "public"."search_suggestion"."status" IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN "public"."search_suggestion"."dept_id" IS '部门ID';
COMMENT ON TABLE "public"."search_suggestion" IS '搜索联想词表';

-- ----------------------------
-- Records of search_suggestion
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_comment
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_comment";
CREATE TABLE "public"."sys_comment" (
  "id" int8 NOT NULL DEFAULT nextval('sys_comment_id_seq'::regclass),
  "user_id" int8 NOT NULL,
  "target_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "target_type" int2 NOT NULL,
  "content" text COLLATE "pg_catalog"."default" NOT NULL,
  "like_count" int4 NOT NULL DEFAULT 0,
  "reply_count" int4 NOT NULL DEFAULT 0,
  "topped" bool NOT NULL DEFAULT false,
  "status" int2 NOT NULL DEFAULT 0,
  "ip_address" inet,
  "created_at" timestamp(6) NOT NULL,
  "updated_at" timestamp(6) NOT NULL
)
;
ALTER TABLE "public"."sys_comment" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_comment
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_comment_reply
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_comment_reply";
CREATE TABLE "public"."sys_comment_reply" (
  "id" int8 NOT NULL DEFAULT nextval('sys_comment_reply_id_seq'::regclass),
  "pid" int8 NOT NULL DEFAULT 0,
  "comment_id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "reply_to" int8,
  "content" text COLLATE "pg_catalog"."default" NOT NULL,
  "like_count" int4 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 0,
  "ip_address" inet,
  "created_at" timestamp(6) NOT NULL,
  "updated_at" timestamp(6) NOT NULL
)
;
ALTER TABLE "public"."sys_comment_reply" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_comment_reply
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_config";
CREATE TABLE "public"."sys_config" (
  "code" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "value" varchar(500) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "type" int2 NOT NULL DEFAULT 0,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_config" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_config
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_config" ("code", "value", "name", "type", "remark", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys.account.captchaOnOff', 'false', '账号自助-验证码开关', 0, '是否开启验证码功能（true开启，false关闭）', '2025-06-16 09:16:18.503553', 0, '2025-06-16 09:16:18.503553', 0, 0);
INSERT INTO "public"."sys_config" ("code", "value", "name", "type", "remark", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys.index.sideTheme', 'theme-dark', '主框架页-侧边栏主题', 0, '深色主题theme-dark，浅色主题theme-light', '2025-06-16 09:16:18.503553', 0, '2025-06-16 09:16:18.503553', 0, 0);
INSERT INTO "public"."sys_config" ("code", "value", "name", "type", "remark", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys.index.skinName', 'skin-blue', '主框架页-默认皮肤样式名称', 0, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', '2025-06-16 09:16:18.503553', 0, '2025-06-16 09:16:18.503553', 0, 0);
INSERT INTO "public"."sys_config" ("code", "value", "name", "type", "remark", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys.account.registerUser', 'true', '账号自助-是否开启用户注册功能', 0, '是否开启注册用户功能（true开启，false关闭）', '2025-07-19 19:43:51.455478', 1, '2025-06-16 09:16:18.503553', 0, 0);
INSERT INTO "public"."sys_config" ("code", "value", "name", "type", "remark", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys.user.initPassword', '123456', '用户管理-账号初始密码', 1, '初始化密码 1234567', '2025-08-18 06:01:11.729967', 1, '2025-06-16 09:16:18.503553', 0, 0);
INSERT INTO "public"."sys_config" ("code", "value", "name", "type", "remark", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys.user.initRole', '20', '用户管理-账号初始角色', 0, '初始角色12', '2025-08-18 06:02:10.03159', 1, '2025-07-19 04:34:47.280142', 0, 0);
INSERT INTO "public"."sys_config" ("code", "value", "name", "type", "remark", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('mineru_apikey', 'eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFM1MTIifQ.eyJqdGkiOiI3ODkwMDE5MiIsInJvbCI6IlJPTEVfUkVHSVNURVIiLCJpc3MiOiJPcGVuWExhYiIsImlhdCI6MTc2MjE2OTU3MywiY2xpZW50SWQiOiJsa3pkeDU3bnZ5MjJqa3BxOXgydyIsInBob25lIjoiMTMzOTEyNDg2MDUiLCJvcGVuSWQiOm51bGwsInV1aWQiOiI2MzRkZjIyMi02Yzk5LTQ5YmUtYTI5MS1lZWM3NzBhYmRlZjUiLCJlbWFpbCI6IiIsImV4cCI6MTc2MzM3OTE3M30.SpvzMSRG17E2MbE19oRgtpTwqsKV5ol02O79TP5FurB1kHvxaoJejA6FUnNhGWfZj0YFrkv-ypGyonM7m62wSg', 'mineru配置', 0, '', '2025-11-04 10:04:44.185981', 1, '2025-10-31 16:58:14.687119', 1, 0);
INSERT INTO "public"."sys_config" ("code", "value", "name", "type", "remark", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('my.index.chat', '1PnC_YwdLx', '知识库工作台助手', 0, '', '2025-11-10 17:03:43.787451', 1, '2025-07-25 11:08:31.554253', 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_dept";
CREATE TABLE "public"."sys_dept" (
  "id" int8 NOT NULL DEFAULT nextval('sys_dept_id_seq'::regclass),
  "pid" int8 NOT NULL DEFAULT 0,
  "ancestors" jsonb NOT NULL,
  "code" varchar(40) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(40) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "sort" int4 DEFAULT 0,
  "leader_id" int8,
  "phone" varchar(11) COLLATE "pg_catalog"."default",
  "email" varchar(50) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 0,
  "deleted" bool NOT NULL DEFAULT false,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_dept" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_dept" ("id", "pid", "ancestors", "code", "name", "sort", "leader_id", "phone", "email", "status", "deleted", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1, 0, '[]', 'first', '第一级团队', 0, NULL, NULL, NULL, 0, 'f', '2025-06-19 02:27:45.823023', 0, '2025-06-19 02:27:45.823023', 0, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_dict_data";
CREATE TABLE "public"."sys_dict_data" (
  "code" int8 NOT NULL DEFAULT nextval('sys_dict_data_code_seq'::regclass),
  "sort" int4 NOT NULL DEFAULT 0,
  "label" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "value" text COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "css_class" varchar(100) COLLATE "pg_catalog"."default",
  "list_class" varchar(100) COLLATE "pg_catalog"."default",
  "is_default" bool NOT NULL DEFAULT false,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 0,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0,
  "category" varchar(16) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."sys_dict_data" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1, 1, '男', '1', 'sys_user_sex', '', '', 't', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (2, 2, '女', '2', 'sys_user_sex', '', '', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (3, 3, '未知', '0', 'sys_user_sex', '', '', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 't', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (8, 1, '正常', 'false', 'sys_job_status', '', 'primary', 't', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (9, 2, '暂停', 'true', 'sys_job_status', '', 'danger', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 't', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (12, 1, '是', 'true', 'sys_yes_no', '', 'primary', 't', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (13, 2, '否', 'false', 'sys_yes_no', '', 'danger', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (14, 4, '通知', '4', 'sys_notify_type', '', 'warning', 't', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (15, 5, '公告', '5', 'sys_notify_type', '', 'success', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (16, 2, '成功', '2', 'sys_notify_status', '', 'primary', 't', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (17, 3, '失败', '3', 'sys_notify_status', '', 'danger', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (27, 1, '成功', 'false', 'sys_common_status', '', 'primary', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (28, 2, '失败', 'true', 'sys_common_status', '', 'danger', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (4, 1, '显示', 'false', 'sys_show_hide', '', 'primary', 't', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (5, 2, '隐藏', 'true', 'sys_show_hide', '', 'danger', 'f', NULL, 0, '2025-06-16 09:16:18.652844', 0, '2025-06-16 09:16:18.652844', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1009, 1, '工厂建设', 'factory_construction', 'ai_shop_subject', NULL, 'primary', 'f', NULL, 0, '2025-07-19 05:37:20.709206', 0, '2025-07-19 05:37:20.709206', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1010, 2, '产品研发', 'product_r_d', 'ai_shop_subject', NULL, 'primary', 'f', NULL, 0, '2025-07-19 05:37:20.709206', 0, '2025-07-19 05:37:20.709206', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1011, 3, '工艺设计', 'process_design', 'ai_shop_subject', NULL, 'primary', 'f', NULL, 0, '2025-07-19 05:37:20.709206', 0, '2025-07-19 05:37:20.709206', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1012, 4, '生产管理', 'production_management', 'ai_shop_subject', NULL, 'primary', 'f', NULL, 0, '2025-07-19 05:37:20.709206', 0, '2025-07-19 05:37:20.709206', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1013, 5, '生产作业', 'manufacturing', 'ai_shop_subject', NULL, 'primary', 'f', NULL, 0, '2025-07-19 05:37:20.709206', 0, '2025-07-19 05:37:20.709206', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1014, 6, '运营管理', 'operation_management', 'ai_shop_subject', NULL, 'primary', 'f', NULL, 0, '2025-07-19 05:37:20.709206', 0, '2025-07-19 05:37:20.709206', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1015, 7, '产品服务', 'product_service', 'ai_shop_subject', NULL, 'primary', 'f', NULL, 0, '2025-07-19 05:37:20.709206', 0, '2025-07-19 05:37:20.709206', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1016, 8, '供应链管理', 'supply_chain_management', 'ai_shop_subject', NULL, 'primary', 'f', NULL, 0, '2025-07-19 05:37:20.709206', 0, '2025-07-19 05:37:20.709206', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1000, 0, 'gte-rerank-v2', 'gte-rerank-v2', 'rerank_model', '', 'primary', 'f', 'gte-rerank-v2是多语言排序模型，用于提升检索与RAG效果。', 0, '2025-07-20 01:25:08.945892', 1, '2025-07-17 05:24:31.694633', 1, 1, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (9999, 0, 'KM小物', 'aff7cc5d72d54f4491d98b04e38e901e', '', NULL, NULL, 'f', NULL, 0, '2025-07-25 12:16:39.754608', 0, '2025-07-25 12:16:39.754608', 0, 0, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1019, 0, '文本生成', 'text_generation', 'ai_model_type', NULL, NULL, 'f', NULL, 0, '2025-08-20 14:08:36.193883', 1, '2025-08-20 14:08:36.193883', 1, 1, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1020, 0, '深度推理', 'deep_reasoning', 'ai_model_type', NULL, NULL, 'f', NULL, 0, '2025-08-20 14:08:43.210109', 1, '2025-08-20 14:08:43.210109', 1, 1, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1021, 0, '多模态', 'multimodal', 'ai_model_type', NULL, NULL, 'f', NULL, 0, '2025-08-20 14:08:49.678006', 1, '2025-08-20 14:08:49.678006', 1, 1, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1027, 0, '向量模型', 'embedding_model', 'ai_model_type', NULL, NULL, 'f', NULL, 0, '2025-08-20 14:09:26.912132', 1, '2025-08-20 14:09:26.912132', 1, 1, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1028, 0, '检索重排', 'text_rerank', 'ai_model_type', NULL, NULL, 'f', NULL, 0, '2025-08-20 14:09:32.770755', 1, '2025-08-20 14:09:32.770755', 1, 1, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1029, 1, 'Deepseek', 'deepseek', 'ai_llm_model', '', 'primary', 'f', '', 0, '2025-09-15 08:48:38.3195', 1, '2025-08-20 13:58:59.206582', 1, 1, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1031, 2, '通义千问', 'qwen', 'ai_llm_model', '', 'primary', 'f', '', 0, '2025-09-15 14:53:28.509436', 1, '2025-08-20 14:00:34.048157', 1, 1, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1030, 4, '智普', 'zhipu', 'ai_llm_model', '', 'primary', 'f', '', 0, '2025-10-14 15:01:25.829976', 1001, '2025-08-20 14:00:47.956641', 1, 1, NULL);
INSERT INTO "public"."sys_dict_data" ("code", "sort", "label", "value", "type", "css_class", "list_class", "is_default", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id", "category") VALUES (1032, 4, '月之暗面', 'moonshot', 'ai_llm_model', '', 'primary', 'f', '', 0, '2025-10-15 18:54:16.470577', 1, '2025-10-15 18:53:46.691463', 1, 0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_dict_type";
CREATE TABLE "public"."sys_dict_type" (
  "type" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "category" varchar(50) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 0,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_dict_type" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_user_sex', '用户性别', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_show_hide', '菜单状态', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_normal_disable', '系统开关', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_job_status', '任务状态', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_job_group', '任务分组', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_yes_no', '系统是否', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_notify_type', '通知类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_notify_status', '通知状态', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_oper_type', '操作类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_common_status', '系统状态', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('dashboard_refresh_interval', '时间间隔', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('ml_batch_status', '模型batch状态', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('ml_algorithm_type', '算法类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('ml_subset_type', '算法模型数据类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('ml_subset_data_from', '算法模型数据来源', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('emc_factory_level', '工厂层级', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('thing_property_data_type', '物模型属性数据类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('thing_property_method', '物模型属性访问模式', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('thing_type', '物模型类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('thing_status', '状态', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('thing_event_type', '物模型事件类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('alert_level', '报警级别', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('alert_source', '报警处理', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('alert_handle_status', '报警处理状态', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('emc_domain', '主题域', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('alert_send_status', '报警发送状态', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('alert_rule_type', '报警规则类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('thing_service_type', '物模型服务调用方式', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('rule_trigger_type', '触发器类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('rule_executor_type', '执行器类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('rule_things_type', '物模型类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('rule_operate_list', '操作符', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('rule_alert_type', '报警or消警', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('rule_notification_type', '通知途径', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('rule_pre_alert_type', '预定义报警类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('flow_trigger_type', '上报类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('sys_process_category', '工作流分类', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('low_code_form_type', '表单类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('alert_notify_config_type', '规则引擎通知类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('alert_notify_config_request_type', 'RequestType', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('alert_notify_config_auth', 'Auth', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('notify_template_type', '通知模板类别', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('alert_record_property', '报警记录属性', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('alert_notify_config_cron_defined', 'cron表达式类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('default_cron_expression', 'cron表达式', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('asset_equipment_lost_time', '失效时间', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('home_successful_case', '成功案例', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('home_my_task', '我的任务', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('home_recently_view', '最近访问', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('home_platform_monitor', '平台监控', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('equipment_alert_frequency', '设备报警展示区间', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('equipment_status', '资产有效状态', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('limit_time', '时限', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('trigger_imes_limit', '触发限制次数', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('event_mgt_template_type', '事件管理模板类别', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('workbench_task_type', '任务类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('notify_menu_type', '通知类型', 'system', NULL, 0, '2025-06-16 09:16:18.634974', 0, '2025-06-16 09:16:18.634974', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('rerank_model', '重排模型', 'system', NULL, 0, '2025-07-17 13:21:40', 0, '2025-07-17 05:22:16.904571', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('ai_shop_subject', 'AI商店主题', 'system', NULL, 0, '2025-07-19 05:35:40.180011', 0, '2025-07-19 05:35:40.180011', 0, 0);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('ai_llm_model', '模型厂商', '', '', 0, '2025-08-20 13:57:52.542918', 1, '2025-08-20 13:46:55.852303', 1, 1);
INSERT INTO "public"."sys_dict_type" ("type", "name", "category", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES ('ai_model_type', '模型类型', '', '', 0, '2025-08-20 13:57:57.156634', 1, '2025-08-20 13:47:07.977528', 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_job";
CREATE TABLE "public"."sys_job" (
  "id" int8 NOT NULL DEFAULT nextval('sys_job_id_seq'::regclass),
  "job_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "job_group" varchar(255) COLLATE "pg_catalog"."default",
  "trigger_type" int4,
  "trigger_interval" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "misfire_policy" int4,
  "concurrent" int4,
  "invoker_type" int4 NOT NULL,
  "invoker_info" text COLLATE "pg_catalog"."default",
  "invoker_args" text COLLATE "pg_catalog"."default",
  "status" int4,
  "del_flag" bool DEFAULT false,
  "created_by" varchar(64) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "updated_by" varchar(64) COLLATE "pg_catalog"."default",
  "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "dept_id" int4
)
;
ALTER TABLE "public"."sys_job" OWNER TO "postgres";
COMMENT ON COLUMN "public"."sys_job"."job_name" IS '任务名称';
COMMENT ON COLUMN "public"."sys_job"."job_group" IS '任务组名';
COMMENT ON COLUMN "public"."sys_job"."trigger_type" IS '触发类型 1.固定频率 2.Cron表达式';
COMMENT ON COLUMN "public"."sys_job"."trigger_interval" IS '触发间隔(Cron表达式、毫秒值)';
COMMENT ON COLUMN "public"."sys_job"."misfire_policy" IS 'cron计划策略';
COMMENT ON COLUMN "public"."sys_job"."concurrent" IS '是否并发执行（0允许 1禁止）';
COMMENT ON COLUMN "public"."sys_job"."invoker_type" IS '执行器类型 1.本地方法 2.远程方法';
COMMENT ON COLUMN "public"."sys_job"."invoker_info" IS '执行信息';
COMMENT ON COLUMN "public"."sys_job"."invoker_args" IS '执行参数';
COMMENT ON COLUMN "public"."sys_job"."status" IS '任务状态（0正常 1暂停）';
COMMENT ON COLUMN "public"."sys_job"."del_flag" IS '删除标记';
COMMENT ON COLUMN "public"."sys_job"."created_by" IS '创建人';
COMMENT ON COLUMN "public"."sys_job"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."sys_job"."updated_by" IS '更新人';
COMMENT ON COLUMN "public"."sys_job"."updated_at" IS '更新时间';
COMMENT ON TABLE "public"."sys_job" IS '定时任务调度表';

-- ----------------------------
-- Records of sys_job
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_job_log";
CREATE TABLE "public"."sys_job_log" (
  "job_log_id" int8 NOT NULL DEFAULT nextval('sys_job_log_job_log_id_seq'::regclass),
  "job_name" varchar(255) COLLATE "pg_catalog"."default",
  "job_group" varchar(255) COLLATE "pg_catalog"."default",
  "invoker_info" text COLLATE "pg_catalog"."default",
  "job_message" text COLLATE "pg_catalog"."default",
  "exception_info" text COLLATE "pg_catalog"."default",
  "status" int4,
  "trigger_time" timestamp(6),
  "completed_time" timestamp(6),
  "del_flag" bool DEFAULT false,
  "create_by" varchar(64) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_by" varchar(64) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."sys_job_log" OWNER TO "postgres";
COMMENT ON COLUMN "public"."sys_job_log"."job_log_id" IS '日志ID';
COMMENT ON COLUMN "public"."sys_job_log"."job_name" IS '任务名称';
COMMENT ON COLUMN "public"."sys_job_log"."job_group" IS '任务组名';
COMMENT ON COLUMN "public"."sys_job_log"."invoker_info" IS '调用目标字符串';
COMMENT ON COLUMN "public"."sys_job_log"."job_message" IS '日志信息';
COMMENT ON COLUMN "public"."sys_job_log"."exception_info" IS '异常信息';
COMMENT ON COLUMN "public"."sys_job_log"."status" IS '执行状态（0正常 1失败）';
COMMENT ON COLUMN "public"."sys_job_log"."trigger_time" IS '触发时间';
COMMENT ON COLUMN "public"."sys_job_log"."completed_time" IS '完成时间';
COMMENT ON COLUMN "public"."sys_job_log"."del_flag" IS '删除标记';
COMMENT ON COLUMN "public"."sys_job_log"."create_by" IS '创建人';
COMMENT ON COLUMN "public"."sys_job_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_job_log"."update_by" IS '更新人';
COMMENT ON COLUMN "public"."sys_job_log"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."sys_job_log" IS '定时任务调度日志表';

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_login_log";
CREATE TABLE "public"."sys_login_log" (
  "login_time" timestamp(6) NOT NULL,
  "user_id" int8 NOT NULL,
  "ipaddr" varchar(128) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "browser" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "os" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "msg" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "status" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_login_log" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_menu";
CREATE TABLE "public"."sys_menu" (
  "id" int8 NOT NULL DEFAULT nextval('sys_menu_id_seq'::regclass),
  "pid" int8 NOT NULL DEFAULT 0,
  "name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "sort" int4 NOT NULL DEFAULT 0,
  "path" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "component" varchar(255) COLLATE "pg_catalog"."default",
  "query" varchar(255) COLLATE "pg_catalog"."default",
  "menu_type" char(1) COLLATE "pg_catalog"."default" DEFAULT ''::bpchar,
  "hidden" bool NOT NULL DEFAULT false,
  "is_cache" bool NOT NULL DEFAULT false,
  "is_frame" bool NOT NULL DEFAULT false,
  "perms" varchar(100) COLLATE "pg_catalog"."default",
  "icon" varchar(100) COLLATE "pg_catalog"."default" DEFAULT '#'::character varying,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 0,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_menu" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (11, 1, '数据管理', 2, 'data', '', '', 'M', 'f', 'f', 'f', '', 'date-range', NULL, 0, '2025-08-12 01:40:05.794409', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (3001, 0, '工作空间', 1, 'home', NULL, NULL, 'M', 'f', 'f', 'f', NULL, 'home', NULL, 0, '2025-08-11 09:16:55.647742', 0, '2025-08-11 09:16:55.647742', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (3101, 3001, '我的工作台', 1, 'index', 'index', NULL, 'C', 'f', 'f', 'f', NULL, 'home', NULL, 0, '2025-08-11 09:16:55.647742', 0, '2025-08-11 09:16:55.647742', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (10, 1, '权限管理', 0, 'permission', '', '', 'M', 'f', 'f', 'f', '', 'education', NULL, 0, '2025-08-12 01:40:14.118895', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (3102, 3001, '知识专家', 2, 'specialist', 'specialist/index', NULL, 'C', 'f', 'f', 'f', NULL, 'staff', NULL, 0, '2025-08-11 09:16:55.647742', 0, '2025-08-11 09:16:55.647742', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (3002, 0, '知识库', 2, 'space', NULL, NULL, 'M', 'f', 'f', 'f', NULL, '', NULL, 0, '2025-08-11 09:16:55.647742', 0, '2025-08-11 09:16:55.647742', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (3106, 3002, '词库设置', 6, 'thesaurus', 'thesaurus/index', NULL, 'C', 'f', 'f', 'f', NULL, 'data_model', NULL, 0, '2025-08-11 09:16:55.647742', 0, '2025-08-11 09:16:55.647742', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1, 0, '系统管理', 999, 'system', '', '', 'M', 'f', 'f', 'f', '', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1001, 100, '用户查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:user:query', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1002, 100, '用户新增', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:user:add', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1003, 100, '用户修改', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:user:edit', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1004, 100, '用户删除', 4, '#', '', '', 'F', 'f', 'f', 'f', 'system:user:remove', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1005, 100, '用户导出', 5, '#', '', '', 'F', 'f', 'f', 'f', 'system:user:export', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1006, 100, '用户导入', 6, '#', '', '', 'F', 'f', 'f', 'f', 'system:user:import', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1007, 100, '重置密码', 7, '#', '', '', 'F', 'f', 'f', 'f', 'system:user:resetPwd', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (100, 10, '用户管理', 0, 'user', 'system/user/index', '', 'C', 'f', 'f', 'f', 'system:user:list', '#', NULL, 0, '2025-08-12 01:37:47.392053', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (101, 10, '角色管理', 1, 'role', 'system/role/index', '', 'C', 'f', 'f', 'f', 'system:role:list', '#', NULL, 0, '2025-08-12 01:37:51.79067', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (108, 10, '通知', 7, 'notice', 'system/notice/index', '', 'C', 't', 'f', 'f', 'system:notice:list', '#', NULL, 1, '2025-08-12 01:38:14.548674', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (110, 10, '日志', 8, 'log', 'system/log/index', '', 'M', 't', 'f', 'f', '', '#', NULL, 1, '2025-08-12 01:38:22.125388', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (107, 10, '定时任务', 9, '#', 'system/job/index', '', 'C', 't', 'f', 'f', 'system:job:list', '#', NULL, 1, '2025-08-12 01:38:29.893448', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1009, 101, '角色新增', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:role:add', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1010, 101, '角色修改', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:role:edit', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1011, 101, '角色删除', 4, '#', '', '', 'F', 'f', 'f', 'f', 'system:role:remove', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1012, 101, '角色导出', 5, '#', '', '', 'F', 'f', 'f', 'f', 'system:role:export', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (103, 10, '部门管理', 3, 'dept', 'system/dept/index', '', 'C', 'f', 'f', 'f', 'system:dept:list', '#', NULL, 0, '2025-08-12 01:38:01.148604', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1013, 102, '菜单查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:menu:query', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1014, 102, '菜单新增', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:menu:add', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1015, 102, '菜单修改', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:menu:edit', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1016, 102, '菜单删除', 4, '#', '', '', 'F', 'f', 'f', 'f', 'system:menu:remove', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (104, 10, '岗位管理', 4, 'post', 'system/post/index', '', 'C', 'f', 'f', 'f', 'system:post:list', '#', NULL, 0, '2025-08-12 01:38:05.334709', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1017, 103, '部门查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:dept:query', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1018, 103, '部门新增', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:dept:add', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1020, 103, '部门删除', 4, '#', '', '', 'F', 'f', 'f', 'f', 'system:dept:remove', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (105, 11, '字典管理', 5, 'dict', 'system/dict/index', '', 'C', 'f', 'f', 'f', 'system:dict:list', '#', NULL, 0, '2025-08-12 01:38:36.663543', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1021, 104, '岗位查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:post:query', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1022, 104, '岗位新增', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:post:add', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1023, 104, '岗位修改', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:post:edit', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1024, 104, '岗位删除', 4, '#', '', '', 'F', 'f', 'f', 'f', 'system:post:remove', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1025, 104, '岗位导出', 5, '#', '', '', 'F', 'f', 'f', 'f', 'system:post:export', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (106, 11, '参数管理', 6, 'config', 'system/config/index', '', 'C', 'f', 'f', 'f', 'system:config:list', '#', NULL, 0, '2025-08-12 01:38:40.564766', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1026, 105, '字典查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:dict:query', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1027, 105, '字典新增', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:dict:add', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1028, 105, '字典修改', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:dict:edit', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1029, 105, '字典删除', 4, '#', '', '', 'F', 'f', 'f', 'f', 'system:dict:remove', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1030, 105, '字典导出', 5, '#', '', '', 'F', 'f', 'f', 'f', 'system:dict:export', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1031, 106, '参数查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:config:query', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1032, 106, '参数新增', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:config:add', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1033, 106, '参数修改', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:config:edit', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1034, 106, '参数删除', 4, '#', '', '', 'F', 'f', 'f', 'f', 'system:config:remove', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1035, 106, '参数导出', 5, '#', '', '', 'F', 'f', 'f', 'f', 'system:config:export', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (3104, 3002, '知识管理', 2, 'knowledge', 'knowledge/index', NULL, 'C', 'f', 'f', 'f', NULL, 'component', NULL, 0, '2025-10-14 14:37:21.077365', 1001, '2025-08-11 09:16:55.647742', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (3105, 3002, '命中测试', 5, 'hittesting', 'hittesting/index', NULL, 'C', 't', 'f', 'f', NULL, 'button', NULL, 0, '2025-10-21 17:06:00.812595', 1, '2025-08-11 09:16:55.647742', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (3107, 3002, '知识设置', 7, 'setup', 'setup/index', NULL, 'C', 'f', 'f', 'f', NULL, 'standard', NULL, 1, '2025-10-22 15:10:03.762726', 1001, '2025-08-11 09:16:55.647742', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1036, 107, '任务查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:job:query', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1037, 107, '任务新增', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:job:add', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1038, 107, '任务修改', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:job:edit', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1039, 107, '任务删除', 4, '#', '', '', 'F', 'f', 'f', 'f', 'system:job:remove', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1040, 107, '状态修改', 5, '#', '', '', 'F', 'f', 'f', 'f', 'system:job:changeStatus', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1041, 107, '任务导出', 7, '#', '', '', 'F', 'f', 'f', 'f', 'system:job:export', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1042, 108, '公告查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:notice:query', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1043, 108, '公告新增', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:notice:add', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1044, 108, '公告修改', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:notice:edit', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1045, 108, '公告删除', 4, '#', '', '', 'F', 'f', 'f', 'f', 'system:notice:remove', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1046, 500, '日志查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:operlog:query', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1047, 500, '日志删除', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:operlog:remove', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1048, 500, '日志导出', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:operlog:export', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (501, 110, '登录日志', 2, 'loginlog', 'system/loginlog/index', '', 'C', 'f', 'f', 'f', 'system:loginlog:list', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1049, 501, '日志查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:loginlog:query', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1050, 501, '日志删除', 2, '#', '', '', 'F', 'f', 'f', 'f', 'system:loginlog:remove', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1051, 501, '日志导出', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:loginlog:export', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (102, 10, '菜单管理', 2, 'menu', 'system/menu/index', '', 'C', 'f', 'f', 'f', 'system:menu:list', '#', NULL, 0, '2025-08-12 01:37:57.063553', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1008, 101, '角色查询', 1, '#', '', '', 'F', 'f', 'f', 'f', 'system:role:query', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1019, 103, '部门修改', 3, '#', '', '', 'F', 'f', 'f', 'f', 'system:dept:edit', '#', NULL, 0, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (500, 110, '操作日志', 1, 'operlog', 'system/operlog/index', '', 'C', 'f', 'f', 'f', 'system:operlog:list', '#', NULL, 1, '2025-08-11 09:17:06.336547', 0, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (135, 10, '案例', 0, 'case', 'system/case/index', NULL, 'C', 't', 'f', 'f', 'system:case:list', '#', NULL, 1, '2025-08-12 01:37:29.381507', 1, '2025-08-11 09:17:06.336547', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (3103, 3002, '知识检索', 1, 'retrieve', 'retrieve/index', NULL, 'C', 'f', 'f', 'f', NULL, 'integration', NULL, 0, '2025-10-14 14:37:15.700457', 1001, '2025-08-11 09:16:55.647742', 0, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (521, 3002, '问答优化', 8, 'qa', 'qa/index', NULL, 'C', 'f', 't', 'f', NULL, 'import_w', NULL, 0, '2025-10-14 17:12:41.968203', 1001, '2025-10-14 16:43:23.144297', 1001, 0);
INSERT INTO "public"."sys_menu" ("id", "pid", "name", "sort", "path", "component", "query", "menu_type", "hidden", "is_cache", "is_frame", "perms", "icon", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (523, 1, '模型管理', 3, 'modelOpt', 'modelOpt/index', NULL, 'C', 'f', 't', 'f', NULL, 'etl', NULL, 0, '2025-10-28 08:35:09.682624', 1001, '2025-10-21 13:51:23.910815', 1, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_notice";
CREATE TABLE "public"."sys_notice" (
  "id" int8 NOT NULL DEFAULT nextval('sys_notice_id_seq'::regclass),
  "title" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "summary" varchar(255) COLLATE "pg_catalog"."default",
  "content" text COLLATE "pg_catalog"."default",
  "type" int2 NOT NULL,
  "level" int2 NOT NULL DEFAULT 1,
  "menu" varchar(255) COLLATE "pg_catalog"."default",
  "item" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0,
  "target_id" varchar(32) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."sys_notice" OWNER TO "postgres";
COMMENT ON COLUMN "public"."sys_notice"."target_id" IS '目标uuid';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_oper_log";
CREATE TABLE "public"."sys_oper_log" (
  "oper_time" timestamp(6) NOT NULL,
  "cost_time" int4 NOT NULL,
  "dept_id" int8 NOT NULL DEFAULT 0,
  "oper_id" int8 NOT NULL DEFAULT 0,
  "oper_ip" varchar(128) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "method" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "oper_url" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "oper_param" text COLLATE "pg_catalog"."default",
  "json_result" text COLLATE "pg_catalog"."default",
  "error_msg" text COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_oper_log" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_post";
CREATE TABLE "public"."sys_post" (
  "id" int8 NOT NULL DEFAULT nextval('sys_post_id_seq'::regclass),
  "code" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "sort" int4 NOT NULL DEFAULT 0,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 0,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_post" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_post
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_post" ("id", "code", "name", "sort", "remark", "status", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1, 'post', '第一岗位', 0, '', 0, '2025-06-19 10:29:32.182507', 1, '2025-06-19 10:29:32.182507', 1, 2);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role";
CREATE TABLE "public"."sys_role" (
  "id" int8 NOT NULL DEFAULT nextval('sys_role_id_seq'::regclass),
  "name" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
  "code" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "sort" int4 NOT NULL DEFAULT 0,
  "data_scope" int2 NOT NULL DEFAULT 1,
  "menu_check_strictly" bool NOT NULL DEFAULT true,
  "dept_check_strictly" bool NOT NULL DEFAULT true,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 0,
  "deleted" bool NOT NULL DEFAULT false,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_role" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_role" ("id", "name", "code", "sort", "data_scope", "menu_check_strictly", "dept_check_strictly", "remark", "status", "deleted", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1, '超级管理员', 'SUPER_ADMIN', 1, 1, 't', 't', NULL, 0, 'f', '2025-08-11 03:23:37.879234', 0, '2025-08-11 03:23:37.879234', 0, 0);
INSERT INTO "public"."sys_role" ("id", "name", "code", "sort", "data_scope", "menu_check_strictly", "dept_check_strictly", "remark", "status", "deleted", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (20, '租户访客', 'GUEST', 5, 1, 't', 't', '', 0, 'f', '2025-10-21 17:03:19.029702', 1001, '2025-08-11 03:23:37.879234', 0, 0);
INSERT INTO "public"."sys_role" ("id", "name", "code", "sort", "data_scope", "menu_check_strictly", "dept_check_strictly", "remark", "status", "deleted", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (2, '系统管理员', 'SYSTEM_ADMIN', 2, 1, 't', 't', '', 0, 'f', '2025-10-22 15:11:26.58876', 1001, '2025-08-11 03:23:37.879234', 0, 0);
INSERT INTO "public"."sys_role" ("id", "name", "code", "sort", "data_scope", "menu_check_strictly", "dept_check_strictly", "remark", "status", "deleted", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (10, '租户管理员', 'ADMIN', 3, 1, 't', 't', '', 0, 'f', '2025-10-22 15:11:39.782967', 1001, '2025-08-11 03:23:37.879234', 0, 0);
INSERT INTO "public"."sys_role" ("id", "name", "code", "sort", "data_scope", "menu_check_strictly", "dept_check_strictly", "remark", "status", "deleted", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (15, '租户用户', 'USER', 4, 1, 't', 't', '', 0, 'f', '2025-11-03 18:48:09.963062', 1, '2025-08-11 03:23:37.879234', 0, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role_dept";
CREATE TABLE "public"."sys_role_dept" (
  "role_id" int8 NOT NULL,
  "dept_id" int8 NOT NULL
)
;
ALTER TABLE "public"."sys_role_dept" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role_menu";
CREATE TABLE "public"."sys_role_menu" (
  "role_id" int8 NOT NULL,
  "menu_id" int8 NOT NULL
)
;
ALTER TABLE "public"."sys_role_menu" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_role_menu" ("role_id", "menu_id") VALUES (1, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_terminal
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_terminal";
CREATE TABLE "public"."sys_terminal" (
  "terminal_id" char(32) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "status_time" timestamp(6),
  "status_detail" json,
  "authorized" bool NOT NULL DEFAULT false,
  "token" varchar(255) COLLATE "pg_catalog"."default",
  "address" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "status" int2 NOT NULL DEFAULT 0,
  "deleted" bool NOT NULL DEFAULT false,
  "updated_at" timestamp(6) NOT NULL,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_terminal" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_terminal
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user";
CREATE TABLE "public"."sys_user" (
  "id" int8 NOT NULL DEFAULT nextval('sys_user_id_seq'::regclass),
  "username" varchar(16) COLLATE "pg_catalog"."default",
  "phone_number" char(11) COLLATE "pg_catalog"."default",
  "email" varchar(32) COLLATE "pg_catalog"."default",
  "password" char(60) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::bpchar,
  "nickname" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "avatar" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "gender" int2 NOT NULL DEFAULT 0,
  "login_ip" varchar(128) COLLATE "pg_catalog"."default",
  "login_time" timestamp(6),
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 0,
  "deleted" bool NOT NULL DEFAULT false,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_user" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_user" ("id", "username", "phone_number", "email", "password", "nickname", "avatar", "gender", "login_ip", "login_time", "remark", "status", "deleted", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1, 'wudao', NULL, 'admin@wudao.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'wudao', '/profile/avatar/admin.jpeg', 0, '', '2025-11-13 16:03:51.395379', NULL, 0, 'f', '2025-11-13 16:03:51.396597', 1, '2025-08-18 04:31:36.05451', 0, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_collect
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_collect";
CREATE TABLE "public"."sys_user_collect" (
  "user_id" int8 NOT NULL,
  "target_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "target_type" int2 NOT NULL DEFAULT 0,
  "oper_type" int2 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."sys_user_collect" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_user_collect
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_user_notice
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_notice";
CREATE TABLE "public"."sys_user_notice" (
  "user_id" int8 NOT NULL,
  "notice_id" int8 NOT NULL,
  "unread" bool NOT NULL DEFAULT true
)
;
ALTER TABLE "public"."sys_user_notice" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_user_notice
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_user_oauth
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_oauth";
CREATE TABLE "public"."sys_user_oauth" (
  "user_id" int8 NOT NULL,
  "source" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "uuid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "nickname" varchar(30) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "gender" int2 NOT NULL DEFAULT 0,
  "username" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
  "email" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "avatar" varchar(500) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "blog" varchar(500) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "company" varchar(500) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "location" varchar(500) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "remark" varchar(500) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "access_token" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "expire_in" int8,
  "refresh_token" varchar(255) COLLATE "pg_catalog"."default",
  "refresh_token_expire_in" int8,
  "extra" jsonb NOT NULL DEFAULT '{}'::jsonb,
  "updated_at" timestamp(6) NOT NULL,
  "updated_by" int8 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL,
  "created_by" int8 NOT NULL DEFAULT 0,
  "dept_id" int8 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."sys_user_oauth" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_user_oauth
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_user_oauth" ("user_id", "source", "uuid", "nickname", "gender", "username", "email", "avatar", "blog", "company", "location", "remark", "access_token", "expire_in", "refresh_token", "refresh_token_expire_in", "extra", "updated_at", "updated_by", "created_at", "created_by", "dept_id") VALUES (1, 'wudao', '1', 'wudao', 1, 'wudao', 'admin@wudao.com', '/profile/avatar/admin.jpeg', '', '', '', '', 'eyJhbGciOiJIUzUxMiJ9.eyJkZXB0SWQiOiIxIiwiZXhwaXJ5VGltZSI6IjE3NjMwNDM1OTciLCJsb2dpblRpbWUiOiIxNzYzMDAwMzk3IiwidGVuYW50SWQiOjAsInVzZXJJZCI6IjEifQ.ZM1hQTogELZF_PuS6IhJSrNf0FCha0gtLDJzkcfzkr0ny8wTTvtgQiS-QvGbXQJM1hC3_7A1SYvbmbjHbNPDiQ', 0, NULL, 0, '{"expireIn": 0, "tokenType": "Bearer", "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJkZXB0SWQiOiIxIiwiZXhwaXJ5VGltZSI6IjE3NjMwNDM1OTciLCJsb2dpblRpbWUiOiIxNzYzMDAwMzk3IiwidGVuYW50SWQiOjAsInVzZXJJZCI6IjEifQ.ZM1hQTogELZF_PuS6IhJSrNf0FCha0gtLDJzkcfzkr0ny8wTTvtgQiS-QvGbXQJM1hC3_7A1SYvbmbjHbNPDiQ", "snapshotUser": false, "refreshTokenExpireIn": 0}', '2025-11-13 10:19:57.920593', 1, '2025-08-15 06:49:14.31844', 0, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_post";
CREATE TABLE "public"."sys_user_post" (
  "user_id" int8 NOT NULL,
  "post_id" int8 NOT NULL
)
;
ALTER TABLE "public"."sys_user_post" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_user_post" ("user_id", "post_id") VALUES (1, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_role";
CREATE TABLE "public"."sys_user_role" (
  "user_id" int8 NOT NULL,
  "role_id" int8 NOT NULL
)
;
ALTER TABLE "public"."sys_user_role" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO "public"."sys_user_role" ("user_id", "role_id") VALUES (1, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_storage
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_storage";
CREATE TABLE "public"."sys_user_storage" (
  "user_id" int8 NOT NULL,
  "key" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "value" jsonb NOT NULL,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."sys_user_storage" OWNER TO "postgres";

-- ----------------------------
-- Records of sys_user_storage
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for system_dictionary
-- ----------------------------
DROP TABLE IF EXISTS "public"."system_dictionary";
CREATE TABLE "public"."system_dictionary" (
  "id" int8 NOT NULL DEFAULT nextval('system_dictionary_id_seq'::regclass),
  "dict_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "dict_key" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "dict_value" varchar(500) COLLATE "pg_catalog"."default",
  "sort_order" int4 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "created_by" int8,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_by" int8,
  "delete_flag" bool NOT NULL DEFAULT false,
  "dept_id" int4,
  "space_id" int4
)
;
ALTER TABLE "public"."system_dictionary" OWNER TO "postgres";
COMMENT ON COLUMN "public"."system_dictionary"."id" IS '主键ID';
COMMENT ON COLUMN "public"."system_dictionary"."dict_type" IS '词库类型：PROPER_NOUN-专有名词，SYNONYM-同义词，CORRECTION-纠错词，SENSITIVE-敏感词，SUGGESTION-搜索联想词';
COMMENT ON COLUMN "public"."system_dictionary"."dict_key" IS '词库键（纠错词的错误词，其他类型的词本身）';
COMMENT ON COLUMN "public"."system_dictionary"."dict_value" IS '词库值（纠错词的正确词，同义词的同义词组，其他类型可为空）';
COMMENT ON COLUMN "public"."system_dictionary"."sort_order" IS '排序';
COMMENT ON COLUMN "public"."system_dictionary"."status" IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN "public"."system_dictionary"."created_at" IS '创建时间';
COMMENT ON COLUMN "public"."system_dictionary"."created_by" IS '创建人';
COMMENT ON COLUMN "public"."system_dictionary"."updated_at" IS '更新时间';
COMMENT ON COLUMN "public"."system_dictionary"."updated_by" IS '更新人';
COMMENT ON COLUMN "public"."system_dictionary"."delete_flag" IS '删除标记：false-未删除，true-已删除';
COMMENT ON COLUMN "public"."system_dictionary"."dept_id" IS '部门id';
COMMENT ON COLUMN "public"."system_dictionary"."space_id" IS '知识空间id';
COMMENT ON TABLE "public"."system_dictionary" IS '系统词库表';

-- ----------------------------
-- Records of system_dictionary
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Function structure for array_to_halfvec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_halfvec"(_numeric, int4, bool);
CREATE FUNCTION "public"."array_to_halfvec"(_numeric, int4, bool)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'array_to_halfvec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_halfvec"(_numeric, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_halfvec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_halfvec"(_float4, int4, bool);
CREATE FUNCTION "public"."array_to_halfvec"(_float4, int4, bool)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'array_to_halfvec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_halfvec"(_float4, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_halfvec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_halfvec"(_float8, int4, bool);
CREATE FUNCTION "public"."array_to_halfvec"(_float8, int4, bool)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'array_to_halfvec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_halfvec"(_float8, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_halfvec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_halfvec"(_int4, int4, bool);
CREATE FUNCTION "public"."array_to_halfvec"(_int4, int4, bool)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'array_to_halfvec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_halfvec"(_int4, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_sparsevec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_sparsevec"(_float8, int4, bool);
CREATE FUNCTION "public"."array_to_sparsevec"(_float8, int4, bool)
  RETURNS "public"."sparsevec" AS '$libdir/vector', 'array_to_sparsevec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_sparsevec"(_float8, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_sparsevec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_sparsevec"(_float4, int4, bool);
CREATE FUNCTION "public"."array_to_sparsevec"(_float4, int4, bool)
  RETURNS "public"."sparsevec" AS '$libdir/vector', 'array_to_sparsevec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_sparsevec"(_float4, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_sparsevec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_sparsevec"(_numeric, int4, bool);
CREATE FUNCTION "public"."array_to_sparsevec"(_numeric, int4, bool)
  RETURNS "public"."sparsevec" AS '$libdir/vector', 'array_to_sparsevec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_sparsevec"(_numeric, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_sparsevec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_sparsevec"(_int4, int4, bool);
CREATE FUNCTION "public"."array_to_sparsevec"(_int4, int4, bool)
  RETURNS "public"."sparsevec" AS '$libdir/vector', 'array_to_sparsevec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_sparsevec"(_int4, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_vector
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_vector"(_float8, int4, bool);
CREATE FUNCTION "public"."array_to_vector"(_float8, int4, bool)
  RETURNS "public"."vector" AS '$libdir/vector', 'array_to_vector'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_vector"(_float8, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_vector
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_vector"(_int4, int4, bool);
CREATE FUNCTION "public"."array_to_vector"(_int4, int4, bool)
  RETURNS "public"."vector" AS '$libdir/vector', 'array_to_vector'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_vector"(_int4, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_vector
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_vector"(_float4, int4, bool);
CREATE FUNCTION "public"."array_to_vector"(_float4, int4, bool)
  RETURNS "public"."vector" AS '$libdir/vector', 'array_to_vector'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_vector"(_float4, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for array_to_vector
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."array_to_vector"(_numeric, int4, bool);
CREATE FUNCTION "public"."array_to_vector"(_numeric, int4, bool)
  RETURNS "public"."vector" AS '$libdir/vector', 'array_to_vector'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."array_to_vector"(_numeric, int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for binary_quantize
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."binary_quantize"("public"."halfvec");
CREATE FUNCTION "public"."binary_quantize"("public"."halfvec")
  RETURNS "pg_catalog"."bit" AS '$libdir/vector', 'halfvec_binary_quantize'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."binary_quantize"("public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for binary_quantize
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."binary_quantize"("public"."vector");
CREATE FUNCTION "public"."binary_quantize"("public"."vector")
  RETURNS "pg_catalog"."bit" AS '$libdir/vector', 'binary_quantize'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."binary_quantize"("public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for cosine_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."cosine_distance"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."cosine_distance"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'halfvec_cosine_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."cosine_distance"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for cosine_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."cosine_distance"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."cosine_distance"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'cosine_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."cosine_distance"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for cosine_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."cosine_distance"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."cosine_distance"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'sparsevec_cosine_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."cosine_distance"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec"("public"."halfvec", int4, bool);
CREATE FUNCTION "public"."halfvec"("public"."halfvec", int4, bool)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'halfvec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec"("public"."halfvec", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_accum
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_accum"(_float8, "public"."halfvec");
CREATE FUNCTION "public"."halfvec_accum"(_float8, "public"."halfvec")
  RETURNS "pg_catalog"."_float8" AS '$libdir/vector', 'halfvec_accum'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_accum"(_float8, "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_add
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_add"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_add"("public"."halfvec", "public"."halfvec")
  RETURNS "public"."halfvec" AS '$libdir/vector', 'halfvec_add'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_add"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_avg
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_avg"(_float8);
CREATE FUNCTION "public"."halfvec_avg"(_float8)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'halfvec_avg'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_avg"(_float8) OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_cmp
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_cmp"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_cmp"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."int4" AS '$libdir/vector', 'halfvec_cmp'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_cmp"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_combine
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_combine"(_float8, _float8);
CREATE FUNCTION "public"."halfvec_combine"(_float8, _float8)
  RETURNS "pg_catalog"."_float8" AS '$libdir/vector', 'vector_combine'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_combine"(_float8, _float8) OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_concat
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_concat"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_concat"("public"."halfvec", "public"."halfvec")
  RETURNS "public"."halfvec" AS '$libdir/vector', 'halfvec_concat'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_concat"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_eq
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_eq"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_eq"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'halfvec_eq'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_eq"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_ge
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_ge"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_ge"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'halfvec_ge'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_ge"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_gt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_gt"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_gt"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'halfvec_gt'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_gt"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_in
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_in"(cstring, oid, int4);
CREATE FUNCTION "public"."halfvec_in"(cstring, oid, int4)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'halfvec_in'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_in"(cstring, oid, int4) OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_l2_squared_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_l2_squared_distance"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_l2_squared_distance"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'halfvec_l2_squared_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_l2_squared_distance"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_le
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_le"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_le"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'halfvec_le'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_le"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_lt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_lt"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_lt"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'halfvec_lt'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_lt"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_mul
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_mul"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_mul"("public"."halfvec", "public"."halfvec")
  RETURNS "public"."halfvec" AS '$libdir/vector', 'halfvec_mul'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_mul"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_ne
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_ne"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_ne"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'halfvec_ne'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_ne"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_negative_inner_product
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_negative_inner_product"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_negative_inner_product"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'halfvec_negative_inner_product'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_negative_inner_product"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_out
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_out"("public"."halfvec");
CREATE FUNCTION "public"."halfvec_out"("public"."halfvec")
  RETURNS "pg_catalog"."cstring" AS '$libdir/vector', 'halfvec_out'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_out"("public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_recv
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_recv"(internal, oid, int4);
CREATE FUNCTION "public"."halfvec_recv"(internal, oid, int4)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'halfvec_recv'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_recv"(internal, oid, int4) OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_send
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_send"("public"."halfvec");
CREATE FUNCTION "public"."halfvec_send"("public"."halfvec")
  RETURNS "pg_catalog"."bytea" AS '$libdir/vector', 'halfvec_send'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_send"("public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_spherical_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_spherical_distance"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_spherical_distance"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'halfvec_spherical_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_spherical_distance"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_sub
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_sub"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."halfvec_sub"("public"."halfvec", "public"."halfvec")
  RETURNS "public"."halfvec" AS '$libdir/vector', 'halfvec_sub'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_sub"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_to_float4
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_to_float4"("public"."halfvec", int4, bool);
CREATE FUNCTION "public"."halfvec_to_float4"("public"."halfvec", int4, bool)
  RETURNS "pg_catalog"."_float4" AS '$libdir/vector', 'halfvec_to_float4'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_to_float4"("public"."halfvec", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_to_sparsevec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_to_sparsevec"("public"."halfvec", int4, bool);
CREATE FUNCTION "public"."halfvec_to_sparsevec"("public"."halfvec", int4, bool)
  RETURNS "public"."sparsevec" AS '$libdir/vector', 'halfvec_to_sparsevec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_to_sparsevec"("public"."halfvec", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_to_vector
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_to_vector"("public"."halfvec", int4, bool);
CREATE FUNCTION "public"."halfvec_to_vector"("public"."halfvec", int4, bool)
  RETURNS "public"."vector" AS '$libdir/vector', 'halfvec_to_vector'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_to_vector"("public"."halfvec", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for halfvec_typmod_in
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."halfvec_typmod_in"(_cstring);
CREATE FUNCTION "public"."halfvec_typmod_in"(_cstring)
  RETURNS "pg_catalog"."int4" AS '$libdir/vector', 'halfvec_typmod_in'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."halfvec_typmod_in"(_cstring) OWNER TO "postgres";

-- ----------------------------
-- Function structure for hamming_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."hamming_distance"(bit, bit);
CREATE FUNCTION "public"."hamming_distance"(bit, bit)
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'hamming_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."hamming_distance"(bit, bit) OWNER TO "postgres";

-- ----------------------------
-- Function structure for hnsw_bit_support
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."hnsw_bit_support"(internal);
CREATE FUNCTION "public"."hnsw_bit_support"(internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/vector', 'hnsw_bit_support'
  LANGUAGE c VOLATILE
  COST 1;
ALTER FUNCTION "public"."hnsw_bit_support"(internal) OWNER TO "postgres";

-- ----------------------------
-- Function structure for hnsw_halfvec_support
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."hnsw_halfvec_support"(internal);
CREATE FUNCTION "public"."hnsw_halfvec_support"(internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/vector', 'hnsw_halfvec_support'
  LANGUAGE c VOLATILE
  COST 1;
ALTER FUNCTION "public"."hnsw_halfvec_support"(internal) OWNER TO "postgres";

-- ----------------------------
-- Function structure for hnsw_sparsevec_support
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."hnsw_sparsevec_support"(internal);
CREATE FUNCTION "public"."hnsw_sparsevec_support"(internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/vector', 'hnsw_sparsevec_support'
  LANGUAGE c VOLATILE
  COST 1;
ALTER FUNCTION "public"."hnsw_sparsevec_support"(internal) OWNER TO "postgres";

-- ----------------------------
-- Function structure for hnswhandler
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."hnswhandler"(internal);
CREATE FUNCTION "public"."hnswhandler"(internal)
  RETURNS "pg_catalog"."index_am_handler" AS '$libdir/vector', 'hnswhandler'
  LANGUAGE c VOLATILE
  COST 1;
ALTER FUNCTION "public"."hnswhandler"(internal) OWNER TO "postgres";

-- ----------------------------
-- Function structure for inner_product
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."inner_product"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."inner_product"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'inner_product'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."inner_product"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for inner_product
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."inner_product"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."inner_product"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'halfvec_inner_product'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."inner_product"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for inner_product
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."inner_product"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."inner_product"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'sparsevec_inner_product'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."inner_product"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for ivfflat_bit_support
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."ivfflat_bit_support"(internal);
CREATE FUNCTION "public"."ivfflat_bit_support"(internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/vector', 'ivfflat_bit_support'
  LANGUAGE c VOLATILE
  COST 1;
ALTER FUNCTION "public"."ivfflat_bit_support"(internal) OWNER TO "postgres";

-- ----------------------------
-- Function structure for ivfflat_halfvec_support
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."ivfflat_halfvec_support"(internal);
CREATE FUNCTION "public"."ivfflat_halfvec_support"(internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/vector', 'ivfflat_halfvec_support'
  LANGUAGE c VOLATILE
  COST 1;
ALTER FUNCTION "public"."ivfflat_halfvec_support"(internal) OWNER TO "postgres";

-- ----------------------------
-- Function structure for ivfflathandler
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."ivfflathandler"(internal);
CREATE FUNCTION "public"."ivfflathandler"(internal)
  RETURNS "pg_catalog"."index_am_handler" AS '$libdir/vector', 'ivfflathandler'
  LANGUAGE c VOLATILE
  COST 1;
ALTER FUNCTION "public"."ivfflathandler"(internal) OWNER TO "postgres";

-- ----------------------------
-- Function structure for jaccard_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."jaccard_distance"(bit, bit);
CREATE FUNCTION "public"."jaccard_distance"(bit, bit)
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'jaccard_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."jaccard_distance"(bit, bit) OWNER TO "postgres";

-- ----------------------------
-- Function structure for l1_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l1_distance"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."l1_distance"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'sparsevec_l1_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l1_distance"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for l1_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l1_distance"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."l1_distance"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'l1_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l1_distance"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for l1_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l1_distance"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."l1_distance"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'halfvec_l1_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l1_distance"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for l2_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l2_distance"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."l2_distance"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'l2_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l2_distance"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for l2_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l2_distance"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."l2_distance"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'sparsevec_l2_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l2_distance"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for l2_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l2_distance"("public"."halfvec", "public"."halfvec");
CREATE FUNCTION "public"."l2_distance"("public"."halfvec", "public"."halfvec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'halfvec_l2_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l2_distance"("public"."halfvec", "public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for l2_norm
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l2_norm"("public"."sparsevec");
CREATE FUNCTION "public"."l2_norm"("public"."sparsevec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'sparsevec_l2_norm'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l2_norm"("public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for l2_norm
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l2_norm"("public"."halfvec");
CREATE FUNCTION "public"."l2_norm"("public"."halfvec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'halfvec_l2_norm'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l2_norm"("public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for l2_normalize
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l2_normalize"("public"."halfvec");
CREATE FUNCTION "public"."l2_normalize"("public"."halfvec")
  RETURNS "public"."halfvec" AS '$libdir/vector', 'halfvec_l2_normalize'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l2_normalize"("public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for l2_normalize
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l2_normalize"("public"."vector");
CREATE FUNCTION "public"."l2_normalize"("public"."vector")
  RETURNS "public"."vector" AS '$libdir/vector', 'l2_normalize'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l2_normalize"("public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for l2_normalize
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."l2_normalize"("public"."sparsevec");
CREATE FUNCTION "public"."l2_normalize"("public"."sparsevec")
  RETURNS "public"."sparsevec" AS '$libdir/vector', 'sparsevec_l2_normalize'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."l2_normalize"("public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec"("public"."sparsevec", int4, bool);
CREATE FUNCTION "public"."sparsevec"("public"."sparsevec", int4, bool)
  RETURNS "public"."sparsevec" AS '$libdir/vector', 'sparsevec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec"("public"."sparsevec", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_cmp
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_cmp"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_cmp"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."int4" AS '$libdir/vector', 'sparsevec_cmp'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_cmp"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_eq
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_eq"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_eq"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'sparsevec_eq'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_eq"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_ge
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_ge"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_ge"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'sparsevec_ge'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_ge"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_gt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_gt"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_gt"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'sparsevec_gt'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_gt"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_in
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_in"(cstring, oid, int4);
CREATE FUNCTION "public"."sparsevec_in"(cstring, oid, int4)
  RETURNS "public"."sparsevec" AS '$libdir/vector', 'sparsevec_in'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_in"(cstring, oid, int4) OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_l2_squared_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_l2_squared_distance"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_l2_squared_distance"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'sparsevec_l2_squared_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_l2_squared_distance"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_le
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_le"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_le"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'sparsevec_le'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_le"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_lt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_lt"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_lt"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'sparsevec_lt'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_lt"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_ne
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_ne"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_ne"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'sparsevec_ne'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_ne"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_negative_inner_product
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_negative_inner_product"("public"."sparsevec", "public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_negative_inner_product"("public"."sparsevec", "public"."sparsevec")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'sparsevec_negative_inner_product'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_negative_inner_product"("public"."sparsevec", "public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_out
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_out"("public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_out"("public"."sparsevec")
  RETURNS "pg_catalog"."cstring" AS '$libdir/vector', 'sparsevec_out'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_out"("public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_recv
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_recv"(internal, oid, int4);
CREATE FUNCTION "public"."sparsevec_recv"(internal, oid, int4)
  RETURNS "public"."sparsevec" AS '$libdir/vector', 'sparsevec_recv'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_recv"(internal, oid, int4) OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_send
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_send"("public"."sparsevec");
CREATE FUNCTION "public"."sparsevec_send"("public"."sparsevec")
  RETURNS "pg_catalog"."bytea" AS '$libdir/vector', 'sparsevec_send'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_send"("public"."sparsevec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_to_halfvec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_to_halfvec"("public"."sparsevec", int4, bool);
CREATE FUNCTION "public"."sparsevec_to_halfvec"("public"."sparsevec", int4, bool)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'sparsevec_to_halfvec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_to_halfvec"("public"."sparsevec", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_to_vector
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_to_vector"("public"."sparsevec", int4, bool);
CREATE FUNCTION "public"."sparsevec_to_vector"("public"."sparsevec", int4, bool)
  RETURNS "public"."vector" AS '$libdir/vector', 'sparsevec_to_vector'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_to_vector"("public"."sparsevec", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for sparsevec_typmod_in
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sparsevec_typmod_in"(_cstring);
CREATE FUNCTION "public"."sparsevec_typmod_in"(_cstring)
  RETURNS "pg_catalog"."int4" AS '$libdir/vector', 'sparsevec_typmod_in'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."sparsevec_typmod_in"(_cstring) OWNER TO "postgres";

-- ----------------------------
-- Function structure for subvector
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."subvector"("public"."halfvec", int4, int4);
CREATE FUNCTION "public"."subvector"("public"."halfvec", int4, int4)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'halfvec_subvector'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."subvector"("public"."halfvec", int4, int4) OWNER TO "postgres";

-- ----------------------------
-- Function structure for subvector
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."subvector"("public"."vector", int4, int4);
CREATE FUNCTION "public"."subvector"("public"."vector", int4, int4)
  RETURNS "public"."vector" AS '$libdir/vector', 'subvector'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."subvector"("public"."vector", int4, int4) OWNER TO "postgres";

-- ----------------------------
-- Function structure for sync_zhprs_custom_word
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."sync_zhprs_custom_word"();
CREATE FUNCTION "public"."sync_zhprs_custom_word"()
  RETURNS "pg_catalog"."void" AS $BODY$
declare
	data_dir text;
	dict_path text;
	time_tag_path text;
	query text;
begin
	select setting from pg_settings where name='data_directory' into data_dir;

	select data_dir || '/base/' || '/zhprs_dict_' || current_database() || '.txt' into dict_path;
	select data_dir || '/base/' || '/zhprs_dict_' || current_database() || '.tag' into time_tag_path;

	query = 'copy (select word, tf, idf, attr from zhparser.zhprs_custom_word) to ' || chr(39) || dict_path || chr(39) || ' encoding ' || chr(39) || 'utf8' || chr(39) ;
	execute query;
	query = 'copy (select now()) to ' || chr(39) || time_tag_path || chr(39) ;
	execute query;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION "public"."sync_zhprs_custom_word"() OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector"("public"."vector", int4, bool);
CREATE FUNCTION "public"."vector"("public"."vector", int4, bool)
  RETURNS "public"."vector" AS '$libdir/vector', 'vector'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector"("public"."vector", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_accum
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_accum"(_float8, "public"."vector");
CREATE FUNCTION "public"."vector_accum"(_float8, "public"."vector")
  RETURNS "pg_catalog"."_float8" AS '$libdir/vector', 'vector_accum'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_accum"(_float8, "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_add
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_add"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_add"("public"."vector", "public"."vector")
  RETURNS "public"."vector" AS '$libdir/vector', 'vector_add'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_add"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_avg
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_avg"(_float8);
CREATE FUNCTION "public"."vector_avg"(_float8)
  RETURNS "public"."vector" AS '$libdir/vector', 'vector_avg'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_avg"(_float8) OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_cmp
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_cmp"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_cmp"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."int4" AS '$libdir/vector', 'vector_cmp'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_cmp"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_combine
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_combine"(_float8, _float8);
CREATE FUNCTION "public"."vector_combine"(_float8, _float8)
  RETURNS "pg_catalog"."_float8" AS '$libdir/vector', 'vector_combine'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_combine"(_float8, _float8) OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_concat
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_concat"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_concat"("public"."vector", "public"."vector")
  RETURNS "public"."vector" AS '$libdir/vector', 'vector_concat'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_concat"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_dims
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_dims"("public"."vector");
CREATE FUNCTION "public"."vector_dims"("public"."vector")
  RETURNS "pg_catalog"."int4" AS '$libdir/vector', 'vector_dims'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_dims"("public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_dims
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_dims"("public"."halfvec");
CREATE FUNCTION "public"."vector_dims"("public"."halfvec")
  RETURNS "pg_catalog"."int4" AS '$libdir/vector', 'halfvec_vector_dims'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_dims"("public"."halfvec") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_eq
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_eq"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_eq"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'vector_eq'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_eq"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_ge
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_ge"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_ge"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'vector_ge'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_ge"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_gt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_gt"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_gt"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'vector_gt'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_gt"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_in
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_in"(cstring, oid, int4);
CREATE FUNCTION "public"."vector_in"(cstring, oid, int4)
  RETURNS "public"."vector" AS '$libdir/vector', 'vector_in'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_in"(cstring, oid, int4) OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_l2_squared_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_l2_squared_distance"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_l2_squared_distance"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'vector_l2_squared_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_l2_squared_distance"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_le
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_le"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_le"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'vector_le'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_le"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_lt
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_lt"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_lt"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'vector_lt'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_lt"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_mul
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_mul"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_mul"("public"."vector", "public"."vector")
  RETURNS "public"."vector" AS '$libdir/vector', 'vector_mul'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_mul"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_ne
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_ne"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_ne"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."bool" AS '$libdir/vector', 'vector_ne'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_ne"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_negative_inner_product
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_negative_inner_product"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_negative_inner_product"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'vector_negative_inner_product'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_negative_inner_product"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_norm
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_norm"("public"."vector");
CREATE FUNCTION "public"."vector_norm"("public"."vector")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'vector_norm'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_norm"("public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_out
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_out"("public"."vector");
CREATE FUNCTION "public"."vector_out"("public"."vector")
  RETURNS "pg_catalog"."cstring" AS '$libdir/vector', 'vector_out'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_out"("public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_recv
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_recv"(internal, oid, int4);
CREATE FUNCTION "public"."vector_recv"(internal, oid, int4)
  RETURNS "public"."vector" AS '$libdir/vector', 'vector_recv'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_recv"(internal, oid, int4) OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_send
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_send"("public"."vector");
CREATE FUNCTION "public"."vector_send"("public"."vector")
  RETURNS "pg_catalog"."bytea" AS '$libdir/vector', 'vector_send'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_send"("public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_spherical_distance
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_spherical_distance"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_spherical_distance"("public"."vector", "public"."vector")
  RETURNS "pg_catalog"."float8" AS '$libdir/vector', 'vector_spherical_distance'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_spherical_distance"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_sub
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_sub"("public"."vector", "public"."vector");
CREATE FUNCTION "public"."vector_sub"("public"."vector", "public"."vector")
  RETURNS "public"."vector" AS '$libdir/vector', 'vector_sub'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_sub"("public"."vector", "public"."vector") OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_to_float4
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_to_float4"("public"."vector", int4, bool);
CREATE FUNCTION "public"."vector_to_float4"("public"."vector", int4, bool)
  RETURNS "pg_catalog"."_float4" AS '$libdir/vector', 'vector_to_float4'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_to_float4"("public"."vector", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_to_halfvec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_to_halfvec"("public"."vector", int4, bool);
CREATE FUNCTION "public"."vector_to_halfvec"("public"."vector", int4, bool)
  RETURNS "public"."halfvec" AS '$libdir/vector', 'vector_to_halfvec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_to_halfvec"("public"."vector", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_to_sparsevec
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_to_sparsevec"("public"."vector", int4, bool);
CREATE FUNCTION "public"."vector_to_sparsevec"("public"."vector", int4, bool)
  RETURNS "public"."sparsevec" AS '$libdir/vector', 'vector_to_sparsevec'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_to_sparsevec"("public"."vector", int4, bool) OWNER TO "postgres";

-- ----------------------------
-- Function structure for vector_typmod_in
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."vector_typmod_in"(_cstring);
CREATE FUNCTION "public"."vector_typmod_in"(_cstring)
  RETURNS "pg_catalog"."int4" AS '$libdir/vector', 'vector_typmod_in'
  LANGUAGE c IMMUTABLE STRICT
  COST 1;
ALTER FUNCTION "public"."vector_typmod_in"(_cstring) OWNER TO "postgres";

-- ----------------------------
-- Function structure for zhprs_end
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."zhprs_end"(internal);
CREATE FUNCTION "public"."zhprs_end"(internal)
  RETURNS "pg_catalog"."void" AS '$libdir/zhparser', 'zhprs_end'
  LANGUAGE c VOLATILE STRICT
  COST 1;
ALTER FUNCTION "public"."zhprs_end"(internal) OWNER TO "postgres";

-- ----------------------------
-- Function structure for zhprs_getlexeme
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."zhprs_getlexeme"(internal, internal, internal);
CREATE FUNCTION "public"."zhprs_getlexeme"(internal, internal, internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/zhparser', 'zhprs_getlexeme'
  LANGUAGE c VOLATILE STRICT
  COST 1;
ALTER FUNCTION "public"."zhprs_getlexeme"(internal, internal, internal) OWNER TO "postgres";

-- ----------------------------
-- Function structure for zhprs_lextype
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."zhprs_lextype"(internal);
CREATE FUNCTION "public"."zhprs_lextype"(internal)
  RETURNS "pg_catalog"."internal" AS '$libdir/zhparser', 'zhprs_lextype'
  LANGUAGE c VOLATILE STRICT
  COST 1;
ALTER FUNCTION "public"."zhprs_lextype"(internal) OWNER TO "postgres";

-- ----------------------------
-- Function structure for zhprs_start
-- ----------------------------
DROP FUNCTION IF EXISTS "public"."zhprs_start"(internal, int4);
CREATE FUNCTION "public"."zhprs_start"(internal, int4)
  RETURNS "pg_catalog"."internal" AS '$libdir/zhparser', 'zhprs_start'
  LANGUAGE c VOLATILE STRICT
  COST 1;
ALTER FUNCTION "public"."zhprs_start"(internal, int4) OWNER TO "postgres";

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."agent_message_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."agent_session_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."ai_assistant_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."ai_feedback_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."ai_file_chunk_record_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."chat_file_upload_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."chat_session_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."chat_session_qa_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."comment_record_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."favorite_record_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."file_upload_record_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."knowledge_base_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."knowledge_base_permission_application_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."knowledge_base_permission_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."knowledge_file_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."knowledge_file_segment_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."knowledge_space_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."knowledge_tag_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."knowledge_vector_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."llm_model_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."miner_u_task_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."notification_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."qa_improve_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."search_dictionary_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."search_history_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."search_logs_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."search_suggestion_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_comment_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_comment_reply_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_dept_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_dict_data_code_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_job_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_job_log_job_log_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_menu_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_notice_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_post_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_role_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."sys_user_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."system_dictionary_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."visit_record_id_seq"', 1, false);
