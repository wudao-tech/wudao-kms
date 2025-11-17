package com.wudao.kms.agent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wudao.kms.agent.domain.Prompt;
import com.wudao.kms.agent.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 提示词服务接口
 */
public interface PromptService {
    
    /**
     * 保存提示词模板
     * @param request 创建请求
     * @return 创建的提示词
     */
    Prompt savePrompt(PromptCreateRequest request);
    
    /**
     * 更新提示词模板
     * @param request 更新请求
     * @return 更新后的提示词
     */
    Prompt updatePrompt(PromptUpdateRequest request);
    
    /**
     * 根据名称搜索提示词，支持分页
     * @param request 查询请求
     * @return 分页结果
     */
    IPage<Prompt> getPromptList(PromptListRequest request);
    
    /**
     * 根据ID获取提示词
     * @param id 提示词ID
     * @return 提示词信息
     */
    Prompt getPromptById(Long id);
    
    /**
     * 根据名称获取提示词
     * @param name 提示词名称
     * @return 提示词信息
     */
    Prompt getPromptByName(String name);
    
    /**
     * 删除提示词
     * @param id 提示词ID
     * @return 是否删除成功
     */
    boolean deletePrompt(Long id);
    
    /**
     * 根据用户权限分页查询提示词列表
     * @param request 查询请求
     * @param userId 用户ID
     * @param isAdmin 是否管理员
     * @return 分页结果
     */
    IPage<Prompt> getPromptListWithPermission(PromptListRequest request, Long userId, boolean isAdmin);
    
    /**
     * 批量删除提示词
     * @param ids 提示词ID列表
     * @return 是否删除成功
     */
    boolean batchDeletePrompts(List<Long> ids);
    
    /**
     * 复制提示词
     * @param request 复制请求
     * @return 复制后的提示词
     */
    Prompt copyPrompt(PromptCopyRequest request);
    
    /**
     * 按标签查询提示词
     * @param tags 标签列表（逗号分隔）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    IPage<Prompt> getPromptsByTags(String tags, Integer pageNum, Integer pageSize);
    
    /**
     * 更新提示词状态
     * @param id 提示词ID
     * @param status 状态值
     * @return 是否更新成功
     */
    boolean updatePromptStatus(Long id, Integer status);
    
    /**
     * 导出提示词
     * @param request 导出请求
     * @return 导出的文件字节数组
     */
    byte[] exportPrompts(PromptExportRequest request);
    
    /**
     * 导入提示词
     * @param file 上传的文件
     * @return 导入的提示词数量
     */
    int importPrompts(MultipartFile file);
}