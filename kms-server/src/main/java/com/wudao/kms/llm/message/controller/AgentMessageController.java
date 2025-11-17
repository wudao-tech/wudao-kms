package com.wudao.kms.llm.message.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.llm.message.domain.AgentMessage;
import com.wudao.kms.llm.message.service.AgentMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/agent/message")
@RequiredArgsConstructor
@RestController
public class AgentMessageController {
    private final AgentMessageService agentMessageService;

    @GetMapping("/queryList")
    public PageDomain<AgentMessage> queryList(@ParameterObject String sessionUuid,
                                              @ParameterObject PageDomain<AgentMessage> page) {
        LambdaQueryWrapper<AgentMessage> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AgentMessage::getSessionUuid, sessionUuid);
        IPage<AgentMessage> pageResult = agentMessageService.page(new Page<>(page.getPageNum(), page.getPageSize()), queryWrapper);
        page.setRecords(pageResult.getRecords());
        page.setTotal(pageResult.getTotal());
        return page;
    }

    @Tag(name = "更新")
    @PutMapping
    public R<Void> update(@RequestBody AgentMessage agentMessage){
        LambdaQueryWrapper<AgentMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentMessage::getChatUuid, agentMessage.getChatUuid());
        AgentMessage serviceOne = agentMessageService.getOne(queryWrapper);
        agentMessage.setId(serviceOne.getId());
        this.agentMessageService.updateById(agentMessage);
        return R.ok();
    }
}
