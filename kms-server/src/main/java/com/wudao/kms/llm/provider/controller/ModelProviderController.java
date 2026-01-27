package com.wudao.kms.llm.provider.controller;

import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.llm.provider.domain.ModelProvider;
import com.wudao.kms.llm.provider.dto.ModelProviderListResp;
import com.wudao.kms.llm.provider.service.ModelProviderService;
import com.wudao.kms.llm.token.dto.TokenQuotaResp;
import com.wudao.kms.llm.token.service.TokenQuotaService;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model/provider")
@RequiredArgsConstructor
@Tag(name = "模型供应商管理")
public class ModelProviderController {

    private final ModelProviderService modelProviderService;
    private final TokenQuotaService tokenQuotaService;

    @Tag(name = "查询列表", description = "列表需要把模型的数量带回来")
    @GetMapping("/queryList")
    public PageDomain<ModelProviderListResp> queryList(@ParameterObject ModelProvider model,
                                                       @ParameterObject PageDomain<ModelProvider> page) {
        return modelProviderService.queryList(model, page);
    }

    @Tag(name = "新增")
    @PostMapping
    public R<Void> add(@RequestBody ModelProvider model) {
        model.setCreatedBy(SecurityUtils.getUserId());
        modelProviderService.save(model);
        return R.ok();
    }

    @Tag(name = "删除")
    @DeleteMapping
    public R<Void> delete(@RequestParam Long id) {
        modelProviderService.removeById(id);
        return R.ok();
    }

    @Tag(name = "更新")
    @PutMapping
    public R<Void> update(@RequestBody ModelProvider model) {
        if (model.getId() == null) {
            return R.fail("更新失败：ID不能为空");
        }
        model.setUpdatedBy(SecurityUtils.getUserId());
        modelProviderService.updateById(model);
        return R.ok();
    }

    @Tag(name = "刷新状态", description = "链接llmModel表通过provider_code找到随便一个模型，使用chat如果能连接通就刷新状态链接成功")
    @PostMapping("/refresh")
    public R<ModelProvider> refresh(@RequestBody ModelProvider modelProvider) {
        ModelProvider provider = modelProviderService.refresh(modelProvider);
        if (provider == null) {
            return R.fail();
        }
        if (!"连接成功".equals(provider.getStatus())) {
            return R.fail("连接失败");
        }
        return R.ok(provider);
    }

    @Operation(summary = "查询Token配额", description = "查询当前用户的Token配额使用情况")
    @GetMapping("/token/quota")
    public R<TokenQuotaResp> getTokenQuota() {
        return R.ok(tokenQuotaService.getQuotaInfo(SecurityUtils.getUserId()));
    }
}
