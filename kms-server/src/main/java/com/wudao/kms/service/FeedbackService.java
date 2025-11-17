package com.wudao.kms.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.common.utils.DateFormats;
import com.wudao.common.utils.ServletUtils;
import com.wudao.common.utils.StringUtils;
import com.wudao.common.utils.edoc.CsvUtils;
import com.wudao.common.utils.edoc.Getters;
import com.wudao.kms.agent.domain.Assistant;
import com.wudao.kms.entity.Feedback;
import com.wudao.kms.mapper.FeedbackMapper;
import com.wudao.security.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService extends MPJBaseServiceImpl<FeedbackMapper, Feedback> {

    public PageDomain<Feedback> queryList(Feedback feedback,
                                          PageDomain<Feedback> page) {
        MPJLambdaWrapper<Feedback> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Feedback.class);
        wrapper.selectAs(Assistant::getName, Feedback::getAgentName);
        wrapper.selectAs(SysUser::getNickname, Feedback::getCreatedByName);
        wrapper.eq(StringUtils.isNotEmpty(feedback.getType()), Feedback::getType, feedback.getType());
        wrapper.ge(feedback.getStartTime() != null, Feedback::getCreateTime, feedback.getStartTime());
        wrapper.le(feedback.getStartTime() != null, Feedback::getCreateTime, feedback.getEndTime());
        wrapper.eq(SecurityUtils.getUserId() != 1L, Feedback::getCreatedBy, SecurityUtils.getUserId());
        wrapper.like(StringUtils.isNotEmpty(feedback.getUserMessage()), Feedback::getUserMessage, feedback.getUserMessage());
        wrapper.eq(feedback.getCreatedBy() != null, Feedback::getCreatedBy, feedback.getCreatedBy());
        wrapper.eq(StringUtils.isNotEmpty(feedback.getType()), Feedback::getType, feedback.getType());
        wrapper.eq(StringUtils.isNotEmpty(feedback.getAgentUuid()), Feedback::getAgentUuid, feedback.getAgentUuid());
        wrapper.eq(StringUtils.isNotEmpty(feedback.getAdoptionStatus()), Feedback::getAdoptionStatus, feedback.getAdoptionStatus());
        wrapper.orderByDesc("create_time");
        wrapper.leftJoin(SysUser.class, SysUser::getId, Feedback::getCreatedBy);
        wrapper.leftJoin(Assistant.class,  Assistant::getUuid, Feedback::getAgentUuid);
        IPage<Feedback> pageResult = this.page(new Page<>(page.getPageNum(), page.getPageSize()), wrapper);
        page.setRecords(pageResult.getRecords());
        page.setTotal(pageResult.getTotal());
        return page;
    }

    public R<Void> update(Feedback feedback) {
        if (feedback.getId() == null) {
            return R.fail("请传入ID");
        }
        feedback.setOptimizationFlag(true);
        this.updateById(feedback);
        return R.ok();
    }

    public R<Void> delete(List<Long> ids) {
        this.removeBatchByIds(ids);
        return R.ok();
    }

    public void exportData(Feedback feedback, HttpServletResponse response) throws Exception {
        MPJLambdaWrapper<Feedback> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Feedback.class);
        wrapper.selectAs(SysUser::getNickname, Feedback::getCreatedByName);
        if (StringUtils.isNotEmpty(feedback.getType())) {
            wrapper.eq(Feedback::getType, feedback.getType());
        }
        if (feedback.getStartTime() != null) {
            wrapper.ge(Feedback::getCreateTime, feedback.getStartTime());
            wrapper.le(Feedback::getCreateTime, feedback.getEndTime());
        }
        if (SecurityUtils.getUserId() != 1L) {
            wrapper.eq(Feedback::getCreatedBy, SecurityUtils.getUserId());
        }
        wrapper.in(CollUtil.isNotEmpty(feedback.getIds()), Feedback::getId, feedback.getIds());
        wrapper.orderByDesc("create_time");
        wrapper.leftJoin(SysUser.class, SysUser::getId, Feedback::getCreatedBy);
        List<Feedback> feedbacks = this.list(wrapper);
        String fileName = "反馈数据_" + LocalDateTime.now().format(DateFormats.yyyyMMdd_HHmmss) + ".csv";
        Getters<Feedback> getters = new Getters();
        getters.addText("序号", Feedback::getId);
        getters.addText("问题", Feedback::getUserMessage);
        getters.addText("问答", Feedback::getAgent);
        getters.addText("提问人", Feedback::getCreatedByName);
        getters.addText("反馈", (t) -> t.getType().equals("agree") ? "点赞" : "点踩");
        DateTimeFormatter dateTimeFormatter = DateFormats.STANDARD;
        getters.addText("提问时间", Feedback::getCreateTime, dateTimeFormatter::format);
        ServletUtils.setFileName(response, fileName);
        CsvUtils.write(feedbacks, getters, response.getOutputStream());
    }
}
