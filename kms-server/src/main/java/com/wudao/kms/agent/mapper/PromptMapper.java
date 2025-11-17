package com.wudao.kms.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wudao.kms.agent.domain.Prompt;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提示词数据访问层
 */
@Mapper
public interface PromptMapper extends BaseMapper<Prompt> {
}