package com.wudao.kms.llm.llmmode.controller;

import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.llm.llmmode.domain.LLMModel;
import com.wudao.kms.llm.llmmode.service.LLMModelService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/llm/model")
@RequiredArgsConstructor
public class LLMModelController {
    private final LLMModelService llmModelService;

    @GetMapping("/queryList")
    public PageDomain<LLMModel> queryList(@ParameterObject LLMModel model,
                                          @ParameterObject PageDomain<LLMModel> page){
        return llmModelService.queryList(model,page);
    }

    @PostMapping
    public R<Void> add(@RequestBody LLMModel model){
        llmModelService.save(model);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody LLMModel model){
        if (model.getId() == null){
            return R.fail("更新失败");
        }
        llmModelService.updateById(model);
        return R.ok();
    }

    @DeleteMapping
    public R<Void> delete(@RequestParam Long id){
        llmModelService.removeById(id);
        return R.ok();
    }

}
