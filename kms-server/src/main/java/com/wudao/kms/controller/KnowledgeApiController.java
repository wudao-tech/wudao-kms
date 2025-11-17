package com.wudao.kms.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wudao.common.model.vo.R;
import com.wudao.common.utils.StringUtils;
import com.wudao.kms.dto.KnowledgeSearchResult;
import com.wudao.kms.dto.KnowledgeTestDTO;
import com.wudao.kms.entity.KnowledgeBase;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.kms.entity.KnowledgeFileSegment;
import com.wudao.kms.service.KnowledgeBaseService;
import com.wudao.kms.service.KnowledgeFileSegmentService;
import com.wudao.kms.service.KnowledgeFileService;
import com.wudao.kms.service.VectorizationService;

import com.wudao.oss.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Tag(name = "知识库api", description = "提供知识相关的API接口")
@RestController
@RequestMapping("/knowledge/api")
@RequiredArgsConstructor
public class KnowledgeApiController {
    private final VectorizationService vectorizationService;
    private final KnowledgeFileService knowledgeFileService;
    private final OssService ossService;
    @Operation(summary = "根据名称搜索知识库")
    @PostMapping("/search")
    public R<List<KnowledgeSearchResult>> search(@RequestBody KnowledgeTestDTO knowledgeTestDTO) {
        if (StringUtils.isEmpty(knowledgeTestDTO.getSearchType())) {
            knowledgeTestDTO.setSearchType("semantic");
        }
        List<KnowledgeSearchResult> search = vectorizationService.searchPro(knowledgeTestDTO);
        return R.ok(search);
    }

    @PostMapping("/queryFileByChunk")
    public R<List<KnowledgeFile>> queryFileByChunk(@RequestBody JSONObject jsonObject) {
        if (!jsonObject.containsKey("chunkIds")){
            return R.ok();
        }
        List<Long> chunkIds = jsonObject.getJSONArray("chunkIds").toJavaList(Long.class);
        chunkIds = chunkIds.stream().distinct().toList();
        MPJLambdaWrapper<KnowledgeFile> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(KnowledgeFile.class);
        wrapper.leftJoin(KnowledgeFileSegment.class,KnowledgeFileSegment::getFileId, KnowledgeFile::getId);
        wrapper.in(KnowledgeFileSegment::getId, chunkIds);
        List<KnowledgeFile> files = knowledgeFileService.list(wrapper);
        if (CollUtil.isEmpty(files)) {
            return R.ok();
        }
        files = files.stream().distinct().toList();
        files.forEach(item-> item.setPreviewS3Url(ossService.getHttpUrl(item.getOssPath(),1, TimeUnit.DAYS)));
        return R.ok(files);
    }

    private final KnowledgeBaseService knowledgeBaseService;

    @Operation(summary = "根据名称搜索知识库")
    @GetMapping("/knowledge-list")
    public R<List<KnowledgeBase>> search(@Parameter(description = "搜索关键词") @RequestParam(required = false) String name,
                                         @Parameter(description = "创建人", example = "true") @RequestParam(defaultValue = "true") String createdBy) {
        List<KnowledgeBase> result = knowledgeBaseService.searchByName(name, createdBy);
        return R.ok(result);
    }
}
