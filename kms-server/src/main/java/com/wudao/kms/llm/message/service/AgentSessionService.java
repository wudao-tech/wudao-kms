package com.wudao.kms.llm.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.utils.StringUtils;
import com.wudao.kms.llm.message.domain.AgentSession;
import com.wudao.kms.llm.message.mapper.AgentSessionMapper;
import com.wudao.security.utils.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class AgentSessionService extends MPJBaseServiceImpl<AgentSessionMapper, AgentSession> {
    public PageDomain<AgentSession> queryList(String sessionName, String agentUuid, PageDomain<AgentSession> page) {
        MPJLambdaWrapper<AgentSession> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.eq(SecurityUtils.getUserId() != 1L, AgentSession::getCreatedBy, SecurityUtils.getUserId());
        queryWrapper.like(StringUtils.isNotEmpty(sessionName), AgentSession::getSessionName, sessionName);
        queryWrapper.eq(AgentSession::getAgentUuid, agentUuid);
        queryWrapper.orderByDesc(AgentSession::getId);
        IPage<AgentSession> pageResult = this.page(new Page<>(page.getPageNum(), page.getPageSize()), queryWrapper);
        page.setRecords(pageResult.getRecords());
        page.setTotal(pageResult.getTotal());
        return page;
    }

    public AgentSession getByUuid(String uuid) {
        LambdaQueryWrapper<AgentSession> wrapper = Wrappers.lambdaQuery(AgentSession.class).eq(AgentSession::getUuid, uuid);
        return this.getOne(wrapper);
    }
}
