package com.wudao.kms.controller;

import com.wudao.common.model.vo.R;
import com.wudao.kms.dto.ChatFileUploadDTO;
import com.wudao.kms.dto.ChatRequestDTO;
import com.wudao.kms.service.ChatService;
import com.wudao.kms.service.ChatSessionQaService;
import com.wudao.kms.service.ChatSessionService;
import com.wudao.kms.vo.ChatSessionListVO;
import com.wudao.kms.vo.ChatSessionQaVO;
import com.wudao.kms.vo.ChatFileUploadVO;
import com.wudao.kms.entity.chat.ChatFileUpload;
import com.wudao.kms.service.ChatFileUploadService;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "知识专家",description = "知识专家相关接口")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatSessionService chatSessionService;
    private final ChatSessionQaService chatSessionQaService;
    private final ChatService chatService;
    private final ChatFileUploadService chatFileUploadService;

    @Operation(summary = "创建新会话")
    @PostMapping("/newSession")
    public R<Long> newChat(){
        Long sessionId = chatSessionService.createNewSession();
        return R.ok(sessionId);
    }

    @Operation(summary = "获取会话列表，区分今天和七日内")
    @GetMapping("/getSessionList")
    public R<ChatSessionListVO> getSessionList(){
        ChatSessionListVO sessionList = chatSessionService.getSessionList();
        return R.ok(sessionList);
    }

    @Operation(summary = "上传文件（图片和文件）",description = "上传文件交给问答使用")
    @PostMapping("/uploadFile")
    public R<Long> uploadFile(ChatFileUploadDTO uploadDTO){
        Long fileUploadId = chatService.uploadFile(uploadDTO);
        return R.ok(fileUploadId);
    }

    @Operation(summary = "删除会话")
    @PostMapping("/deleteSession")
    public R<Boolean> deleteSession(@Parameter(description = "会话ID") @RequestParam Long sessionId){
        Boolean result = chatSessionService.deleteSession(sessionId);
        return R.ok(result);
    }

    @Operation(summary = "编辑会话名称")
    @PostMapping("/editSessionName")
    public R<Boolean> editSessionName(@Parameter(description = "会话ID") @RequestParam Long sessionId,
                                     @Parameter(description = "会话名称") @RequestParam String sessionName){
        Boolean result = chatSessionService.editSessionName(sessionId, sessionName);
        return R.ok(result);
    }

    @Operation(summary = "获取会话内容，从远到近")
    @GetMapping("/getSessionContent")
    public R<List<ChatSessionQaVO>> getSessionContent(@Parameter(description = "会话ID") @RequestParam Long sessionId){
        List<ChatSessionQaVO> sessionContent = chatSessionQaService.getSessionContent(sessionId, SecurityUtils.getUserId());
        return R.ok(sessionContent);
    }

    @Operation(summary = "删除会话内容")
    @PostMapping("/deleteSessionContent")
    public R<Boolean> deleteSessionContent(@Parameter(description = "问答ID") @RequestParam Long qaId){
        Boolean result = chatSessionQaService.deleteSessionContent(qaId, SecurityUtils.getUserId());
        return R.ok(result);
    }

    @Operation(summary = "清空会话内容")
    @PostMapping("/clearSessionContent")
    public R<Boolean> clearSessionContent(@Parameter(description = "会话ID") @RequestParam Long sessionId){
        Boolean result = chatSessionQaService.clearSessionContent(sessionId, SecurityUtils.getUserId());
        return R.ok(result);
    }

    @Operation(summary = "上传文件记录",description = "根据问答内容找到fileLists，然后去重返回结果")
    @PostMapping("/uploadFileRecord")
    public R<List<String>> uploadFileRecord(@Parameter(description = "回答内容") @RequestParam String sessionId){
        List<String> fileRecord = chatService.getUploadFileRecord(sessionId);
        return R.ok(fileRecord);
    }

    @Operation(summary = "开始聊天",description = "使用flux流式返回结果还有参数联网flag、深度思考flag、文件列表")
    @PostMapping("/startChat")
    public SseEmitter startChat(@RequestBody ChatRequestDTO requestDTO){
        return chatService.startChat(requestDTO);
    }

    @Operation(summary = "推荐知识库", description = "把聊天经常返回的知识库记录下来，用于推荐")
    @PostMapping("/recommendKnowledgeBase")
    public R<List<String>> recommendKnowledgeBase(){
        // TODO: 实现推荐逻辑
        List<String> recommendations = List.of("推荐知识库1", "推荐知识库2");
        return R.ok(recommendations);
    }

    @Operation(summary = "获取会话文件列表", description = "获取指定会话的所有上传文件")
    @GetMapping("/getSessionFiles")
    public R<List<ChatFileUploadVO>> getSessionFiles(@Parameter(description = "会话ID") @RequestParam Long sessionId){
        List<ChatFileUpload> fileUploads = chatFileUploadService.getUploadFilesBySessionId(sessionId);
        List<ChatFileUploadVO> fileUploadVOs = fileUploads.stream()
                .map(this::convertToFileUploadVO)
                .collect(Collectors.toList());
        return R.ok(fileUploadVOs);
    }

    @Operation(summary = "删除上传文件", description = "删除指定的文件上传记录")
    @PostMapping("/deleteUploadFile")
    public R<Boolean> deleteUploadFile(@Parameter(description = "文件上传记录ID") @RequestParam Long fileUploadId){
        Boolean result = chatFileUploadService.deleteUploadFile(fileUploadId);
        return R.ok(result);
    }

    /**
     * 转换为文件上传VO
     */
    private ChatFileUploadVO convertToFileUploadVO(ChatFileUpload fileUpload) {
        ChatFileUploadVO vo = new ChatFileUploadVO();
        vo.setId(fileUpload.getId());
        vo.setSessionId(fileUpload.getSessionId());
        vo.setOriginalFileName(fileUpload.getOriginalFileName());
        vo.setFileUrl(fileUpload.getFileUrl());
        vo.setFileSize(fileUpload.getFileSize());
        vo.setFileType(fileUpload.getFileType());
        vo.setFileCategory(fileUpload.getFileCategory());
        vo.setCreatedAt(fileUpload.getCreatedAt());
        return vo;
    }
}
