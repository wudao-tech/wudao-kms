package com.wudao.kms.llm.provider.mapper;


import com.github.yulichang.base.MPJBaseMapper;
import com.wudao.kms.llm.provider.domain.ModelProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 模型厂商Mapper接口
 *
 * @author wudao
 */
@Mapper
public interface ModelProviderMapper extends MPJBaseMapper<ModelProvider> {

    /**
     * 根据厂商code获取厂商配置
     *
     * @param providerCode 厂商code
     * @return 厂商配置
     */
    @Select("SELECT * FROM model_provider WHERE provider_code = #{providerCode} LIMIT 1")
    ModelProvider getByProviderCode(String providerCode);
}
