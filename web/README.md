<div align="center">

![](https://oss-kms-static-bucket-hangzhou.oss-cn-hangzhou.aliyuncs.com/icons/badge.jpg)

# 物道知识库管理系统 (Wudao KMS)

**基于 Vue3 + TypeScript 的企业级知识库管理与 AI 对话系统**

[![License](https://img.shields.io/badge/license-AGPL--3.0-blue.svg)](LICENSE)
[![Vue](https://img.shields.io/badge/Vue-3.4.34-brightgreen.svg)](https://vuejs.org/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.9.0-409EFF.svg)](https://element-plus.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.4.5-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-5.2.12-646CFF.svg)](https://vitejs.dev/)

</div>

---

## 📖 项目简介

物道知识库管理系统（Wudao KMS）是一款企业级知识管理与AI对话平台，提供知识文档管理、工作流设计、智能检索、AI问答等功能。系统采用现代化前端技术栈，支持多格式文件预览、富文本编辑、流程可视化设计等企业级功能。

### ✨ 核心特性

- 🎯 **现代化技术栈** - 基于 Vue 3 + TypeScript + Vite 5 构建
- 📚 **知识库管理** - 支持多格式文档上传、预览、分类管理
- 🤖 **AI 智能对话** - 集成AI问答系统，提供智能知识检索
- 🔄 **工作流引擎** - BPMN流程设计器，支持复杂业务流程建模
- 📊 **数据可视化** - 集成 ECharts、AntV X6 等专业图表库
- 📝 **富文本编辑** - 支持 WangEditor、Markdown、代码编辑等多种编辑器
- 🌐 **国际化支持** - 中英文双语切换
- 🎨 **主题定制** - 支持深色模式和多种主题配置
- 🔐 **权限管理** - 完善的RBAC权限体系
- 🚀 **微前端架构** - 基于 Qiankun 支持模块化部署

---

## 🛠️ 技术栈

### 核心框架
- **Vue 3.4.34** - 渐进式 JavaScript 框架
- **TypeScript 5.4.5** - JavaScript 的超集，提供类型安全
- **Vite 5.2.12** - 下一代前端构建工具
- **Pinia 2.1.7** - Vue 官方状态管理库
- **Vue Router 4.3.2** - Vue 官方路由管理器

### UI 组件库
- **Element Plus 2.9.0** - 企业级 UI 组件库
- **UnoCSS 0.58.6** - 即时按需原子化 CSS 引擎
- **@element-plus/icons-vue** - Element Plus 图标库

### 业务组件
- **ECharts 5.5.0** - 强大的数据可视化库
- **AntV X6 2.18.1** - 图编辑引擎（流程图、关系图）
- **BPMN-JS 16.4.0** - BPMN 2.0 流程设计器
- **JSPlumb 2.12.6** - 图表连接器库
- **VXE-Table 4.5.22** - 高性能表格组件

### 编辑器
- **WangEditor 5.1.23** - 轻量级 Web 富文本编辑器
- **Mavon-Editor 3.0.1** - Vue Markdown 编辑器
- **CodeMirror 5.65.18** - 代码编辑器
- **Vue-Quill 1.2.0** - Quill 富文本编辑器

### 文件处理
- **XLSX 0.18.5** - Excel 文件处理
- **docx-preview 0.3.0** - Word 文档预览
- **vue-pdf-embed 1.2.1** - PDF 预览
- **file-saver 2.0.5** - 文件下载工具

### 工具库
- **Axios 1.6.8** - HTTP 客户端
- **Lodash 4.17.21** - 实用工具库
- **Moment 2.30.1** - 日期处理库
- **Crypto-JS 4.2.0** - 加密库
- **JSEncrypt 3.3.2** - RSA 加密
- **NProgress 0.2.0** - 页面加载进度条

### 开发工具
- **ESLint 8.57.0** - 代码质量检查
- **Prettier 3.2.5** - 代码格式化
- **Sass 1.72.0** - CSS 预处理器
- **Unplugin Auto Import** - API 自动导入
- **Unplugin Vue Components** - 组件自动注册

---

## 📁 项目结构

```
kms-ui/
├── bin/                      # 脚本文件
├── html/                     # HTML 页面
├── public/                   # 公共静态资源
├── src/                      # 源代码目录
│   ├── api/                  # API 接口层
│   │   ├── knowledge/        # 知识库接口
│   │   ├── workflow/         # 工作流接口
│   │   ├── specialist/       # 知识专家接口
│   │   ├── retrieve/         # 检索接口
│   │   ├── qa/               # QA 对话接口
│   │   ├── system/           # 系统管理接口
│   │   ├── monitor/          # 监控接口
│   │   └── ...
│   ├── assets/               # 静态资源
│   │   ├── fonts/            # 字体文件
│   │   ├── icons/            # 图标
│   │   ├── images/           # 图片
│   │   └── styles/           # 全局样式
│   ├── bpmn/                 # BPMN 工作流相关
│   ├── components/           # 全局组件
│   │   ├── AIDialogue/       # AI 对话组件
│   │   ├── AdvancedUpload/   # 高级上传组件
│   │   ├── SeTable/          # 高级表格组件
│   │   └── ...
│   ├── config/               # 应用配置
│   ├── directive/            # 自定义指令
│   ├── enums/                # 枚举定义
│   ├── hooks/                # Vue Composition API 钩子
│   ├── lang/                 # 国际化语言包
│   │   ├── zh/               # 中文
│   │   └── en/               # 英文
│   ├── layout/               # 布局组件
│   ├── plugins/              # 插件配置
│   ├── router/               # 路由配置
│   ├── store/                # Pinia 状态管理
│   │   └── modules/          # Store 模块
│   ├── types/                # TypeScript 类型定义
│   ├── utils/                # 工具函数
│   ├── views/                # 页面视图
│   ├── App.vue               # 根组件
│   ├── main.ts               # 应用入口
│   └── permission.ts         # 权限控制
├── vite/                     # Vite 配置
│   └── plugins/              # Vite 插件
├── .gitignore                # Git 忽略文件
├── index.html                # HTML 模板
├── package.json              # 项目依赖
├── tsconfig.json             # TypeScript 配置
├── uno.config.ts             # UnoCSS 配置
├── vite.config.ts            # Vite 配置文件
└── README.md                 # 项目说明
```

---

## 🚀 快速开始

### 环境要求

- **Node.js**: >= 16.0.0（推荐使用 18.x 或 20.x）
- **npm**: >= 8.0.0 或 **pnpm**: >= 7.0.0（推荐）
- **Git**: 最新版本

### 安装

```bash
# 克隆项目
git clone https://gitee.com/JavaLionLi/plus-ui.git

# 进入项目目录
cd kms-ui

# 安装依赖（推荐使用 pnpm）
npm install
# 或
pnpm install
```

### 启动开发服务器

```bash
# 启动开发环境
npm run dev
```

开发服务器将在 `http://localhost:5173` 启动（端口可能会有所不同）

---

## 📋 开发指南

### 可用命令

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

### 开发规范

- **代码风格**: 遵循 ESLint + Prettier 规范
- **组件命名**: 使用 PascalCase（大驼峰）
- **文件命名**: 组件文件使用 PascalCase，工具文件使用 camelCase
- **提交规范**: 建议使用 Conventional Commits 规范

### 环境配置

项目支持多环境配置：

- **development** - 开发环境
- **uat** - UAT 测试环境
- **production** - 生产环境

环境配置文件位于项目根目录（需手动创建 `.env.xxx` 文件）

---

## 📦 构建部署

### 生产环境构建

```bash
npm run build:prod
```

构建产物将生成在 `dist/` 目录下。

### 部署

构建完成后，将 `dist/` 目录下的文件部署到以下任一服务器：

- Nginx
- Apache
- Tomcat
- 或任何静态文件服务器

**Nginx 配置示例**：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }

    # 代理后端 API
    location /api/ {
        proxy_pass http://backend-server:8090/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 🎯 主要功能模块

### 知识库管理
- 知识文档上传、分类、检索
- 支持多种文件格式（Word、PDF、Excel、图片等）
- 文件在线预览
- 版本管理

### AI 对话系统
- 智能问答交互
- 知识检索增强
- 对话历史记录
- 上下文理解

### 工作流管理
- BPMN 2.0 流程设计
- 流程实例管理
- 任务管理
- 表单设计

### 系统管理
- 用户管理
- 角色权限
- 菜单管理
- 部门管理
- 字典管理

### 监控中心
- 在线用户监控
- 操作日志
- 登录日志
- 缓存监控

---

## 🔧 核心依赖说明

| 依赖 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4.34 | 核心框架 |
| TypeScript | 5.4.5 | 类型支持 |
| Vite | 5.2.12 | 构建工具 |
| Element Plus | 2.9.0 | UI 组件库 |
| Pinia | 2.1.7 | 状态管理 |
| Vue Router | 4.3.2 | 路由管理 |
| Axios | 1.6.8 | HTTP 请求 |
| ECharts | 5.5.0 | 数据可视化 |
| BPMN-JS | 16.4.0 | 流程设计 |
| Qiankun | 2.10.16 | 微前端 |

---

## 🌐 浏览器支持

现代浏览器（Chrome、Firefox、Safari、Edge）最新版本

| ![Chrome](https://raw.githubusercontent.com/alrra/browser-logos/master/src/chrome/chrome_48x48.png) | ![Firefox](https://raw.githubusercontent.com/alrra/browser-logos/master/src/firefox/firefox_48x48.png) | ![Safari](https://raw.githubusercontent.com/alrra/browser-logos/master/src/safari/safari_48x48.png) | ![Edge](https://raw.githubusercontent.com/alrra/browser-logos/master/src/edge/edge_48x48.png) |
|:---:|:---:|:---:|:---:|
| Latest ✔ | Latest ✔ | Latest ✔ | Latest ✔ |

---

## 📄 许可证

本项目基于 [AGPL-3.0](LICENSE) 开源协议

---

## 🤝 贡献

欢迎贡献代码、提出问题和建议！

---

## 📞 联系方式

- **项目仓库**: [GitHub](https://github.com/wudao-tech/kms-ui) | [GitEE](https://gitee.com/JavaLionLi/plus-ui)
- **问题反馈**: 请在 Issues 中提交
- **联系我们**:
<p align="center">
    <img src="./doc/imgs/contact.png" alt="联系方式" width="300"/>
</p>

---

<div align="center">

**© 2024 Wudao KMS. All Rights Reserved.**

</div>