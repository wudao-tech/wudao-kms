package com.wudao.kms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.entity.Feedback;
import com.wudao.kms.llm.message.domain.AgentMessage;
import com.wudao.kms.llm.message.service.AgentMessageService;
import com.wudao.kms.service.FeedbackService;
import com.xkcoding.http.util.StringUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Tag(name = "反馈controller")
public class FeedbackController {

    private final FeedbackService feedbackService;

    private final AgentMessageService agentMessageService;

    @Tag(name = "查询列表")
    @GetMapping("/queryList")
    public PageDomain<Feedback> queryList(@ParameterObject Feedback feedback,
                                          @ParameterObject PageDomain<Feedback> page) {
        return feedbackService.queryList(feedback, page);
    }

    @PutMapping
    @Tag(name = "更新")
    public R<Void> update(@RequestBody Feedback feedback) {
        return  feedbackService.update(feedback);
    }

    @GetMapping("/delete")
    @Tag(name = "删除")
    public R<Void> delete(@RequestParam List<Long> ids) {
        return feedbackService.delete(ids);
    }

    @PostMapping
    public R<Void> save(@RequestBody Feedback feedback) {
        // 判断feedback是否已经存在
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getChatUuid, feedback.getChatUuid());
        feedbackService.remove(wrapper);
        feedback.setCreateTime(LocalDateTime.now());
        feedbackService.save(feedback);
        if (StringUtil.isNotEmpty(feedback.getChatUuid())) {
            LambdaQueryWrapper<AgentMessage> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentMessage::getChatUuid, feedback.getChatUuid());
            AgentMessage agentMessage = agentMessageService.getOne(queryWrapper);
            if (agentMessage != null) {
                agentMessage.setFeedbackType(feedback.getType());
            }
            agentMessageService.updateById(agentMessage);
        }
        return R.ok();
    }

    @PostMapping("export")
    public void exportData(@RequestBody Feedback feedback, HttpServletResponse response) throws Exception {
        feedbackService.exportData(feedback, response);
    }
}
