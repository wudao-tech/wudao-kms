package com.wudao.kms.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.wudao.kms.entity.chat.ChatSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天会话Mapper
 */
@Mapper
public interface ChatSessionMapper extends MPJBaseMapper<ChatSession> {
} 