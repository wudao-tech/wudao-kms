# AdvancedUpload 高级文件上传组件

一个支持多文件上传、分片上传和进度条显示的 Vue 3 组件，基于 Element Plus 构建。

## 功能特性

- ✅ 多文件上传
- ✅ 分片上传（支持大文件）
- ✅ 实时进度条显示
- ✅ 拖拽上传
- ✅ 文件类型验证
- ✅ 文件大小限制
- ✅ 上传状态管理
- ✅ 错误重试机制
- ✅ 取消上传功能
- ✅ 文件移除功能

## 安装使用

### 1. 引入组件

```vue
<template>
  <AdvancedUpload
    ref="uploadRef"
    :upload-url="uploadUrl"
    :headers="headers"
    :data="uploadData"
    @upload-success="handleUploadSuccess"
    @upload-error="handleUploadError"
    @upload-progress="handleUploadProgress"
    @file-remove="handleFileRemove"
  />
</template>

<script setup>
import AdvancedUpload from '@/components/AdvancedUpload/index.vue'

// 上传配置
const uploadUrl = ref('/api/upload/chunk')
const headers = ref({
  'Authorization': 'Bearer your-token-here'
})
const uploadData = ref({
  knowledgeBaseId: 1,
  knowledgeSpaceId: 1
})

// 事件处理
const handleUploadSuccess = (fileItem) => {
  console.log('文件上传成功:', fileItem)
}

const handleUploadError = (error) => {
  console.log('文件上传失败:', error)
}

const handleUploadProgress = (progress) => {
  console.log('上传进度:', progress)
}

const handleFileRemove = (fileItem) => {
  console.log('文件已移除:', fileItem)
}
</script>
```

### 2. 控制上传

```vue
<template>
  <div>
    <AdvancedUpload ref="uploadRef" />
    <el-button @click="startUpload">开始上传</el-button>
    <el-button @click="cancelUpload">取消上传</el-button>
    <el-button @click="clearFiles">清空文件</el-button>
  </div>
</template>

<script setup>
const uploadRef = ref(null)

const startUpload = () => {
  uploadRef.value?.startUpload()
}

const cancelUpload = () => {
  uploadRef.value?.cancelUpload()
}

const clearFiles = () => {
  uploadRef.value?.clearFiles()
}
</script>
```

## Props 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `accept` | String | `.pdf,.doc,.docx,.txt,.md,.csv,.pptx,.xlsx,.html,.png,.jpg,.jpeg,.gif,.bmp` | 接受的文件类型 |
| `maxFiles` | Number | `15` | 最大文件数量 |
| `maxFileSize` | Number | `100` | 单个文件最大大小 (MB) |
| `chunkSize` | Number | `1024` | 分片大小 (KB) |
| `uploadUrl` | String | `/api/upload` | 上传接口地址 |
| `headers` | Object | `{}` | 额外的请求头 |
| `data` | Object | `{}` | 额外的表单数据 |

## Events 事件

| 事件名 | 参数 | 说明 |
|--------|------|------|
| `upload-success` | `fileItem` | 文件上传成功 |
| `upload-error` | `{ file, error }` | 文件上传失败 |
| `upload-progress` | `{ file, progress }` | 上传进度更新 |
| `file-remove` | `fileItem` | 文件被移除 |

## Methods 方法

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `startUpload()` | - | - | 开始上传所有文件 |
| `cancelUpload()` | - | - | 取消所有上传 |
| `clearFiles()` | - | - | 清空文件列表 |
| `getFileList()` | - | `Array` | 获取当前文件列表 |

## 文件状态

文件有以下几种状态：

- `waiting`: 等待上传
- `uploading`: 正在上传
- `success`: 上传成功
- `error`: 上传失败

## 分片上传说明

组件支持分片上传，适用于大文件上传：

1. **分片大小**: 默认 1MB，可通过 `chunkSize` 属性调整
2. **分片信息**: 每个分片包含以下信息：
   - `chunkIndex`: 分片索引
   - `totalChunks`: 总分片数
   - `fileName`: 文件名
   - `fileSize`: 文件大小

3. **服务器要求**: 服务器需要支持分片上传，接收以下参数：
   ```javascript
   {
     file: Blob,           // 分片数据
     chunkIndex: Number,   // 分片索引
     totalChunks: Number,  // 总分片数
     fileName: String,     // 文件名
     fileSize: Number      // 文件大小
   }
   ```

## 样式定制

组件使用 SCSS 编写样式，可以通过以下方式定制：

```scss
.advanced-upload {
  .upload-dragger {
    // 自定义拖拽区域样式
  }
  
  .file-list {
    .file-item {
      // 自定义文件项样式
    }
  }
}
```

## 注意事项

1. **服务器支持**: 确保服务器支持分片上传和文件合并
2. **文件类型**: 根据实际需求调整 `accept` 属性
3. **文件大小**: 根据服务器限制调整 `maxFileSize` 属性
4. **并发控制**: 大文件上传时注意控制并发数量
5. **错误处理**: 实现完善的错误处理和重试机制

## 示例

完整的使用示例请参考 `src/views/knowledge/components/AdvancedUploadDemo.vue` 