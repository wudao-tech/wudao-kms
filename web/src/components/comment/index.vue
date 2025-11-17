<template>
    <div class="comment-container">
        <!-- 评论区标题 -->
        <div class="comment-header">
            评论区 ({{ totalComments }})
        </div>

        <!-- 评论列表 -->
        <div class="comment-list">
            <div 
                v-for="comment in displayComments" 
                :key="comment.id" 
                class="comment-item"
            >
                <!-- 评论主体 -->
                <div class="comment-main">
                    <div class="comment-avatar">
                        <!-- <div v-if="comment.user.avatar" class="avatar-circle">
                            <img style="width: 100%; height: 100%; border-radius: 50%;" :src="comment.user.avatar" />
                        </div> -->
                        <div class="avatar-circle" :style="{ backgroundColor: comment.avatarColor }">
                            {{ comment.avatarText }}
                        </div>
                    </div>
                    <div class="comment-content">
                        <div class="comment-user">
                            {{ comment.username }}
                        </div>
                        <div class="comment-text">
                            <span v-if="!comment.expanded && comment.content.length > 40">
                                {{ comment.content.substring(0, 40) }}...
                                <span class="expand-link" @click="expandComment(comment.id)">展开</span>
                            </span>
                            <span v-else>{{ comment.content }}</span>
                        </div>
                        
                        <!-- 评论图片 -->
                        <div v-if="comment.images && comment.images.length > 0" class="comment-images">
                            <img 
                                v-for="(image, index) in comment.images" 
                                :key="index"
                                :src="image"
                                class="comment-image"
                                @click="previewImage(image)"
                            />
                        </div>

                        <div class="comment-footer">
                            <span class="comment-time">{{ comment.time }}</span>
                            <div class="comment-actions">
                                                                 <!-- <el-button 
                                     link
                                     size="small"
                                     @click="toggleLike(comment.id)"
                                     :class="{ 'liked': comment.isLiked }"
                                 >
                                    <el-icon class="like-icon"><Download /></el-icon>
                                 </el-button> -->
                                <el-button 
                                    link
                                    size="small"
                                    @click="showReplyInputFn(comment.id)"
                                >
                                    <el-icon><ChatDotRound /></el-icon>
                                </el-button>
                                <el-button 
                                    v-if="comment.canDelete"
                                    link
                                    size="small"
                                    @click="deleteComment(comment.id)"
                                >
                                    <el-icon><Delete /></el-icon>
                                </el-button>
                            </div>
                        </div>

                        <!-- 评论回复输入框 -->
                        <div v-if="comment.showReplyInput" class="reply-input">
                            <div class="input-wrapper">
                                <textarea 
                                    v-model="comment.replyText" 
                                    placeholder="回复评论"
                                    class="reply-textarea"
                                ></textarea>
                                <el-button 
                                    type="primary" 
                                    size="small"
                                    class="send-btn"
                                    @click="postReply(comment.replyText, comment.id)"
                                >
                                    发送
                                </el-button>
                            </div>
                        </div>

                        <!-- 回复列表 -->
                        <div v-if="(comment.replies && comment.replies.length > 0) && comment.showReplyList" class="reply-list">
                            <div 
                                v-for="reply in comment.displayReplies" 
                                :key="reply.id" 
                                class="reply-item"
                            >
                                <div class="reply-avatar">
                                    <!-- <div v-if="reply.user.avatar" class="avatar-circle" :style="{ backgroundColor: reply.avatarColor }">
                                        {{ reply.avatarText }}
                                    </div> -->
                                    <div class="avatar-circle" :style="{ backgroundColor: reply.avatarColor }">
                                        {{ reply.avatarText }}
                                    </div>
                                </div>
                                <div class="reply-content">
                                    <div class="reply-user">
                                        {{ reply.username  }}
                                        <span v-if="reply.replyTo && reply.pid !== comment.id" class="reply-to">
                                            <el-icon style="font-size: 15px; margin-right: 5px"><CaretRight /></el-icon> {{ getUserName(reply.replyTo) }}
                                        </span>
                                    </div>
                                    <div class="reply-text">{{ reply.content }}</div>
                                    <div class="reply-footer">
                                        <span class="reply-time">{{ reply.time }}</span>
                                        <div>
                                            <el-button 
                                            link
                                            size="small"
                                            @click="showReplyInputFn(comment.id, reply.id)"
                                            >
                                                    <el-icon><ChatDotRound /></el-icon>
                                            </el-button>
                                            <el-button 
                                            v-if="reply.canDelete"
                                            link
                                            size="small"
                                            @click="deleteComment(reply.id, comment.id)"
                                        >
                                            <el-icon><Delete /></el-icon>
                                        </el-button>
                                        </div>
                                    </div>
                                    
                                    <!-- 回复输入框 - 显示在当前回复项下面 -->
                                    <div v-if="reply.showReplyInput" class="reply-input">
                                        <div class="input-wrapper">
                                            <textarea 
                                                v-model="reply.replyText" 
                                                placeholder="回复评论"
                                                class="reply-textarea"
                                            ></textarea>
                                            <el-button 
                                                type="primary" 
                                                size="small"
                                                class="send-btn"
                                                @click="postReply(reply.replyText, comment.id, reply.id, reply.userId || reply.id)"
                                            >
                                                发送
                                            </el-button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                                                    <!-- 展开/收起回复控制 -->
                        <div v-if="(comment.replies && comment.replies.length > 1) && comment.showAllReplies" class="expand-replies">
                            <!-- <el-button link class="expand-link" @click="expandReplies(comment.id)">
                                展开更多
                            </el-button> -->
                            <el-button link class="collapse-link" @click="collapseReplies(comment.id)">
                                收起
                            </el-button>
                        </div>
                        </div>

                        <!-- 展开回复链接 - 只有当回复数量大于当前显示的回复数量时才显示 -->
                        <div v-if="comment.replyCount > 2 && !comment.showAllReplies" class="expand-replies-link">
                            <el-button link class="expand-link" @click="showReplies(comment)">
                                展开全部{{ comment.replyCount }}条回复
                            </el-button>
                            <el-icon><ArrowRight /></el-icon>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 加载更多 -->
        <!-- <div v-if="hasMoreComments" class="load-more">
            <el-button type="text" @click="loadMoreComments">加载更多评论</el-button>
        </div> -->
    </div>
</template>

<script setup> 
import { ref, computed, onMounted, defineProps } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import moment from 'moment'
import { listUser } from '@/api/system/user'
import { getCommentList, deleteComments, deleteReplys, getReplyList, addReply } from '@/api/comment'
const target = 'knowledge'

const props = defineProps({
    targetId: {
        type: String,
        default: () => ''
    }
})

// 响应式数据
const newComment = ref('')
const totalComments = ref(0)
const hasMoreComments = ref(true)
const comments = ref([])

const userInfo = ref({})
const userList = ref([])

// 根据用户ID生成固定颜色
const generateAvatarColor = (userId) => {
    // 颜色组映射规则：1,5 -> 组0；2,4 -> 组1；3,6 -> 组2；0,7 -> 组3；8,9 -> 组4
    const colorGroups = {
        0: '#409EFF',  // 蓝色 - 对应ID末尾 1,5
        1: '#67C23A',  // 绿色 - 对应ID末尾 2,4
        2: '#E6A23C',  // 橙色 - 对应ID末尾 3,6
        3: '#F56C6C',  // 红色 - 对应ID末尾 0,7
        4: '#9C27B0'   // 紫色 - 对应ID末尾 8,9
    }
    
    // 将userId转换为字符串并获取最后一位
    const userIdStr = String(userId || '0')
    const lastDigit = parseInt(userIdStr[userIdStr.length - 1]) || 0
    
    // 根据最后一位数字确定颜色组
    let colorGroup
    if (lastDigit === 1 || lastDigit === 5) {
        colorGroup = 0
    } else if (lastDigit === 2 || lastDigit === 4) {
        colorGroup = 1
    } else if (lastDigit === 3 || lastDigit === 6) {
        colorGroup = 2
    } else if (lastDigit === 0 || lastDigit === 7) {
        colorGroup = 3
    } else { // 8, 9
        colorGroup = 4
    }
    
    return colorGroups[colorGroup]
}

// 生成头像文字
const generateAvatarText = (username) => {
    return username.substring(0, 2).toUpperCase()
}

// 计算属性
const displayComments = computed(() => {
    return comments.value.map(comment => {
        // 为每个评论添加 displayReplies 属性，但保持原对象的响应式
        const replies = comment.replies || []
        // 如果已经展开了全部回复，显示所有回复；否则只显示前2条
        comment.displayReplies = comment.showAllReplies 
            ? replies 
            : replies.slice(0, 2)
        return comment
    })
})


// 获取评论列表
const getCommentFn = () => {
  let params = {
    targetId: props.targetId,
    pageNum: 1,
    pageSize: 999
  }
  getCommentList(target, params).then(res => {
    comments.value = res.data.map(item => {
        return {
            ...item,
            username: item.user.nickname,
            time: moment(item.updatedAt).format('MM-DD HH:mm'),
            userId: item.userId,
            avatarColor: generateAvatarColor(item.userId),
            avatarText: generateAvatarText(item.user.nickname),
            isLiked: item.topped, // 是否置顶
            canDelete: userInfo.value.id === item.userId ? true : false, // 是否可以删除
            expanded: false, // 是否展开
            showReplyInput: false, // 是否显示回复输入框
            replyText: '', // 回复输入框内容
            showReplyList: true, // 是否显示回复列表 - 初始化时默认显示
            showAllReplies: false, // 是否显示所有回复
            replyCount: item.replyCount || 0, // 回复数量
            replies: item.replies && item.replies.length > 0 ? item.replies.map(i => {
                return {
                    ...i,
                    username: i.user.nickname,
                    userId: i.userId,
                    time: moment(i.updatedAt).format('MM-DD HH:mm'),
                    avatarColor: generateAvatarColor(i.userId),
                    avatarText: generateAvatarText(i.user.nickname),
                    canDelete: userInfo.value.id === i.userId ? true : false,
                    showReplyInput: false, // 是否显示回复输入框
                    replyText: '' // 回复输入框内容
                }
            }) : []
        }
    })
    console.log('comments.value', comments.value)
    totalComments.value = res.total
  }).catch(err => {
    ElMessage.error('获取评论失败')
  })

//   axios.get(iotUrl + `/api/comment/${target}/list`, {
//     params: params,
//     headers: {
//       Authorization: token,
//     }
//   }).then(res => {
//     console.log('res', res.data.data)
//     comments.value = res.data.data.map(item => {
//         return {
//             ...item,
//             username: item.user.nickname,
//             time: moment(item.updatedAt).format('MM-DD HH:mm'),
//             avatarColor: generateAvatarColor(),
//             avatarText: generateAvatarText(item.user.nickname),
//             isLiked: item.topped, // 是否置顶
//             canDelete: userInfo.value.user.userId === item.userId ? true : false, // 是否可以删除
//             expanded: false, // 是否展开
//             showReplyInput: false, // 是否显示回复输入框
//             replyText: '', // 回复输入框内容
//             showReplyList: true, // 是否显示回复列表 - 初始化时默认显示
//             showAllReplies: false, // 是否显示所有回复
//             replyCount: item.replyCount || 0, // 回复数量
//             replies: item.replies && item.replies.length > 0 ? item.replies.map(i => {
//                 return {
//                     ...i,
//                     username: i.user.nickname,
//                     time: moment(i.updatedAt).format('MM-DD HH:mm'),
//                     avatarColor: generateAvatarColor(),
//                     avatarText: generateAvatarText(i.user.nickname),
//                     canDelete: userInfo.value.user.userId === i.userId ? true : false,
//                     showReplyInput: false, // 是否显示回复输入框
//                     replyText: '' // 回复输入框内容
//                 }
//             }) : []
//         }
//     })
//     console.log('comments.value', comments.value)
//     totalComments.value = res.data.total
//   }).catch(error => {
//     console.error('获取评论失败:', error)
//     ElMessage.error('获取评论失败')
//   })
}

const expandComment = (commentId) => {
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) {
        comment.expanded = true
    }
}

const toggleLike = (commentId) => {
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) {
        comment.isLiked = !comment.isLiked
    }
}

const showReplyInputFn = (commentId, replyId = null) => {
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) {
        if (replyId) {
            // 如果是回复某个回复，需要找到对应的回复项
            const reply = comment.replies.find(r => r.id === replyId)
            if (reply) {
                // 关闭其他回复的输入框
                comment.replies.forEach(r => {
                    if (r.id !== reply.id) {
                        r.showReplyInput = false
                    }
                })
                reply.showReplyInput = !reply.showReplyInput
                // if (reply.showReplyInput) {
                //     reply.replyText = `@${reply.username} `
                // }
            }
        } else {
            // 如果是回复评论本身
            comment.showReplyInput = !comment.showReplyInput
            if (comment.showReplyInput) {
                comment.replyText = ''
            }
        }
    }
}

const postReply = (val, commentId, pid, replyTo) => {
    const comment = comments.value.find(c => c.id === commentId) 
    if (!val || !val.trim()) {
        ElMessage.warning('请输入回复内容')
        return
    }
    let params = {
        commentId: commentId,
        pid: pid ? pid : commentId,
        content: val,
        replyTo
    }
    addReply(target, params).then(res => {
        if (pid && pid !== commentId) {
            // 如果是回复某个回复，清空并关闭该回复的输入框
            const reply = comment.replies.find(r => r.id === pid)
            if (reply) {
                reply.replyText = ''
                reply.showReplyInput = false
            }
        } else {
            // 如果是回复评论本身，清空并关闭评论的输入框
            comment.replyText = ''
            comment.showReplyInput = false
        }
        setTimeout(() => {
            getCommentFn()
            ElMessage.success('回复发表成功')
        }, 1000)
       
    })
}

const deleteComment = async (id, commentId) => {
    try {
        await ElMessageBox.confirm(`确定要删除这条${commentId ? '回复' : '评论'}吗？`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        if (commentId) { // 删除的是回复
            let params = {
                id: id,
                commentId: commentId,
            }
            deleteReplys(target, params).then(res => {
                setTimeout(() => {
                    getCommentFn()
                    ElMessage.success('删除回复成功')
                }, 1000)
            })
        } else { // 删除的是评论
            let params = {
                id: id,
                targetId: props.targetId,
            }
            deleteComments(target, params).then(res => {
                setTimeout(() => {
                    getCommentFn()
                    ElMessage.success('删除评论成功')
                }, 1000)
            })
        }
        // if (commentId) { // 删除的是回复
        //     axios.delete(iotUrl + `/api/comment/${target}/reply`, {
        //         params: {
        //             id: id,
        //             commentId: commentId,
        //         },
        //         headers: {
        //             Authorization: token,
        //         }
        //     }).then(res => {
        //         getCommentFn()
        //         ElMessage.success('删除回复成功')
        //     })
        // } else { // 删除的是评论
        //   axios.delete(iotUrl + `/api/comment/${target}`, {
        //     params: {
        //         id: id,
        //         targetId: props.targetId,
        //     },
        //     headers: {
        //         Authorization: token,
        //     }
        //   }).then(res => {
        //     console.log('res', res.data)
        //     getCommentFn()
        //     ElMessage.success('删除评论成功')
        //   })
        // }
        
    } catch {
        // 用户取消删除
    }
}

const showReplies = (comment) => {
    let params = {
        commentId: comment.id,
        pageNum: 1,
        pageSize: 100
    }
    getReplyList(target, params).then(res => {
        console.log('1res', res.data)
        // 找到对应的评论并更新其回复列表
        const commentIndex = comments.value.findIndex(c => c.id === comment.id)
        if (commentIndex !== -1) {
            // 更新回复列表
            comments.value[commentIndex].replies = res.data.map(item => ({
                ...item,
                username: item.user?.nickname || '用户',
                userId: item.userId,
                time: moment(item.updatedAt).format('MM-DD HH:mm'),
                avatarColor: generateAvatarColor(item.userId),
                avatarText: generateAvatarText(item.user?.nickname || '用户'),
                canDelete: userInfo.value?.id === item.userId ? true : false,
                showReplyInput: false,
                replyText: ''
            }))
            // 更新回复数量
            comments.value[commentIndex].replyCount = res.data.length
            // 显示回复列表 - 显示全部回复
            comments.value[commentIndex].showReplyList = true
            comments.value[commentIndex].showAllReplies = true
        }
    })
    // axios.get(iotUrl + `/api/comment/${target}/reply/list`, {
    //     params: {
    //         commentId: comment.id,
    //         pageNum: 1,
    //         pageSize: 100
    //     },
    //     headers: {
    //         Authorization: token,
    //     }
    // }).then(res => {
    //     console.log('1res', res.data.data)
    //     // 找到对应的评论并更新其回复列表
    //     const commentIndex = comments.value.findIndex(c => c.id === comment.id)
    //     if (commentIndex !== -1) {
    //         // 更新回复列表
    //         comments.value[commentIndex].replies = res.data.data.map(item => ({
    //             ...item,
    //             username: item.user?.nickname || '用户',
    //             time: moment(item.updatedAt).format('MM-DD HH:mm'),
    //             avatarColor: generateAvatarColor(),
    //             avatarText: generateAvatarText(item.user?.nickname || '用户'),
    //             canDelete: userInfo.value.user?.userId === item.userId ? true : false,
    //             showReplyInput: false,
    //             replyText: ''
    //         }))
    //         console.log(123, comments.value[commentIndex].replies)
    //         // 更新回复数量
    //         comments.value[commentIndex].replyCount = res.data.data.length
    //         // 显示回复列表 - 显示全部回复
    //         comments.value[commentIndex].showReplyList = true
    //         comments.value[commentIndex].showAllReplies = true
    //     }
    // }).catch(error => {
    //     console.error('获取回复列表失败:', error)
    //     ElMessage.error('获取回复列表失败')
    // })
}



const collapseReplies = (commentId) => {
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) {
        // 恢复到初始状态，只显示前2条回复
        comment.showAllReplies = false
    }
}

const loadMoreComments = () => {
    // 模拟加载更多评论
    ElMessage.info('加载更多评论功能')
}

const getUserName = (userId) => {
  let user = userList.value.find(item => item.id === userId)
  return user?.nickname || ''
}

const previewImage = (imageUrl) => {
    // 图片预览功能
    console.log('预览图片:', imageUrl)
}

// 暴露刷新方法给父组件
const refreshComments = () => {
    if (props.targetId) {
        getCommentFn()
    }
}

// 暴露方法给父组件
defineExpose({
    refreshComments
})

// 初始化
onMounted(() => {
    if (props.targetId) {
        getCommentFn()
    }
    userInfo.value = JSON.parse(localStorage.getItem('userInfo'))
    listUser({pageNum: 1, pageSize: 1000}).then(res => {
        userList.value = res.data
    })
})
</script>

<style lang="scss" scoped>
.comment-container {
    width: 100%;
    display: flex;
    flex-direction: column;
}

.comment-post-section {
    padding: 20px;
    border-bottom: 1px solid #f0f0f0;
    
    .comment-title {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 12px;
    }
    
    .comment-input-area {
        display: flex;
        gap: 12px;
        align-items: flex-start;
        
        .input-wrapper {
            flex: 1;
            position: relative;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            background: #fafafa;
            .send-btn {
                position: absolute;
                right: 7px;
                bottom: 7px;
                width: 40px;
                height: 20px;
            }
            // .add-btn {
            //     position: absolute;
            //     left: 12px;
            //     top: 12px;
            //     width: 24px;
            //     height: 24px;
            //     background: #f0f0f0;
            //     border-radius: 4px;
            //     display: flex;
            //     align-items: center;
            //     justify-content: center;
            //     cursor: pointer;
            //     color: #666;
                
            //     &:hover {
            //         background: #e0e0e0;
            //     }
            // }
            
            .comment-textarea {
                width: 100%;
                min-height: 80px;
                padding: 12px 12px 12px 44px;
                border: none;
                outline: none;
                resize: vertical;
                background: transparent;
                font-size: 14px;
                line-height: 1.5;
                
                &::placeholder {
                    color: #999;
                }
            }
        }
        
        .post-btn {
            padding: 8px 20px;
            height: 40px;
            border-radius: 6px;
        }
    }
}

.comment-header {
    padding: 10px;
    font-size: 16px;
    color: #333;
    // border-bottom: 1px solid #f0f0f0;
}

.comment-list {
    flex: 1;
    overflow-y: auto;
    padding: 0 10px;
}

.comment-item {
    border-bottom: 1px solid #f8f8f8;
    padding: 10px 0;
}

.comment-main {
    display: flex;
    gap: 12px;
}

.comment-avatar {
    flex-shrink: 0;
    
    .avatar-circle {
        width: 28px;
        height: 28px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-weight: 600;
        font-size: 14px;
    }
}

.comment-content {
    flex: 1;
    min-width: 0;
}

.comment-user {
    font-size: 14px;
    color: #B4B4B4;
    margin-bottom: 4px;
}

.comment-text {
    font-size: 14px;
    line-height: 1.6;
    color: #333;
    margin-bottom: 8px;
    word-break: break-word;
    
    .expand-link {
        color: #409EFF;
        cursor: pointer;
        
        // &:hover {
        //     text-decoration: underline;
        // }
    }
}

.comment-images {
    display: flex;
    gap: 8px;
    margin-bottom: 8px;
    
    .comment-image {
        width: 60px;
        height: 60px;
        object-fit: cover;
        border-radius: 4px;
        cursor: pointer;
        border: 1px solid #e0e0e0;
        
        &:hover {
            opacity: 0.8;
        }
    }
}

.comment-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    
    .comment-time {
        font-size: 12px;
        color: #999;
    }
    
    .like-icon {
        transform: rotate(180deg);
    }
}

.reply-input {
    margin-top: 12px;
    // padding: 12px;
    // background: #f8f8f8;
    // border-radius: 6px;
    
    .input-wrapper {
        margin-bottom: 8px;
        position: relative;
        .send-btn {
            position: absolute;
            right: 7px;
            bottom: 7px;
            width: 40px;
            height: 20px;
        }
        .reply-textarea {
            width: 100%;
            min-height: 60px;
            padding: 5px;
            border: 1px solid #e0e0e0;
            border-radius: 4px;
            outline: none;
            resize: vertical;
            font-size: 14px;
            line-height: 1.5;
            
            &::placeholder {
                color: #999;
            }
        }
        
        // .add-btn {
        //     position: absolute;
        //     left: 8px;
        //     top: 8px;
        //     width: 20px;
        //     height: 20px;
        //     background: #f0f0f0;
        //     border-radius: 3px;
        //     display: flex;
        //     align-items: center;
        //     justify-content: center;
        //     cursor: pointer;
        //     color: #666;
        //     font-size: 12px;
        // }
    }
}

.reply-list {
    margin-top: 12px;
    padding-left: 16px;
    border-left: 2px solid #f0f0f0;
}

.reply-item {
    display: flex;
    gap: 8px;
    margin-bottom: 12px;
    
    &:last-child {
        margin-bottom: 0;
    }
}

.reply-avatar {
    flex-shrink: 0;
    
    .avatar-circle {
        width: 20px;
        height: 20px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-weight: 600;
        font-size: 10px;
    }
}

.reply-content {
    flex: 1;
    min-width: 0;
}

.reply-user {
    font-size: 14px;
    font-weight: 600;
    color: #B4B4B4;
    margin-bottom: 2px;
    display: flex;
    align-items: center;
    .reply-to {
        color: #999;
        font-weight: normal;
        margin-left: 4px;
        display: flex;
        align-items: center;
    }
}

.reply-text {
    font-size: 14px;
    line-height: 1.5;
    color: #333;
    margin-bottom: 4px;
}

.reply-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .reply-time {
        font-size: 12px;
        color: #999;
    }
    
    .el-button {
        padding: 2px;
        color: #666;
        font-size: 14px;
        
        &:hover {
            color: #409EFF;
        }
    }
}

.expand-replies {
    margin-top: 8px;
    font-size: 12px;
    
    .expand-link, .collapse-link {
        // color: #409EFF;
        // cursor: pointer;
        font-size: 12px;
        margin-right: 12px;
        // &:hover {
        //     text-decoration: underline;
        // }
    }
}

.expand-replies-link {
    margin-top: 8px;
    font-size: 12px;
    display: flex;
    align-items: center;
    .expand-link {
        // color: #409EFF;
        // cursor: pointer;
        font-size: 12px;
        // &:hover {
        //     text-decoration: underline;
        // }
    }
}

.load-more {
    padding: 16px;
    text-align: center;
    border-top: 1px solid #f0f0f0;
    
    .el-button {
        color: #409EFF;
    }
}

// 响应式设计
@media (max-width: 768px) {
    .comment-post-section,
    .comment-header,
    .comment-item {
        padding-left: 12px;
        padding-right: 12px;
    }
    
    .comment-input-area {
        flex-direction: column;
        
        .post-btn {
            align-self: flex-end;
        }
    }
    
    .comment-images {
        .comment-image {
            width: 50px;
            height: 50px;
        }
    }
}
</style>