package com.wudao.kms.controller;

import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.dto.QaImproveAddReq;
import com.wudao.kms.entity.QaImprove;
import com.wudao.kms.service.QaImproveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/qa/improve")
@RequiredArgsConstructor
@Tag(name = "QA优化控制器")
public class QaImproveController {
    private final QaImproveService qaImproveService;

    @PostMapping
    @Operation(description = "添加qa")
    public R<Void> addQa(@RequestBody QaImprove qaImprove) {
        return qaImproveService.addQa(qaImprove);
    }

    @Tag(name = "批量添加Qa")
    @PostMapping("/batch/add")
    public R<Void> batchAddQa(@RequestBody QaImproveAddReq improveAddReq) {
        return qaImproveService.batchAddQa(improveAddReq);
    }

    @GetMapping("/batchPass")
    public R<Void> batchPass(@RequestParam List<Long> qaIds,@RequestParam String status){
        return qaImproveService.batchPass(qaIds, status);
    }

    @PutMapping
    public R<Void> update(@RequestBody QaImprove qaImprove) {
        return qaImproveService.updateQa(qaImprove);
    }

    @Tag(name = "删除")
    @GetMapping("/delete")
    public R<Void> delete(@RequestParam List<Long> id) {
        qaImproveService.removeBatchByIds(id);
        return R.ok();
    }

    @GetMapping("/queryList")
    @Operation(description = "查询列表")
    public PageDomain<QaImprove> queryList(
            @ParameterObject QaImprove qaImprove,
            @ParameterObject PageDomain<QaImprove> page) {
        return qaImproveService.queryList(qaImprove,page);
    }

    @PostMapping("/export")
    public void exportData(QaImprove qaImprove, HttpServletResponse response) throws Exception {
        qaImproveService.exportData(qaImprove, response);
    }

    @PostMapping("/importData")
    public R<Void> importData(@RequestPart("file") MultipartFile file, @RequestParam Long knowledgeId, @RequestParam Long knowledgeSpaceId) throws Exception {
        qaImproveService.importData(file, knowledgeId, knowledgeSpaceId);
        return R.ok();
    }
}
