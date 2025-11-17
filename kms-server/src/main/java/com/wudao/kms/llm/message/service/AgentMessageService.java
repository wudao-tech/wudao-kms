package com.wudao.kms.llm.message.service;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.wudao.kms.llm.message.domain.AgentMessage;
import com.wudao.kms.llm.message.mapper.AgentMessageMapper;
import org.springframework.stereotype.Service;

@Service
public class AgentMessageService extends MPJBaseServiceImpl<AgentMessageMapper, AgentMessage> {
}
