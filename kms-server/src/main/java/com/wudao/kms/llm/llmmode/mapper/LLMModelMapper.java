package com.wudao.kms.llm.llmmode.mapper;


import com.github.yulichang.base.MPJBaseMapper;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 大模型Mapper接口
 *
 * @author wudao
 */
@Mapper
public interface LLMModelMapper extends MPJBaseMapper<LLMModel> {
    @Select("SELECT * FROM llm_model WHERE model = #{model} limit 1")
    LLMModel queryByModel(String model);
}