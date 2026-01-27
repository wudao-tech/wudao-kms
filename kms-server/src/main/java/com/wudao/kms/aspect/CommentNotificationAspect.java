package com.wudao.kms.aspect;

import com.wudao.comment.model.entity.SysComment;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.mapper.KnowledgeFileMapper;
import com.wudao.kms.notification.domain.Notification;
import com.wudao.kms.notification.service.NotificationService;
import com.wudao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 评论通知切面
 * 在评论添加成功后自动发送通知给被评论内容的创建者
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommentNotificationAspect {

    private final NotificationService notificationService;
    private final KnowledgeFileMapper knowledgeFileMapper;

    /**
     * 定义切点：拦截 SysCommentService.insert 方法
     */
    @Pointcut("execution(* com.wudao.comment.service.SysCommentService.insert(..)) && args(record)")
    public void commentInsert(SysComment record) {
    }

    /**
     * 后置通知：在评论添加成功后发送通知
     *
     * @param record     评论记录
     * @param returnValue 方法返回值（影响行数）
     */
    @AfterReturning(pointcut = "commentInsert(record)", returning = "returnValue")
    public void sendNotificationAfterComment(SysComment record, Object returnValue) {
        try {
            // 只有在评论添加成功时才发送通知
            if (returnValue instanceof Integer && (Integer) returnValue > 0) {
                log.info("评论添加成功，准备发送通知: targetType={}, targetId={}", record.getTargetType(), record.getTargetId());

                // 根据 targetType 获取被评论内容的创建者
                Long receiverId = getContentCreator(record.getTargetType(), record.getTargetId());

                if (receiverId == null) {
                    log.warn("无法获取被评论内容的创建者: targetType={}, targetId={}", record.getTargetType(), record.getTargetId());
                    return;
                }

                // 不给自己发通知
                Long currentUserId = SecurityUtils.getUserId();
                if (receiverId.equals(currentUserId)) {
                    log.debug("评论者和内容创建者是同一人，跳过通知");
                    return;
                }

                // 获取内容标题
                String contentTitle = getContentTitle(record.getTargetType(), record.getTargetId());

                // 创建通知
                Notification notification = Notification.builder()
                        .behavior("COMMENT")
                        .type("USER")
                        .title(String.format("您的文章【%s】收到了新评论", contentTitle))
                        .content(record.getContent())
                        .senderId(currentUserId)
                        .receiverId(receiverId)
                        .createTime(LocalDateTime.now())
                        .targetId(record.getTargetId())
                        .jumpType("DOCUMENT")
                        .build();

                notificationService.save(notification);
                log.info("评论通知发送成功: receiverId={}, targetId={}", receiverId, record.getTargetId());
            }
        } catch (Exception e) {
            log.error("发送评论通知失败: targetType={}, targetId={}", record.getTargetType(), record.getTargetId(), e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 根据目标类型和ID获取内容创建者
     *
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @return 创建者用户ID
     */
    private Long getContentCreator(Integer targetType, String targetId) {
        // 根据配置，需要判断 targetType 对应的实际内容类型
        // 假设 knowledge 类型的评论对应知识文件
        // 可以通过查看 application.yml 中的 comment.target 配置来确定

        try {
            // 尝试作为知识文件处理
            KnowledgeFile knowledgeFile = knowledgeFileMapper.selectById(targetId);
            if (knowledgeFile != null) {
                return knowledgeFile.getCreatedBy();
            }
        } catch (Exception e) {
            log.warn("获取知识文件创建者失败: targetId={}", targetId, e);
        }

        // TODO: 根据实际业务添加其他类型的处理
        // 例如：assistant、article 等

        return null;
    }

    /**
     * 根据目标类型和ID获取内容标题
     *
     * @param targetType 目标类型
     * @param targetId   目标ID
     * @return 内容标题
     */
    private String getContentTitle(Integer targetType, String targetId) {
        try {
            // 尝试作为知识文件处理
            KnowledgeFile knowledgeFile = knowledgeFileMapper.selectById(targetId);
            if (knowledgeFile != null) {
                return knowledgeFile.getFileName();
            }
        } catch (Exception e) {
            log.warn("获取内容标题失败: targetId={}", targetId, e);
        }

        return "内容";
    }
}
