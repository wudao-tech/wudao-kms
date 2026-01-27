<template>
    <div class="assistant-form">
        <div class="form-header">
            <div class="dp-cc" style="width: 33%; justify-content: flex-start;">
                <el-button link icon="Back" @click="handleCancel"></el-button>
                <img :src="formData.logo" style="width: 30px;height: 30px; margin: 0 10px;" alt="">
                <div>
                    <div style="font-size: 14px;">{{ formData.name }}</div>
                    <div style="display: flex; font-size: 12px;color: #AFAFAF; align-items: center">
                        <div class="text-ellipsis" style=" max-width: 200px">{{ formData.description }}</div>
                        <span :style="{marginLeft: '20px', color: formData.status === 1 ? '#3dcd57' : ''}">{{formData.status === 1 ? '已发布' : '草稿'}}</span>
                        <span style="margin-left: 20px">{{ moment(formData.updateTime).format('YYYY-MM-DD HH:mm') }}</span>
                    </div>
                </div>
            </div>
            <div style="flex: 1;justify-content: center" class="dp-cc">
                <span style="color: #6b05a8">配置</span>
            </div>
            <div class="dp-cc" style="width: 33%; justify-content: flex-end; align-items: center; gap: 10px;">
                <!-- 自动保存状态提示 -->
                <div class="auto-save-status" v-if="autoSaveStatus">
                    <el-icon v-if="autoSaveStatus === 'saving'" class="is-loading">
                        <Loading />
                    </el-icon>
                    <el-icon v-else-if="autoSaveStatus === 'saved'" style="color: #67c23a;">
                        <CircleCheck />
                    </el-icon>
                    <el-icon v-else-if="autoSaveStatus === 'failed'" style="color: #f56c6c;">
                        <CircleClose />
                    </el-icon>
                    <span style="font-size: 12px; margin-left: 4px;">
                        {{ autoSaveStatus === 'saving' ? '自动保存中...' : 
                           autoSaveStatus === 'saved' ? '已自动保存' : 
                           autoSaveStatus === 'failed' ? '自动保存失败' : '' }}
                    </span>
                </div>
                
                <el-button v-if="userId === formData.createdBy" type="primary" @click="handleSubmit" size="small" :disabled="route.query.status === '1'" :loading="submitLoading">
                    {{ submitLoading ? '保存中...' : '保存' }}
                </el-button>
                <span v-else style="color: #666666; font-size: 14px;">您只有查看权限</span>
                <!-- <el-button size="small" type="primary" :loading="submitLoading" @click="handlePublish">发布</el-button> -->
            </div>
        </div>
        <!-- 配置类型和智能体类型显示表单 -->
        <el-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            label-width="80px"
            label-position="left"
            class="assistant-form-content"
        >
            <div class="form-layout">
                <!-- 左侧65%：编辑智能体区域 -->
                <div class="edit-section">
                                         <!-- 右侧50%：基础配置 -->
                    <div class="config-column">
                        <div class="config-container custom-scrollbar-container" >
                            <el-collapse v-model="activeCollapse" class="config-collapse">                    
                                
                                <el-collapse-item title="知识库" name="knowledge">
                                    <template #title>
                                        <span><span  style="font-weight: bold;">知识库</span><span style="color: #666666;margin-left: 20px; font-weight: 400;">扩宽大规模的知识边界</span></span>
                                    </template>
                                    <el-form-item label="知识库">
                                        <el-select
                                            v-model="formData.knowledgeList"
                                            placeholder="选择知识库"
                                            filterable
                                            style="width: 100%"
                                            value-key="id"
                                            @change="handleKnowledgeChange"
                                        >
                                            <el-option
                                                v-for="kb in knowledgeOptions"
                                                :key="kb.id"
                                                :label="kb.name"
                                                :value="kb.id"
                                            >
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="知识空间">
                                       <el-cascader
                                            :options="spaceOptions"
                                            v-model="formData.knowledgeSpaceList"
                                            :props="defaultProps"
                                            :show-all-levels="false"
                                            clearable
                                            collapse-tags
                                            collapse-tags-tooltip
                                            style="width: 100%"
                                            @change="handleKnowledgeSpaceChange"
                                        />
                                    </el-form-item>
                                    <div class="segment-options">
                                        <el-form-item label="检索方式" label-position="top">
                                            <el-radio-group v-model="formData.knowledgeSearchType">
                                                <el-radio value="fulltext" size="large">
                                                    <div class="radio-title">
                                                        <span>全文检索</span>
                                                        <el-tooltip content="通过关键词更精准匹配进行检索" placement="top" >
                                                            <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                                                        </el-tooltip>
                                                    </div>
                                                </el-radio>
                                                <el-radio value="hybrid" size="large">
                                                    <div class="radio-title">
                                                        混合检索
                                                        <el-tooltip content="使同时使用语义检索和全文检索进行查询，并对结果进行排序" placement="top">
                                                            <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                                                        </el-tooltip>
                                                    </div>
                                                </el-radio>
                                                <el-radio value="semantic" size="large">
                                                    <div class="radio-title">
                                                        <span>语义检索</span>
                                                        <el-tooltip content="基于向量化技术，通过分析查询的语义和上下文来提供更准确和相关的搜索结果" placement="top">
                                                            <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                                                        </el-tooltip>
                                                    </div>
                                                </el-radio>
                                               
                                            </el-radio-group>
                                        </el-form-item>
                                        <el-form-item  label-position="left"  style="margin-top: 10px;">
                                            <template #label>
                                            <div style="display: flex;">
                                                <span>检索分</span>
                                                <el-tooltip content="根据检索分限制从知识库得到最相近的答案" placement="top" >
                                                <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                                                </el-tooltip>
                                            </div>
                                            </template>
                                            <el-slider v-model="formData.ragScore" :min="0" :max="1" :step="0.1" show-input size="small" />
                                        </el-form-item>
                                        <el-form-item  label-position="left">
                                            <template #label>
                                            <div style="display: flex;">
                                                <span>top k</span>
                                                <el-tooltip content="根据检索分排序，按照topK选出前几个结果" placement="top" >
                                                <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                                                </el-tooltip>
                                            </div>
                                            </template>
                                            <el-slider v-model="formData.ragTopK" :min="1" :max="20" :step="1" show-input size="small" />
                                        </el-form-item>
                                    </div>
                                </el-collapse-item>
                                <!-- 结果重排 -->
                                <el-collapse-item title="结果重排模型" name="rerank">
                                    <el-form-item label-width="120px">
                                        <template #label>
                                            <div style="display: flex;">
                                                <span>模型选择</span>
                                                <el-tooltip content="使用重排模型来进行二次排序,可增强综合排名" placement="top" >
                                                <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                                                </el-tooltip>
                                            </div>
                                        </template>
                                        <el-select v-model="formData.rerankModel" @change="handleRerankModelChange">
                                            <el-option
                                            v-for="item in modelOptions.filter(item => item.modelType === 'text_rerank')"
                                            :key="item.model"
                                            :label="item.name"
                                            :value="item.model"
                                            >
                                            <div style="display: flex; align-items: center; gap: 8px;">
                                                <img :src="item.modelIcon" style="height: 26px; width: 26px;" alt="">
                                                <span>{{ item.name }}</span>
                                            </div>
                                            </el-option>
                                            <template #label="{ label, value }">
                                                <div style="display: flex; align-items: center; gap: 8px;">
                                                    <img :src="modelOptions.find(item => item.model === value)?.modelIcon" style="width: 24px;" alt="">
                                                    <span>{{ label }}</span>
                                                </div>
                                            </template>
                                        </el-select>
                                    </el-form-item> 
                                    <el-form-item label="" prop="" label-width="120px">
                                        <template #label>
                                            <div style="display: flex;">
                                                <span>重排分</span>
                                                <el-tooltip content=" 增加重排分限制展示最相近的答案" placement="top">
                                                    <el-icon style="color: #D9D9D9;"><i-ep-question-filled /></el-icon>
                                                </el-tooltip>
                                            </div>
                                    </template>
                                    <el-slider v-model="formData.rerankScore" :min="0" :max="1" :step="0.1" show-input size="small" />
                                    </el-form-item>
                                    <el-form-item label="" prop="" label-width="120px">
                                        <template #label>
                                            <div style="display: flex;">
                                                <span>重排top k</span>
                                                <el-tooltip content="根据重排分排序从重排结果中选出前若干个的结果" placement="top">
                                                    <el-icon style="color: #D9D9D9;"><i-ep-question-filled /></el-icon>
                                                </el-tooltip>
                                            </div>
                                        </template>
                                    <el-slider v-model="formData.rerankTopK" :min="1" :max="10" :step="1" show-input size="small" />
                                    </el-form-item>
                                </el-collapse-item>
                                <el-collapse-item name="dialog">
                                    <template #title>
                                        <span><span  style="font-weight: bold;">对话</span><span style="color: #666666;margin-left: 20px; font-weight: 400;">自主配置对话界面</span></span>
                                    </template>
                                    <!-- 开始引导 -->
                                    <el-collapse-item title="开场白" name="guide">
                                        <el-form-item label="开场文案" prop="guideWord" label-position="top">
                                            <div class="field-container">
                                                <el-input
                                                    v-model="formData.guideWord"
                                                    type="textarea"
                                                    :rows="4"
                                                    placeholder="你好，我是小物智能助手，有什么可以帮您？"
                                                    maxlength="300"
                                                    show-word-limit
                                                />
                                            </div>
                                        </el-form-item>

                                        <el-form-item label="推荐问题" prop="guideQuestions" style="margin-bottom: 0;" label-position="top">
                                            <template #label>
                                                <div style="display: flex; justify-content: space-between;">
                                                    <span>推荐问题</span>
                                                    <el-button
                                                        link
                                                        type="primary"
                                                        @click="addQuestion"
                                                        v-if="formData.guideQuestions.length < 5"
                                                    >
                                                        <el-icon><Plus /></el-icon>
                                                        添加问题
                                                    </el-button>
                                                </div>
                                            </template>
                                            <div class="field-container" :class="{ 'ai-updating': improvingPrompt }">
                                                <div class="guide-questions">
                                                    <div
                                                        v-for="(question, index) in formData.guideQuestions"
                                                        :key="index"
                                                        class="question-item"
                                                    >
                                                        <el-input
                                                            v-model="formData.guideQuestions[index]"
                                                            placeholder="请输入推荐问题"
                                                            maxlength="100"
                                                        />
                                                        <el-button link @click="removeQuestion(index)" title="删除问题">
                                                            <el-icon><Delete /></el-icon>
                                                        </el-button>
                                                    </div>
                                                    
                                                </div>
                                            </div>
                                        </el-form-item>
                                    </el-collapse-item>
                                    <!-- 问题追问 -->
                                    <el-collapse-item title="问题追问" name="follow_up">
                                        <template #title>
                                            <span>问题追问</span>
                                            <el-tooltip content="在回答完问题后，自动根据历史对话内容提供用户3条提问建议" placement="top">
                                                <el-icon style="color: #D9D9D9;"><QuestionFilled /></el-icon>
                                            </el-tooltip>
                                            <el-switch style="margin-left: 30px;" v-model="formData.followup.type" @change="handleFollowupTypeChange" @click.stop active-value="div" inactive-value="close" />
                                            <span style="margin-left: 5px; color: #333; font-weight: 500;">自定义</span>
                                        </template>
                                        <el-form-item v-if="formData.followup.type === 'div'" label-width="120px">
                                            <template #label>
                                                <div style="display: flex;">
                                                    <span>模型选择</span>
                                                    <el-tooltip content="选择模型并根据提示词生成追问问题" placement="top" >
                                                    <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                                                    </el-tooltip>
                                                </div>
                                            </template>
                                            <el-select v-model="formData.followup.model">
                                                <el-option
                                                v-for="item in modelOptions.filter(item => item.modelType === 'text_generation')"
                                                :key="item.model"
                                                :label="item.name"
                                                :value="item.model"
                                                >
                                                <div style="display: flex; align-items: center; gap: 8px;">
                                                    <img :src="item.modelIcon" style="height: 26px; width: 26px;" alt="">
                                                    <span>{{ item.name }}</span>
                                                </div>
                                                </el-option>
                                                <template #label="{ label, value }">
                                                    <div style="display: flex; align-items: center; gap: 8px;">
                                                        <img :src="modelOptions.find(item => item.model === value)?.modelIcon" style="width: 24px;" alt="">
                                                        <span>{{ label }}</span>
                                                    </div>
                                                </template>
                                            </el-select>
                                        </el-form-item> 
                                        <el-form-item v-if="formData.followup.type === 'div'" label="追问提示词" label-position="top">
                                            <div class="field-container">
                                                <el-input
                                                    v-model="formData.followup.flowPrompt"
                                                    type="textarea"
                                                    :rows="4"
                                                    placeholder="请按要求提供三条追问问题，问题应该与你最后一轮的回复内容紧密相关，可以引发进一步讨论"
                                                    maxlength="300"
                                                    show-word-limit
                                                />
                                            </div>
                                        </el-form-item>
                                    </el-collapse-item>
                                    <!-- 快捷指令 -->
                                    <el-collapse-item title="快捷指令" name="shortcut">
                                        <template #title>
                                            <span>快捷指令</span>
                                            <el-tooltip content="快捷指令是对话输入框上方的按钮，配置完成后，用户可以快速发起预设对话" placement="top">
                                                <el-icon style="color: #D9D9D9;"><QuestionFilled /></el-icon>
                                            </el-tooltip>
                                        </template>
                                        <div style="width: 100%;">
                                            <div>
                                                <div v-for="(item, index) in formData.quickCommands" :key="index" class="tool-item" style="display: flex; align-items: center; margin-bottom: 5px">
                                                    <span style="flex: 1; background: #F2F4FD; color: #838383; padding: 5px 10px; margin-right: 10px; display: flex; align-items: center;">
                                                        <svg-icon style="font-size: 14px; margin-right: 5px; margin-top: 2px;" :icon-class="item.icon"></svg-icon>
                                                        <span style="font-size: 14px;font-weight: 700; height: 22px;">{{ item.title }}</span>
                                                    </span>
                                                                                                    <el-button  link @click="openShortcut('edit', item, index)" title="编辑快捷指令">
                                                        <el-icon><Edit/></el-icon>
                                                    </el-button>
                                                    <el-button  link @click="removeQuickCommand(index)" title="移除快捷指令">
                                                        <el-icon><Delete /></el-icon>
                                                    </el-button>
                                                </div>
                                            </div>
                                                <el-button  link @click="openShortcut('add')">
                                                <el-icon><Plus /></el-icon>
                                                添加
                                            </el-button>
                                        </div>
                                    </el-collapse-item>
                                    <!-- 背景图 -->
                                    <el-collapse-item title="背景图" name="background">
                                        <div style="position: relative; display: inline-block;">
                                            <el-upload ref="uploadRef" class="avatar-uploader" action="/api/file/upload" :show-file-list="false" :before-upload="beforeAvatarUpload" :http-request="fileUploadFn">
                                                <img v-if="formData.bgImg" :src="formData.bgImg" class="avatar" />
                                                <el-icon v-else class="avatar-uploader-icon">
                                                <Plus />
                                                </el-icon>
                                            </el-upload>
                                            <!-- 删除按钮 -->
                                            <el-button 
                                                v-if="formData.bgImg" 
                                                type="danger" 
                                                size="small" 
                                                circle 
                                                @click="removeBgImg"
                                                style="position: absolute; top: -8px; right: -8px; z-index: 10;"
                                                title="删除背景图"
                                            >
                                                <el-icon><Delete /></el-icon>
                                            </el-button>
                                        </div>
                                    </el-collapse-item>
                                </el-collapse-item>                               
                                       <!-- 工作流Agent -->
                                <el-collapse-item title="工作流Agent" name="flow">
                                    <div style="width: 100%;">
                                        <el-form-item label="工作流">
                                        <el-select
                                            disabled
                                            v-model="formData.workflowUuids"
                                            placeholder="工作流暂不支持，请等待后续版本更新"
                                            filterable
                                            multiple
                                            collapse-tags
                                            collapse-tags-tooltip
                                            clearable
                                            style="width: 100%"
                                            value-key="uuid"
                                            @change="handleWorkflowChange"
                                        >
                                            <el-option
                                                v-for="kb in workflowOptions"
                                                :key="kb.uuid"
                                                :label="kb.name"
                                                :value="kb.uuid"
                                            >
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    </div>
                                </el-collapse-item>
                            </el-collapse>
                        </div>
                    </div>
                    <!-- 左侧55%：提示词区域 -->
                    <div class="prompt-column">         
                        <div  style="border: 1px solid #ebeef5; margin-bottom: 10px;">
                            <div style="height: 48px; font-size: 13px; background-color: #eaedffd4; line-height: 48px; padding-left: 16px; margin-bottom: 15px; font-weight: bold;">
                               大语言模型
                            </div>
                            <div style="padding: 0 16px;">
                                <el-form-item label="" prop="modelName" label-width="120px">
                                <template #label>
                                    <div style="display: flex;">
                                        <span>模型选择</span>
                                        <el-tooltip content="按照需求选择生成最终回答的大模型" placement="top" >
                                        <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                                        </el-tooltip>
                                    </div>
                                </template>
                                <el-select v-model="formData.modelName" class="selectWidth"  @change="handleModelClick">
                                    <el-option
                                    v-for="item in modelOptions.filter(item => item.modelType === 'text_generation')"
                                    :key="item.model"
                                    :label="item.name"
                                    :value="item.model"
                                    >
                                    <div style="display: flex; align-items: center; gap: 8px;">
                                        <img :src="item.modelIcon" style="height: 26px; width: 26px;" alt="">
                                        <span>{{ item.name }}</span>
                                    </div>
                                    </el-option>
                                    <template #label="{ label, value }">
                                        <div style="display: flex; align-items: center; gap: 8px;">
                                            <img :src="modelOptions.find(item => item.model === value)?.modelIcon" style="width: 24px;" alt="">
                                            <span>{{ label }}</span>
                                        </div>
                                    </template>
                                </el-select>
                                </el-form-item> 
                                <el-form-item label="" prop="" label-position="left" label-width="120px">
                                    <template #label>
                                    <div style="display: flex;">
                                        <span>生成多样性</span>
                                        <el-tooltip content="控制模型输出的多样性，数值越高输出内容的差异性越大" placement="top" >
                                        <el-icon style="color: #D2C9C9;"><QuestionFilled /></el-icon>
                                        </el-tooltip>
                                    </div>
                                    </template>
                                    <el-slider v-model="formData.temperature" :min="0" :max="1" :step="0.1" show-input size="small" />
                                </el-form-item>
                                <el-form-item label="" prop="" label-width="120px">
                                    <template #label>
                                        <div style="display: flex;">
                                            <span>对话记忆轮数</span>
                                            <el-tooltip content="设置输入模型的最大历史对话轮数，轮数越多，对话相关性越强" placement="top">
                                                <el-icon style="color: #D9D9D9;"><i-ep-question-filled /></el-icon>
                                            </el-tooltip>
                                        </div>
                                </template>
                                <el-slider v-model="formData.dialogRound" :min="1" :max="10" :step="1" show-input size="small" />
                                </el-form-item>
                                <el-form-item label="" prop="" label-width="120px">
                                    <template #label>
                                        <div style="display: flex;">
                                            <span>最大回复长度</span>
                                            <el-tooltip content="控制模型输入输出的Tokens长度上限" placement="top">
                                                <el-icon style="color: #D9D9D9;"><i-ep-question-filled /></el-icon>
                                            </el-tooltip>
                                        </div>
                                </template>
                                <el-slider v-model="formData.maxToken" :min="1" :max="maxlength" :step="10" show-input size="small" />
                                </el-form-item>
                                <el-form-item label="" prop="" label-width="120px">
                                    <template #label>
                                        <div style="display: flex;">
                                            <span>top-P</span>
                                            <el-tooltip  placement="top">
                                                <template #content>
                                                    <div style="max-width: 600px; word-wrap: break-word; word-break: break-all; white-space: normal;">
                                                        <p>用温度采样的替代方法，称为Nucleus采样，该模型考虑了具有TOP_P概率质量质量的令牌的结果。因此，0.1表示仅考虑包含最高概率质量的令牌。默认为 1</p>
                                                    </div>
                                                </template>
                                                <el-icon style="color: #D9D9D9;"><i-ep-question-filled /></el-icon>
                                            </el-tooltip>
                                        </div>
                                </template>
                                <el-slider v-model="formData.topP" :min="0" :max="1" :step="0.1" show-input size="small" />
                                </el-form-item>
                                <div  style="padding-bottom: 10px; display: flex; align-items: center; font-size: 14px; gap: 10px;">
                                    <span v-if="formData.deepThinkingModel" style="padding: 3px 10px ; background: #EEF1FE; border-radius: 4px;">深度思考
                                        <el-tooltip content="模型思考过程" placement="top">
                                                <el-icon style="color: #D9D9D9;"><i-ep-question-filled /></el-icon>
                                            </el-tooltip>
                                    </span>
                                    <span v-if="formData.multimodal" style="padding: 3px 10px ; background: #EEF1FE; border-radius: 4px;">文件上传
                                        <el-tooltip content="支持基于用户上传的内容回答问题" placement="top">
                                                <el-icon style="color: #D9D9D9;"><i-ep-question-filled /></el-icon>
                                            </el-tooltip>
                                    </span>
                                    <span v-if="formData.webSearch" style="padding: 3px 10px ; background: #EEF1FE; border-radius: 4px;">联网搜索
                                        <el-tooltip content="支持直接在互联网查询公开资料  " placement="top">
                                                <el-icon style="color: #D9D9D9;"><i-ep-question-filled /></el-icon>
                                            </el-tooltip>
                                    </span>
                                </div>  
                            </div>
                        </div> 
                        <div  style="border: 1px solid #ebeef5; flex: 1; display: flex; flex-direction: column;">
                            <div style="height: 48px; font-size: 13px; background-color: #eaedffd4; line-height: 48px; padding: 0 16px; font-weight: bold;">
                                <div style="display: flex;align-items: center; justify-content: space-between;">
                                    <span>提示词</span>
                                    <el-button  type="primary" size="small" :disabled="!formData.prompt || !formData.modelName" icon="MagicStick" @click="handleImprovePrompt">
                                            AI优化
                                    </el-button>
                                </div>  
                            </div>
                            <div style="padding: 16px; flex: 1;">
                                <div class="prompt-container">
                                    <el-input
                                        v-model="formData.prompt"
                                        type="textarea"
                                        placeholder="请输入提示词，描述智能体的角色、能力和行为规范"
                                        show-word-limit
                                        autosize
                                        class="markdown-textarea full-height borderless"
                                        resize="none"
                                    />
                                </div>
                            </div>
                        </div>   
                   
                       
                    </div>

                   
                </div>
                <!-- 右侧40%：实时问答 -->
                <div class="chat-section" style="border-left: 1px solid #E5E5E5;">
                    <Wd-Agent :assistant="formData" mode="assistantchat" :chatUrl="chatUrl"/>
                </div>
            </div>
        </el-form>
        <ShortcutComponent v-model:visible="showShortcut" :shortcutObj="shortcutObj" @confirm="handleShortcut"/>
        <el-dialog v-model="promptVisible" title="提示词优化" width="800px">
            <div v-loading="improvingPrompt" element-loading-text="AI正在优化提示词...">
                <el-input v-model="prompt" type="textarea" :rows="15" 
                placeholder="请输入提示词，描述智能体的角色、能力和行为规范"                                       
                show-word-limit
                :autosize="{ minRows: 15, maxRows: 15 }"
                class="markdown-textarea full-height borderless"
                resize="none" />
            </div>
            <template #footer>
                <el-button style="float: left;" @click="handlePromptOptimize" type="primary" icon="Refresh" :disabled="improvingPrompt">重新优化</el-button>
                <el-button @click="promptVisible = false" :disabled="improvingPrompt">取消</el-button>
                <el-button type="primary" @click="handleUsePrompt" :disabled="improvingPrompt">使用</el-button>
            </template>
        </el-dialog>
    </div>   
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { debounce } from 'lodash-es'
import {  getModel } from '@/api/retrieve'
import { updateAssistant, getAssistantDetail, workflowList } from '@/api/knowledgeExpert'
import { knowledgeBaseList, knowledgeSpaceList } from '@/api/base'

import ShortcutComponent from '@/components/ShortcutComponent'

import { fileUpload } from '@/api/menu'

import { getToken } from '@/utils/auth'
import moment from 'moment'

import WdAgent from '@/components/WdAgent.vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const workflowOptions = ref([])
const userId = ref('')
const chatUrl = ref(import.meta.env.VITE_APP_BASE_API + '/knowledge/chat')
const formData = ref({
    uuid: '',
    name: '',
    logo: '',
    desc: '',

    modelName: '', // 大模型
    maxToken: 1000, // 最大回复长度
    topP: 1, // top-p
    dialogRound: 3, // 对话记忆轮数
    temperature: 0, // 生成多样性
    webSearch: false, // 联网搜索

    isRerank: true, // 是否开启重排
    rerankScore: 0.1, // 重排分
    rerankTopK: 5, // 重排top k
    rerankModel: 'qwen3-rerank', // 重排模型

    prompt: '',

    workflowUuids: [], // 工作流

    knowledgeList: null, // 知识库
    knowledgeSpaceList: [], // 知识空间
    ragTopK: 10, //  top k
    ragScore: 0.1, // 检索分
    knowledgeSearchType: 'fulltext',

    guideWord: '你好，我是小物智能助手，有什么可以帮您？', // 开场文案
    guideQuestions: [], // 推荐问题
       
    type: 'properties', // 默认为配置类型
    
    // 追问配置
    followup: {
        type: 'close',
        flowPrompt: '请按要求提供三条追问问题，问题应该与你最后一轮的回复内容紧密相关，可以引发进一步讨论',
        model: '',
        dialogNumber: 3,
        maxOutputToken: null
    },
    quickCommands: [], // 快捷指令
    bgImg: '' // 背景图
})

const spaceOptions = ref([]) // 知识空间

const defaultProps = ref({
    value: 'id',
    label: 'name',
    children: 'children',
    multiple: true,
    checkStrictly: true
})
// Refs
const formRef = ref()
const submitLoading = ref(false)
const improvingPrompt = ref(false)
const activeCollapse = ref([])
const prompt = ref('')
const promptVisible = ref(false)

// 知识库相关变量
const knowledgeOptions = ref([])

// 模型相关变量
const modelOptions = ref([])
const modelData = ref({})
const knowledgeIds = ref([])
const maxlength = ref(10000)

const showShortcut = ref(false)
const shortcutObj = ref({}) // 快捷指令
const shortcutIndex = ref(null) // 快捷指令索引

// 表单验证规则
const rules = {
    name: [
        { required: true, message: '请输入智能体名称', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    prompt: [
        { required: true, message: '请输入提示词', trigger: 'blur' },
        { min: 10, max: 2000, message: '长度在 10 到 2000 个字符', trigger: 'blur' }
    ]
}

// 自动保存功能相关状态
const isInitialLoad = ref(true)
const autoSaving = ref(false)
const autoSaveStatus = ref('') // 'saving' | 'saved' | 'failed' | ''

// 自动保存函数 - 复用 handleSubmit 方法
const performAutoSave = async () => {
    if (userId.value !== formData.value.createdBy) {
        return
    }
    if (isInitialLoad.value || autoSaving.value) {
        return
    }
    
    try {
        autoSaving.value = true
        autoSaveStatus.value = 'saving'
        
        // 直接调用 handleSubmit 方法，但不显示成功消息
        await handleSubmit(true) // 传入 true 表示是自动保存
        
        autoSaveStatus.value = 'saved'
        // 3秒后清除保存状态
        setTimeout(() => {
            autoSaveStatus.value = ''
        }, 3000)
    } catch (error) {
        console.error('自动保存失败:', error)
        autoSaveStatus.value = 'failed'
        setTimeout(() => {
            autoSaveStatus.value = ''
        }, 5000)
    } finally {
        autoSaving.value = false
    }
}

// 防抖的自动保存函数 - 3秒防抖
const debouncedAutoSave = debounce(performAutoSave, 3000)

// 知识库搜索方法
const getKnowledgeList = async () => {

    let params = {
       pageSize: 1000,
       pageNum: 1,
    }
    try {
        const response = await knowledgeBaseList(params)
        knowledgeOptions.value = response.data || []
    } catch (error) {
        console.error('获取知识库失败:', error)
        knowledgeOptions.value = []
    }
}

const getWorkflowList = async () => {
    let res = await workflowList()
    workflowOptions.value = res.data || []
}

const handleWorkflowChange = (value) => {
}

// 组件挂载时加载知识库
onMounted(async () => {
    userId.value = JSON.parse(localStorage.getItem('userInfo')).id
    // 先获取模型列表
    await getModelList()
    await getKnowledgeList()
    // await getWorkflowList()
    // 然后获取助手详情
    let uuid = route.query.uuid
    getAssistantDetail(uuid).then(res => {
        if (res.data && Object.keys(res.data).length > 0) {
            // 直接使用 res.data 更新 formData
            Object.assign(formData.value, res.data)
            
            // 确保必要的字段有默认值
            formData.value.guideQuestions = Array.isArray(res.data.guideQuestions) ? res.data.guideQuestions : []
            formData.value.tools = Array.isArray(res.data.tools) ? res.data.tools : []
            // 确保追问配置有默认值
            formData.value.followup = res.data.followup || {
                type: 'close',
                flowPrompt: '请按要求提供三条追问问题，问题应该与你最后一轮的回复内容紧密相关，可以引发进一步讨论',
                model: '',
                dialogNumber: 3,
                maxOutputToken: null
            }
            if (formData.value.prompt === null || formData.value.prompt === undefined || formData.value.prompt === '') {
                formData.value.prompt = `## 任务描述
                你是一个知识库回答助手，可以使用RAG中的内容作为你本次回答的参考。
                ## 通用规则
                - 如果你不清楚答案，你需要澄清。
                - 保持答案与知识库中描述的一致。
                - 使用 Markdown 语法优化回答格式。尤其是图片、表格、序列号等内容，需严格完整输出。
                - 如果有合适的图片作为回答，则必须输出图片。输出图片时，仅需输出图片的 url，不要输出图片描述，例如：![](url)。
                - 使用与问题相同的语言回答。`
            }
            formData.value.quickCommands = Array.isArray(res.data.quickCommands) ? res.data.quickCommands : []
            // 设置知识库ID列表
            activeCollapse.value = ['knowledge', 'rerank', 'dialog']
            getModelName(formData.value.modelName)
            
            // 根据模型能力设置功能开关状态
            if (modelData.value) {
                formData.value.deepThinkingModel = modelData.value.deepThinkingModel
                formData.value.multimodal = modelData.value.multimodal
                formData.value.webSearch = modelData.value.webSearch
            }
            
            // 如果有选中的知识库，加载知识空间列表以便回显
            if (formData.value.knowledgeList) {
                // 保存当前的 knowledgeSpaceList（ID数组），用于后续回显
                const savedSpaceIds = Array.isArray(formData.value.knowledgeSpaceList) 
                    ? [...formData.value.knowledgeSpaceList] 
                    : []
                
                // 加载知识空间列表
                getknowledgeSpaceList(formData.value.knowledgeList).then(() => {
                    // 加载完成后，将ID数组转换为完整路径进行回显
                    if (savedSpaceIds.length > 0 && spaceOptions.value.length > 0) {
                        const paths = convertIdsToPaths(savedSpaceIds, spaceOptions.value)
                        nextTick(() => {
                            formData.value.knowledgeSpaceList = paths
                        })
                    }
                })
            }
        }
    })
    
    // 数据加载完成后，开启自动保存监听
    setTimeout(() => {
        isInitialLoad.value = false
    }, 1000)
})

// 监听 formData 变化，触发自动保存
watch(
    () => formData.value,
    (newVal, oldVal) => {
        // 只有在初始加载完成后才触发自动保存
        if (!isInitialLoad.value) {
            debouncedAutoSave()
        }
    },
    { deep: true }
)

// 组件卸载时清理防抖函数
onUnmounted(() => {
    debouncedAutoSave.cancel()
})

const componentsType = ref('')

const getModelName = (name) => {
    modelOptions.value.forEach(item => {
        if (item.model === name) {
            modelData.value = item
        }
    })
}

const handleKnowledgeChange = (value) => {
    // let list = knowledgeOptions.value.filter(item => value.includes(item.id))
    // formData.value.knowledgeList = list.map(item => ({
    //     id: item.id,
    //     description: item.description,
    //     name: item.name
    // }))
    // 切换知识库时，清空知识空间选择
    formData.value.knowledgeSpaceList = []
    getknowledgeSpaceList(value)
}

// 根据ID在树形数据中查找完整路径
const findPathInTree = (tree, targetId, path = []) => {
  for (const node of tree) {
    const currentPath = [...path, node.id]
    if (node.id === targetId) {
      return currentPath
    }
    if (node.children && node.children.length > 0) {
      const found = findPathInTree(node.children, targetId, currentPath)
      if (found) {
        return found
      }
    }
  }
  return null
}

// 将ID数组转换为完整路径数组（用于回显）
const convertIdsToPaths = (ids, treeData) => {
  if (!Array.isArray(ids) || ids.length === 0 || !treeData || treeData.length === 0) {
    return []
  }
  
  const paths = []
  for (const id of ids) {
    const path = findPathInTree(treeData, id)
    if (path) {
      paths.push(path)
    }
  }
  return paths
}

// 提取每层数组的最后一个元素
const extractLastElements = (paths) => {
  if (!Array.isArray(paths) || paths.length === 0) {
    return []
  }
  return paths.map(path => {
    if (Array.isArray(path) && path.length > 0) {
      return path[path.length - 1]
    }
    return path
  })
}

const handleKnowledgeSpaceChange = (val) => {
  if (Array.isArray(val) && val.length > 0) {
    const lastElements = extractLastElements(val)
    formData.value.knowledgeSpaceList = lastElements
  } else {
    formData.value.knowledgeSpaceList = []
  }
}

// 知识空间
const getknowledgeSpaceList = async (val) => {
    const res = await knowledgeSpaceList(val)
    spaceOptions.value = res.data || []
}

// 添加引导问题
const addQuestion = () => {
    if (formData.value.guideQuestions.length < 5) {
        formData.value.guideQuestions.push('')
    }
}

// 删除引导问题
const removeQuestion = (index) => {
    if (formData.value.guideQuestions.length > 1) {
        formData.value.guideQuestions.splice(index, 1)
    } else {
        // 如果只剩一个问题，将其清空而不是删除
        formData.value.guideQuestions[0] = ''
    }
}

const beforeAvatarUpload = (rawFile) => {
  if (!['jpg', 'png', 'jpeg'].includes(rawFile.type.split('/').slice(-1)[0])) {
    ElMessage.error('图片格式必须是 JPG、PNG 或 JPEG!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}
const fileUploadFn = async (req) => {
  const file = req.file
  const format = file.type.split('/').slice(-1)[0]
  const { data } = await fileUpload(file, 'file', format)
  let url = alarmImageUrlConn(data)
  formData.value.bgImg = url
}

// 删除背景图
const removeBgImg = () => {
  formData.value.bgImg = ''
  ElMessage.success('背景图已删除')
}

const alarmImageUrlConn = (imgUrl) => {
  if (!imgUrl) return ''
  const IMAGE_ROOT = import.meta.env.VITE_APP_BASE_API
  return IMAGE_ROOT + imgUrl
}

const handleModelClick = (val) => {
    
    modelData.value = modelOptions.value.find(item => item.model === val)
    console.log('modelData.value', modelData.value)
    maxlength.value = modelData.value.maxTokens
    formData.value.webSearch =  modelData.value.webSearch
    formData.value.multimodal = modelData.value.multimodal
    formData.value.deepThinkingModel = modelData.value.deepThinkingModel
}

// 快捷指令方法
const openShortcut = (type, row, ind) => {
    showShortcut.value = true
    if (type === 'edit') {
        shortcutObj.value = {...row}
        shortcutIndex.value = ind
    } else {
        shortcutObj.value = {}
        shortcutIndex.value = null
    }
}

const removeQuickCommand = (index) => {
    formData.value.quickCommands.splice(index, 1)
}

const handleShortcut = (shortcut) => {  
    // 确保 quickCommands 是数组
    if (!Array.isArray(formData.value.quickCommands)) {
        formData.value.quickCommands = []
    }
    
    if (shortcutIndex.value !== null) {
        // 编辑模式：更新指定索引的数据
        formData.value.quickCommands[shortcutIndex.value] = shortcut
    } else {
        // 新增模式：添加到数组末尾
        formData.value.quickCommands.push(shortcut)
    }
}
// AI优化提示词
const handleImprovePrompt = async () => {
    // 检查是否已选择模型
    if (!formData.value.modelName) {
        ElMessageBox.alert('请先选择模型再进行AI优化', '提示', {
            confirmButtonText: '确定',
            type: 'warning'
        })
        return
    }

    promptVisible.value = true
    prompt.value = ''  // 清空内容准备接收新内容
    improvingPrompt.value = true
    let currentContent = ''  // 用于存储完整内容
    
    try {
        const baseURL = import.meta.env.VITE_APP_BASE_API
        const token = getToken() 
        await fetchEventSource(`${baseURL}/api/agent/v2/assistant/optimizePrompt`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({
                assistantUuid: formData.value?.uuid || '',
                prompt: formData.value.prompt,
                modelId: modelData.value.id,
                providerCode: modelData.value.providerCode
            }),
            onopen: (response) => {
                if (response.ok && response.status === 200) {
                } else {
                    throw new Error(`SSE连接失败: ${response.status} ${response.statusText}`)
                }
            },
            onmessage: (event) => {
                try {
                    const data = event.data
                    
                    if (data === '[DONE]') {
                        improvingPrompt.value = false
                        ElMessage.success('AI优化完成！')
                        return
                    }

                    // 尝试解析JSON数据
                    let content = ''
                    try {
                        const parsedData = JSON.parse(data)
                        if (parsedData.outputType === 'text') {
                            content = parsedData.content || ''
                        } else {
                            return // 如果不是 text 类型，直接返回不处理
                        }
                    } catch (e) {
                        // 如果解析失败，不处理这条数据
                        return
                    }
                    
                    // 如果没有有效内容，直接返回
                    if (!content) return

                    // 累积完整内容
                    currentContent += content

                    // 当有文字输出时停止 loading
                    if (improvingPrompt.value) {
                        improvingPrompt.value = false
                    }

                    // 创建打字机效果
                    let displayedLength = prompt.value.length
                    const animateText = () => {
                        if (displayedLength < currentContent.length) {
                            // 每次显示多个字符，加快速度
                            const charsToAdd = Math.min(3, currentContent.length - displayedLength)
                            prompt.value = currentContent.substring(0, displayedLength + charsToAdd)
                            displayedLength += charsToAdd
                            // 使用 setTimeout 控制速度，比 requestAnimationFrame 更快
                            setTimeout(animateText, 10) // 10ms 间隔，比默认的 16ms 更快
                        }
                        // 自动滚动到底部 - 适配 el-textarea
                        const textarea = document.querySelector('.el-dialog .el-textarea__inner')
                        if (textarea) {
                            textarea.scrollTop = textarea.scrollHeight
                        }
                    }
                    animateText()
                    
                } catch (error) {
                    console.error('fetchEventSource 处理SSE消息时出错:', error)
                }
            },
            onclose: () => {
                improvingPrompt.value = false
            },
            onerror: (err) => {
                improvingPrompt.value = false
                ElMessage.error('优化过程中出现错误')
                throw err
            }
        })
    } catch (error) {
        improvingPrompt.value = false
        prompt.value = formData.value.prompt
        ElMessage.error('优化失败，请重试')
        console.error('Improve prompt error:', error)
    }
}

// 使用优化后的提示词
const handleUsePrompt = () => {
    formData.value.prompt = prompt.value
    promptVisible.value = false
    ElMessage.success('已应用优化后的提示词')
}

const handleFollowupTypeChange = () => {
    if (formData.value.followup.type === 'close') {
        formData.value.followup.model = ''
        formData.value.followup.flowPrompt = ''
    } else if (formData.value.followup.type === 'div') {
        // 切换到自定义模式时，如果flowPrompt为空，设置默认值
        if (!formData.value.followup.flowPrompt) {
            formData.value.followup.flowPrompt = '请按要求提供三条追问问题，问题应该与你最后一轮的回复内容紧密相关，可以引发进一步讨论'
        }
    }
}

// 重新优化提示词
const handlePromptOptimize = () => {
    handleImprovePrompt()
}

const handleRerankModelChange = (val) => {
    let data = modelOptions.value.find(item => item.model === val)
    console.log('data', data)

}

// 提交表单
const handleSubmit = async (isAutoSave = false) => {
    try {     
        // 非工作流模式下验证表单
        if (formRef.value) {
            await formRef.value.validate()
        }
        
        // 只有手动保存时才显示loading状态
        if (!isAutoSave) {
            submitLoading.value = true
        }
        
        // 确保 knowledgeSpaceList 是 ID 数组格式，而不是路径数组格式
        let knowledgeSpaceList = formData.value.knowledgeSpaceList
        
        // 处理 knowledgeSpaceList：如果是路径数组格式 [[109]]，转换为 ID 数组 [109]
        if (Array.isArray(knowledgeSpaceList)) {
            if (knowledgeSpaceList.length > 0) {
                // 检查第一个元素是否是数组（说明是路径数组格式，如 [[109]]）
                const firstElement = knowledgeSpaceList[0]
                if (Array.isArray(firstElement)) {
                    // 如果是路径数组，提取每个路径的最后一个元素
                    knowledgeSpaceList = extractLastElements(knowledgeSpaceList)
                }
                // 如果已经是 ID 数组格式 [109]，直接使用
            } else {
                // 空数组，保持为空数组
                knowledgeSpaceList = []
            }
        } else {
            // 如果不是数组，设为空数组
            knowledgeSpaceList = []
        }
        
        // 构建提交数据，确保 knowledgeSpaceList 是 ID 数组格式
        const submitData = {
            ...formData.value,
            type: formData.value.type, // 使用传入的智能体类型
            guideQuestions: formData.value.guideQuestions.filter(q => q.trim()),
            knowledgeSpaceList: knowledgeSpaceList // 明确指定为处理后的 ID 数组格式，覆盖扩展运算符的值
        }
        
        let response = await updateAssistant(submitData)
        
        if (response.code === 'ok') {
            // 只有手动保存时才显示成功消息
            if (!isAutoSave) {
                ElMessage.success('更新成功')
            }
        } else {
            const errorMsg = response.msg || '更新失败'
            // 自动保存和手动保存都显示错误消息
            if (!isAutoSave) {
                ElMessage.error(errorMsg)
            }
            throw new Error(errorMsg)
        }
        
    } catch (error) {
        if (error.message !== 'Validation failed') {
            // 自动保存时不显示错误消息，让自动保存函数处理
            if (!isAutoSave) {
                ElMessage.error('更新失败')
            }
        }
        // 重新抛出错误，让自动保存函数能捕获到
        throw error
    } finally {
        // 只有手动保存时才重置loading状态
        if (!isAutoSave) {
            submitLoading.value = false
        }
    }
}


// 取消操作
const handleCancel = () => {
    router.push('/home/specialist')
}

const getModelList = async () => {
    let params = {
        pageNum: 1,
        pageSize: 1000,
    }
    const res = await getModel(params)
    modelOptions.value = res.data
}
</script>

<style lang="scss" scoped>
.assistant-form {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.form-header {
    display: flex;
    height: 42px;
    background: #fff;
    padding: 0 20px;
}

.assistant-form-content {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    min-height: 0;
}

.form-layout {
    display: flex;
    margin-top: 20px;
    background: #fff;
    flex: 1;
    overflow: hidden;
}

.edit-section {
    width: 65%;
    display: flex;
}

.prompt-column,
.config-column {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
}
.prompt-column {
    border-left: 1px solid #E5E5E5;
}

.chat-section {
    width: 35%;
    display: flex;
    flex-direction: column;
}

.prompt-container {
    display: flex;
    flex-direction: column;
    overflow: hidden;
    position: relative;
    min-height: 0;
    height: 100%;
    .markdown-textarea {
        &.full-height.borderless {
            flex: 1 1 auto;
            height: 100%;
            min-height: 100%;
            display: flex;
            flex-direction: column;
            overflow: hidden;
            
            :deep(.el-textarea) {
                height: 100% !important;
                min-height: 100% !important;
                flex: 1 1 auto !important;
                display: flex !important;
                flex-direction: column !important;
                border: none !important;
                background: transparent !important;
                overflow: hidden !important;
                box-shadow: none !important;
                position: relative !important;
                
                .el-textarea__inner {
                    height: 100% !important;
                    max-height: none !important;
                    flex: 1 1 auto !important;
                    line-height: 1.6 !important;
                    border: none !important;
                    border-radius: 0 !important;
                    resize: none !important;
                    padding: 12px !important;
                    margin: 0 !important;
                    background: transparent !important;
                    box-shadow: none !important;
                    outline: none !important;
                    overflow-y: auto !important;
                    scrollbar-width: none !important;
                    -ms-overflow-style: none !important;
                    font-size: 14px !important;
                    position: absolute !important;
                    top: 0 !important;
                    left: 0 !important;
                    right: 0 !important;
                    bottom: 0 !important;
                    
                    &::-webkit-scrollbar {
                        display: none !important;
                        width: 0px !important;
                        background: transparent !important;
                    }
                    
                    &::-webkit-scrollbar-track {
                        display: none !important;
                    }
                    
                    &::-webkit-scrollbar-thumb {
                        display: none !important;
                    }
                    
                    &:focus,
                    &:hover,
                    &:focus-visible,
                    &:active {
                        border: none !important;
                        box-shadow: none !important;
                        outline: none !important;
                    }
                }
                
                // 字数统计位置调整
                .el-input__count {
                    position: absolute !important;
                    bottom: 8px !important;
                    right: 8px !important;
                    background: rgba(255, 255, 255, 0.8) !important;
                    padding: 2px 6px !important;
                    border-radius: 4px !important;
                    font-size: 12px !important;
                    color: #999 !important;
                    border: none !important;
                    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1) !important;
                    z-index: 10 !important;
                }
            }
        }
    }
}

.field-container {
    transition: all 0.3s ease;
    width: 100%;
}

:deep(.el-collapse-item__title) {
    font-weight: bold;
}

.config-container {
    flex: 1;
    
    .config-collapse {
        border: none;
        
        :deep(.el-collapse-item__header) {
            background: #fafbfc;
            border: 1px solid #ebeef5;
            padding: 0 16px;
            font-weight: 500;
            color: #333;
            height: 40px;
            &.is-active {
                background: rgba(234,237,255,0.83);
                color: #6B05A8;
                color: #6B05A8;
            }
        }
        
        :deep(.el-collapse-item__content) {
            padding: 16px;
            border: 1px solid #ebeef5;
            border-top: none;
        }
        
        :deep(.el-collapse-item) {
            margin-bottom: 8px;
            
            &:last-child {
                margin-bottom: 0;
            }
        }
    }
}

.chat-section .chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 0 10px 0;   
}

.assistant-form-content {
    .el-form-item {
        margin-bottom: 10px;
    }
}

.guide-questions {
    .question-item {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 10px;
        
        .el-input {
            flex: 1;
        }
    }
}

:deep(.el-textarea) {
    .el-textarea__inner {
        resize: none;
    }
}
:deep(.el-input-number) {
    width: 130px !important;
}
:deep(.el-cascader-node) {
    .el-icon {
        display: none !important;
    }
}
.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
}
.avatar-uploader .el-upload {
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  border: 1px #f5f5f5 solid;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  border: 1px dashed var(--el-border-color);
}
:deep(.el-slider__runway) {
    height: 4px !important;
    .el-slider__bar {
        height: 4px;
    }
    .el-slider__button-wrapper {
        top: -17px;
        .el-slider__button {
            height: 14px;
            width: 14px;
        }
    }
}
:deep(.el-radio-group) {
    width: 100%;
    background: #f5f5f5;
    justify-content: space-around;
}
:deep(.el-collapse-item__header.is-active) {
    color: #333 !important;
}
</style>

<style lang="scss">
/* 全局强制覆盖Element UI默认样式 */
.el-upload--picture-card {
    height: 100px;
    width: 100px;
}

.auto-save-status {
    display: flex;
    align-items: center;
    font-size: 12px;
    color: #666;
    
    .is-loading {
        animation: rotate 1s linear infinite;
    }
}

@keyframes rotate {
    from {
        transform: rotate(0deg);
    }
    to {
        transform: rotate(360deg);
    }
}

// 特别针对提示词容器的强制样式
.prompt-container .el-textarea .el-textarea__inner {
    height: 100% !important;
}
.segment-options {
    margin-bottom: 10px;
    .segment-radio-group {
        display: flex;
        flex-direction: column;
        gap: 10px;
       
        .radio-option {
            padding: 3px 10px;
            background: #F5F5F5;
            width: 100%; 
            :deep(.el-radio) {
                width: 100%;
                
                .el-radio__label {
                    width: 100%;
                    padding-left: 20px;
                }
            }
            
            .radio-content {
                .radio-title {
                    font-size: 14px;
                    color: #303133;
                }
            }
        }
    }
}
:deep(.el-slider__button) {
    width: 15px !important;
    height: 15px !important;
}

.selectWidth {
    width: 100%;
}



</style> 