<div align="center">

![](https://oss-kms-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icons/badge.jpg)

# 物道知识库管理系统

**基于 Spring Boot + Vue 3 的企业级知识库管理与 AI 对话系统**

[![License](https://img.shields.io/badge/license-AGPL--3.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-42b883.svg)](https://vuejs.org/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.9-409EFF.svg)](https://element-plus.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791.svg)](https://www.postgresql.org/)

[功能特性](#功能特性) • [快速开始](#快速开始) • [技术栈](#技术栈) • [文档](#文档) • [贡献指南](#贡献指南)

</div>

---

## 📖 项目简介

物道知识库管理系统（Wudao KMS）是一个开源的企业级知识库管理平台，集成了多种大语言模型，提供完整的知识库创建、文档解析、向量化检索、智能对话等功能。系统采用 RAG（检索增强生成）架构，支持多模态交互，适用于企业知识管理、智能客服、文档问答等场景。

### ✨ 核心亮点

- 🤖 **多模型支持**：集成阿里通义、DeepSeek、智谱 AI、Moonshot、Ollama 等多种大模型
- 📚 **智能文档解析**：支持 PDF、Word、Excel、Markdown 等多种格式，集成 MinerU 增强解析
- 🔍 **混合检索**：支持语义检索、全文检索、混合检索三种模式，配合 Rerank 重排算法
- 💬 **RAG 对话**：基于知识库的智能问答，流式响应，引用溯源
- 🎯 **AI 助手**：可配置的智能助手，支持自定义提示词、知识库绑定、快捷指令
- 🎨 **现代化前端**：Vue 3 + TypeScript + Element Plus，支持工作流设计、数据可视化、富文本编辑
- 📊 **可视化工作流**：集成 BPMN-JS 流程设计器，支持复杂业务流程建模
- 🔐 **权限管理**：细粒度的知识库访问控制和分享机制
- 🐳 **容器化部署**：完整的 Docker Compose 一键部署方案
- 🌍 **国际化支持**：中英文双语切换

---

## 🚀 功能特性

### 1. 知识库管理

- ✅ 知识库创建、编辑、删除（物理删除）
- ✅ 知识空间分层管理（树形结构）
- ✅ 知识库权限控制（公开/私有/共享）
- ✅ 权限申请与审批流程
- ✅ 知识库搜索与推荐

### 2. 文件管理与解析

#### 支持的文件格式

| 格式 | 类型 | 解析能力 |
|------|------|---------|
| `.txt` | 纯文本 | 编码自动识别、段落分割 |
| `.md` | Markdown | 格式保留、目录识别 |
| `.pdf` | PDF 文档 | 文本提取、图像抽取、布局分析（支持 MinerU 增强） |
| `.doc/.docx` | Word 文档 | 文本、图片、表格、文本框提取 |
| `.xls/.xlsx` | Excel 表格 | 表格读取、公式处理 |
| `.csv` | CSV 文件 | 表格数据解析 |
| `.html` | 网页 | HTML 内容提取 |
| `.jpg/.png` | 图像 | 上传预览、多模态理解 |
| `.mp3/.mp4` | 音视频 | 文件存储、引用 |

#### 文件处理功能

- ✅ 单文件/批量上传
- ✅ 分片上传（支持断点续传）
- ✅ 文件内容自动向量化
- ✅ 文档段落智能分割
- ✅ 异步处理与进度追踪
- ✅ MinerU 容器化增强解析（需 GPU 支持）

### 3. 智能检索

- ✅ **语义检索**：基于向量相似度的语义搜索
- ✅ **全文检索**：基于关键词的全文索引搜索
- ✅ **混合检索**：语义与全文结合的混合检索
- ✅ **Rerank 重排**：使用重排模型优化搜索结果
- ✅ 搜索历史记录

### 4. AI 对话与助手

#### AI 助手功能

- ✅ 创建自定义 AI 助手
- ✅ 绑定多个知识库
- ✅ 自定义系统提示词
- ✅ AI 驱动的提示词优化
- ✅ 配置对话参数（温度、Token、轮数）
- ✅ 快捷指令配置
- ✅ 深度思考模式
- ✅ 网络搜索集成
- ✅ 多模态支持

#### 对话会话管理

- ✅ 创建新对话会话
- ✅ 多轮对话上下文管理
- ✅ 会话历史记录
- ✅ 会话导出与分享
- ✅ 流式响应（SSE）
- ✅ 引用溯源标记

### 5. 支持的 AI 模型

#### 对话模型

| 提供商 | 模型示例 | 特点 |
|------|---------|-----|
| **阿里通义** | qwen-plus, qwen-max, qwen-long | 国内快速、成本低、长文本支持 |
| **DeepSeek** | deepseek-chat | 开源、成本低、性能均衡 |
| **智谱 AI** | GLM-4, GLM-4V | 中文理解强、支持多模态 |
| **Moonshot** | moonshot-v1 | 超长上下文（128K tokens） |
| **Ollama** | 本地部署模型 | 离线使用、数据安全 |

#### 向量模型（Embedding）

- text-embedding-v4（阿里通义）

#### 重排模型（Rerank）

- qwen3-rerank（阿里通义）

---

## 🏗️ 技术栈

### 后端技术

- **框架**：Spring Boot 3.x + Spring AI
- **数据库**：PostgreSQL 17 + Redis
- **ORM**：MyBatis Plus
- **文档解析**：Apache POI、PDFBox、MinerU
- **向量化**：Spring AI Embedding
- **容器化**：Docker、Docker Compose
- **对象存储**：MinIO / 阿里云 OSS
- **构建工具**：Maven

### 前端技术

- **核心框架**：Vue 3.4.34 + TypeScript 5.4.5
- **构建工具**：Vite 5.2.12
- **UI 组件库**：Element Plus 2.9.0
- **状态管理**：Pinia 2.1.7
- **路由管理**：Vue Router 4.3.2
- **HTTP 客户端**：Axios 1.6.8
- **CSS 引擎**：UnoCSS 0.58.6
- **可视化**：ECharts 5.5.0、AntV X6 2.18.1
- **工作流**：BPMN-JS 16.4.0
- **富文本编辑器**：WangEditor 5.1.23、Mavon-Editor、CodeMirror
- **文件处理**：XLSX、docx-preview、vue-pdf-embed
- **微前端**：Qiankun 2.10.16
- **国际化**：Vue I18n 9.10.2

---

## 📦 快速开始

### 环境要求

#### 后端环境

- **Java**：JDK 21+
- **数据库**：PostgreSQL 17+
- **缓存**：Redis 7+
- **存储**：MinIO 或阿里云 OSS
- **构建工具**：Maven 3.6+
- **容器**：Docker 和 Docker Compose（可选）
- **GPU**：NVIDIA GPU（可选，用于 MinerU 增强解析）

#### 前端环境

- **Node.js**：>= 16.0.0（推荐 18.x 或 20.x）
- **包管理器**：npm >= 8.0.0 或 pnpm >= 7.0.0（推荐）

### 方式一：Docker Compose 部署（推荐）

#### 1. 启动基础服务

```bash
# 进入 docker 目录
cd docker

# 启动 PostgreSQL、Redis、MinIO
docker-compose -f docker-compose-base.yaml up -d

# 查看服务状态
docker-compose -f docker-compose-base.yaml ps
```

#### 2. 初始化数据库

```bash
# 连接到 PostgreSQL
psql -h localhost -p 5432 -U postgres

# 创建数据库
CREATE DATABASE kms;

# 导入初始化脚本
\c kms
\i kms.sql
```

#### 3. 配置环境变量

创建 `docker/.env` 文件：

```env
# 数据库配置
DATABASE_URL=km-postgres
DATABASE_NAME=kms
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=wudao@2025

# Redis 配置
REDIS_HOST=km-redis
REDIS_PORT=6379
REDIS_DATABASE=0
REDIS_PASSWORD=

# MinIO 配置
MINIO_ENDPOINT=http://km-minio:9000
MINIO_ACCESS_KEY=wudao
MINIO_SECRET_KEY=wudao@2025

# AI 模型 API Key
DASHSCOPE_API=your-dashscope-api-key
DEEPSEEK_API=your-deepseek-api-key
ZHIPUAI_API=your-zhipuai-api-key
MOONSHOT_API=your-moonshot-api-key

# MinerU 增强解析（可选）
ENV_MINERU_IMAGE=registry.cn-hangzhou.aliyuncs.com/wudao-tech/ocr-vlm:20251114

# 日志级别
LOG_LEVEL=info
```

#### 4. 构建并启动应用

```bash
# 回到项目根目录
cd ..

# Maven 打包
mvn clean package -DskipTests

# 启动应用服务
cd docker
docker-compose -f docker-compose-service.yml up -d
```

#### 5. 启动前端项目

```bash
# 进入前端目录
cd ../web

# 安装依赖（首次运行）
npm install
# 或使用 pnpm（推荐）
pnpm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5173` 启动（端口可能会有所不同）

#### 6. 访问应用

- **前端应用**：http://localhost:5173
- **API 接口**：http://localhost:8090/kms
- **健康检查**：http://localhost:8090/kms/actuator/health
- **MinIO 控制台**：http://localhost:9001（用户名：wudao，密码：wudao@2025）

### 方式二：本地开发部署

#### 1. 克隆项目

```bash
git clone https://github.com/your-org/wudao-kms.git
cd wudao-kms
```

#### 2. 配置数据库

确保 PostgreSQL 和 Redis 已安装并运行，导入 `docker/kms.sql`。

#### 3. 配置 application.yml

编辑 `kms-server/src/main/resources/application-dev.yml`，配置数据库、Redis、OSS 等连接信息。

#### 4. 运行后端应用

```bash
# Maven 构建
mvn clean install -DskipTests

# 运行应用
cd kms-server
mvn spring-boot:run
```

或使用 IDE（IntelliJ IDEA / Eclipse）直接运行 `KmsApplication.java`。

#### 5. 启动前端项目

```bash
# 进入前端目录（从项目根目录）
cd web

# 安装依赖
npm install
# 或使用 pnpm（推荐）
pnpm install

# 启动开发服务器
npm run dev
```

前端开发服务器启动后，访问 http://localhost:5173 即可使用系统。

---

## 📐 系统架构


<p align="center">
    <img src="./doc/imgs/framework.png" alt="architecture" style="max-width: 740px; height: 508px" />
</p>


### 核心架构说明

```
用户请求
    ↓
Spring Boot 应用层
    ├─ Controller（API 接口）
    ├─ Service（业务逻辑）
    │   ├─ 知识库管理
    │   ├─ 文件解析服务
    │   ├─ 向量化服务
    │   └─ AI 对话服务
    ├─ Mapper（数据访问）
    └─ LLM 策略层（多模型适配）
         ↓
数据存储层
    ├─ PostgreSQL（关系数据 + 向量索引）
    ├─ Redis（缓存 + 会话）
    └─ MinIO/OSS（文件存储）
         ↓
外部服务
    ├─ AI 模型 API（通义、DeepSeek 等）
    ├─ MinerU 容器（增强解析）
    └─ Docker（容器化部署）
```

---

## 📂 项目结构

```
wudao-kms/
├── kms-server/                    # 主应用模块
│   ├── src/main/java/com/wudao/
│   │   ├── KmsApplication.java    # 应用入口
│   │   └── kms/
│   │       ├── controller/        # API 控制器
│   │       │   ├── KnowledgeBaseController.java
│   │       │   ├── KnowledgeFileController.java
│   │       │   ├── KnowledgeChatController.java
│   │       │   └── ...
│   │       ├── service/           # 业务服务层
│   │       │   ├── KnowledgeBaseService.java
│   │       │   ├── FileContentExtractorService.java
│   │       │   ├── VectorizationService.java
│   │       │   └── ...
│   │       ├── mapper/            # 数据访问层
│   │       ├── entity/            # 实体类
│   │       ├── dto/               # 数据传输对象
│   │       ├── vo/                # 视图对象
│   │       ├── llm/               # LLM 模型集成
│   │       │   ├── chat/          # 对话策略
│   │       │   ├── llmmode/       # 模型管理
│   │       │   └── tool/          # 工具函数
│   │       ├── agent/             # AI 助手模块
│   │       ├── chunk/             # 文件分片
│   │       ├── mineru/            # MinerU 集成
│   │       └── config/            # 配置类
│   └── src/main/resources/
│       ├── application.yml        # 主配置文件
│       ├── application-dev.yml    # 开发环境配置
│       └── mapper/                # MyBatis XML
├── web/                           # 前端项目（Vue 3）
│   ├── src/                       # 前端源代码
│   │   ├── api/                   # API 接口层
│   │   │   ├── knowledge/         # 知识库接口
│   │   │   ├── workflow/          # 工作流接口
│   │   │   ├── specialist/        # 知识专家接口
│   │   │   └── ...
│   │   ├── components/            # 全局组件
│   │   │   ├── AIDialogue/        # AI 对话组件
│   │   │   ├── AdvancedUpload/    # 高级上传组件
│   │   │   └── ...
│   │   ├── views/                 # 页面视图
│   │   ├── router/                # 路由配置
│   │   ├── store/                 # 状态管理（Pinia）
│   │   ├── utils/                 # 工具函数
│   │   └── ...
│   ├── public/                    # 静态资源
│   ├── package.json               # 前端依赖
│   ├── vite.config.ts             # Vite 配置
│   ├── tsconfig.json              # TypeScript 配置
│   └── README.md                  # 前端说明文档
├── docker/                        # Docker 部署文件
│   ├── docker-compose-base.yaml   # 基础服务
│   ├── docker-compose-service.yml # 应用服务
│   ├── kms.sql                    # 数据库初始化脚本
│   └── redis.conf                 # Redis 配置
├── doc/                           # 文档目录
│   └── imgs/                      # 图片资源
├── Dockerfile                     # 应用 Docker 镜像
├── pom.xml                        # Maven 父 POM
├── LICENSE                        # AGPL-3.0 许可证
└── README.md                      # 项目说明
```

---

## ⚙️ 配置说明

### 核心配置项

#### AI 模型配置

在环境变量或 `application-dev.yml` 中配置：

```yaml
env:
  api-key:
    dashscope: ${DASHSCOPE_API}      # 阿里通义 API Key
    deepseek: ${DEEPSEEK_API}        # DeepSeek API Key
    moonshot: ${MOONSHOT_API}        # Moonshot API Key
    zhipuai: ${ZHIPUAI_API}          # 智谱 AI API Key
    ollama: ${OLLAMA_BASE_URL}       # Ollama 本地地址
    openai:
      base-url: ${OPENAI_BASE_URL}
      api-key: ${OPENAI_API_KEY}
```

#### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/kms?currentSchema=public&timezone=Asia/Shanghai
    username: postgres
    password: wudao@2025
    driver-class-name: org.postgresql.Driver
```

#### 文件上传配置

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 500MB      # 单文件最大 500MB
      max-request-size: 500MB   # 请求最大 500MB
```

#### MinerU 增强解析配置

```yaml
docker:
  docker-host: unix:///var/run/docker.sock
  api-version: 1.43
  registry-url: registry.cn-hangzhou.aliyuncs.com
```

环境变量：
```bash
ENV_MINERU_IMAGE=registry.cn-hangzhou.aliyuncs.com/wudao-tech/ocr-vlm:20251114
```

---

## 🔧 开发指南

### API 接口文档

应用启动后，访问 Swagger API 文档：

```
http://localhost:8090/kms/swagger-ui.html
```

### 主要 API 端点

#### 知识库管理

- `POST /api/knowledge-base` - 创建知识库
- `GET /api/knowledge-base/{id}` - 获取知识库详情
- `PUT /api/knowledge-base/{id}` - 更新知识库
- `DELETE /api/knowledge-base/{id}` - 删除知识库

#### 文件管理

- `POST /api/knowledge-file/upload` - 上传文件
- `GET /api/knowledge-file/list` - 文件列表
- `DELETE /api/knowledge-file/{id}` - 删除文件

#### AI 对话

- `POST /knowledge/chat` - 知识库对话（流式响应）
- `GET /api/agent/v2/session` - 获取会话列表

#### AI 助手

- `POST /api/agent/v2/assistant` - 创建助手
- `GET /api/agent/v2/assistant/{uuid}` - 获取助手详情

### 添加新的 AI 模型

1. 在 `llm/chat` 包下创建新的策略类，实现 `ChatModelStrategy` 接口
2. 在 `ChatModelFactory` 中注册新策略
3. 在数据库 `llm_model` 表添加模型配置
4. 在配置文件中添加 API Key

示例：

```java
@Component
public class NewModelStrategy implements ChatModelStrategy {
    @Override
    public Flux<String> streamChat(ChatRequest request) {
        // 实现流式对话逻辑
    }
}
```

### 前端开发指南

#### 可用命令

```bash
# 启动开发环境
npm run dev

# 生产环境构建
npm run build:prod

# UAT 环境构建
npm run build:uat

# 开发环境构建
npm run build:dev

# 预览构建结果
npm run preview

# ESLint 代码检查与修复
npm run lint:eslint

# Prettier 代码格式化
npm run prettier
```

#### 环境配置

项目支持多环境配置，在项目根目录创建对应的 `.env` 文件：

- `.env.development` - 开发环境
- `.env.uat` - UAT 测试环境
- `.env.production` - 生产环境

示例配置：

```env
# 应用端口
VITE_APP_PORT=5173

# API 基础路径
VITE_APP_BASE_API=/api

# 应用上下文路径
VITE_APP_CONTEXT_PATH=/
```

#### 前端构建部署

```bash
# 生产环境构建
npm run build:prod
```

构建产物将生成在 `dist/` 目录下，可部署到 Nginx、Apache 等 Web 服务器。

**Nginx 配置示例**：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /path/to/web/dist;
        try_files $uri $uri/ /index.html;
    }

    # 代理后端 API
    location /api/ {
        proxy_pass http://backend-server:8090/kms/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

#### 开发规范

- **代码风格**：遵循 ESLint + Prettier 规范
- **组件命名**：使用 PascalCase（大驼峰）
- **文件命名**：组件文件使用 PascalCase，工具文件使用 camelCase
- **提交规范**：建议使用 Conventional Commits 规范

详细的前端开发文档请参考 `web/README.md`。

---

## ⚠️ 注意事项

### 重要提示

1. **MinerU 增强解析**
   - 需要在环境变量中配置 `ENV_MINERU_IMAGE`
   - 默认镜像：`registry.cn-hangzhou.aliyuncs.com/wudao-tech/ocr-vlm:20251114`
   - 容器环境下使用文档增强解析，需要安装 NVIDIA 驱动
   - 驱动下载：[NVIDIA Container Toolkit](https://github.com/NVIDIA/nvidia-container-toolkit/releases)

2. **数据库配置**
   - 使用 PostgreSQL 17+ 以获得更好的向量检索性能
   - 确保启用了 `pgvector` 扩展（如需向量检索）

3. **文件存储**
   - 生产环境建议使用阿里云 OSS 或 MinIO 集群
   - 本地存储路径：`/home/wudao/uploadPath/`

4. **API 限流**
   - 注意各 AI 模型服务商的 API 调用频率限制
   - 建议配置 Redis 实现接口限流

5. **安全配置**
   - 生产环境务必修改默认密码
   - 配置 HTTPS 证书
   - 启用 JWT Token 认证

6. **前端开发**
   - 前端项目需要单独启动开发服务器
   - 开发环境下前后端分离，通过 Vite 代理访问后端 API
   - 生产环境构建后可部署到任何静态文件服务器

---

## 🌐 浏览器支持

推荐使用现代浏览器的最新版本以获得最佳体验：

| ![Chrome](https://raw.githubusercontent.com/alrra/browser-logos/master/src/chrome/chrome_48x48.png) | ![Firefox](https://raw.githubusercontent.com/alrra/browser-logos/master/src/firefox/firefox_48x48.png) | ![Safari](https://raw.githubusercontent.com/alrra/browser-logos/master/src/safari/safari_48x48.png) | ![Edge](https://raw.githubusercontent.com/alrra/browser-logos/master/src/edge/edge_48x48.png) |
|:---:|:---:|:---:|:---:|
| Latest ✔ | Latest ✔ | Latest ✔ | Latest ✔ |

**最低版本要求**：Chrome 90+、Firefox 88+、Safari 14+、Edge 90+

---

## 📄 许可证

本项目基于 **GNU Affero General Public License v3.0** 开源协议发布。

详见 [LICENSE](LICENSE) 文件。

**重要说明**：
- ✅ 允许商业使用、修改和分发
- ⚠️ 网络服务部署必须开源
- ⚠️ 修改后必须以相同协议开源
- ℹ️ 不提供任何担保

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

### 贡献流程

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

### 开发规范

- 代码风格：遵循阿里巴巴 Java 开发手册
- 提交信息：使用语义化提交信息（Conventional Commits）
- 单元测试：新功能需包含单元测试

---

## 📞 联系我们

- **项目地址**：[GitHub](https://github.com/wudao-tech/wudao-kms) | [GitEE](https://gitee.com/wudao-tech/wudao-kms.git)
- **问题反馈**：[Issues](https://github.com/wudao-tech/wudao-kms)
- **官方网站**：[物道智云](https://wudao-tech.com)
- **帮助文档**：[帮助文档](https://wudaotech.feishu.cn/wiki/FxliwHDiTiFcmskOHSccvlesnbI)

### 技术交流群

<p align="center">
    <img src="./doc/imgs/contact.png" alt="联系方式" width="300"/>
</p>

---

## 🙏 致谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring AI](https://spring.io/projects/spring-ai)
- [MyBatis Plus](https://baomidou.com/)
- [Apache POI](https://poi.apache.org/)
- [PDFBox](https://pdfbox.apache.org/)
- [PostgreSQL](https://www.postgresql.org/)
- [MinIO](https://min.io/)

---

<div align="center">

**如果这个项目对你有帮助，请给我们一个 ⭐ Star！**

Made with ❤️ by Wudao Team

</div>
