package com.wudao.kms.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wudao.kms.agent.domain.Prompt;
import com.wudao.kms.agent.dto.*;
import com.wudao.kms.agent.mapper.PromptMapper;
import com.wudao.kms.agent.service.PromptService;
import com.wudao.common.exception.ServiceException;
import com.wudao.common.utils.StringUtils;
import com.wudao.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提示词服务实现类
 */
@Slf4j
@Service
public class PromptServiceImpl extends ServiceImpl<PromptMapper, Prompt> implements PromptService {

    @Override
    public Prompt savePrompt(PromptCreateRequest request) {
        // 检查名称是否重复
        QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", request.getName())
                    .eq("is_delete", 0);
        if (this.count(queryWrapper) > 0) {
            throw new ServiceException("提示词名称已存在");
        }
        
        Prompt prompt = new Prompt();
        BeanUtils.copyProperties(request, prompt);
        prompt.setUserId(SecurityUtils.getUserId());
        prompt.setCreatedAt(LocalDateTime.now());
        prompt.setCreatedBy(SecurityUtils.getUserId());
        prompt.setStatus(1); // 默认启用
        prompt.setIsDelete(0); // 默认未删除
        
        this.save(prompt);
        return prompt;
    }

    @Override
    public Prompt updatePrompt(PromptUpdateRequest request) {
        Prompt existingPrompt = this.getById(request.getId());
        if (existingPrompt == null || existingPrompt.getIsDelete() == 1) {
            throw new ServiceException("提示词不存在");
        }
        
        // 检查权限
        Long currentUserId = SecurityUtils.getUserId();
        if (!existingPrompt.getUserId().equals(currentUserId) && currentUserId != 1L) {
            throw new ServiceException("没有权限修改此提示词");
        }
        
        // 检查名称是否重复（排除自己）
        if (StringUtils.isNotEmpty(request.getName()) && !request.getName().equals(existingPrompt.getName())) {
            QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", request.getName())
                        .eq("is_delete", 0)
                        .ne("id", request.getId());
            if (this.count(queryWrapper) > 0) {
                throw new ServiceException("提示词名称已存在");
            }
        }
        
        BeanUtils.copyProperties(request, existingPrompt, "id", "userId", "createTime", "createBy");
        existingPrompt.setUpdatedAt(LocalDateTime.now());
        existingPrompt.setUpdatedBy(SecurityUtils.getUserId());
        
        this.updateById(existingPrompt);
        return existingPrompt;
    }

    @Override
    public IPage<Prompt> getPromptList(PromptListRequest request) {
        Page<Prompt> page = new Page<>(request.getPageNum(), request.getPageSize());
        QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
        
        // 基础查询条件
        queryWrapper.eq("is_delete", 0);
        
        // 搜索条件
        if (StringUtils.isNotEmpty(request.getName())) {
            queryWrapper.like("name", request.getName());
        }
        
        if (StringUtils.isNotEmpty(request.getType())) {
            queryWrapper.eq("type", request.getType());
        }
        
        if (request.getStatus() != null) {
            queryWrapper.eq("status", request.getStatus());
        }
        
        if (request.getIsPublic() != null) {
            queryWrapper.eq("is_public", request.getIsPublic());
        }
        
        // 排序
        queryWrapper.orderByDesc("create_time");
        
        return this.page(page, queryWrapper);
    }

    @Override
    public Prompt getPromptById(Long id) {
        QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).eq("is_delete", 0);
        return this.getOne(queryWrapper);
    }

    @Override
    public Prompt getPromptByName(String name) {
        QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name).eq("is_delete", 0);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean deletePrompt(Long id) {
        Prompt prompt = this.getById(id);
        if (prompt == null || prompt.getIsDelete() == 1) {
            throw new ServiceException("提示词不存在");
        }
        
        // 检查权限
        Long currentUserId = SecurityUtils.getUserId();
        if (!prompt.getUserId().equals(currentUserId) && currentUserId != 1L) {
            throw new ServiceException("没有权限删除此提示词");
        }
        
        // 逻辑删除
        prompt.setIsDelete(1);
        prompt.setUpdatedAt(LocalDateTime.now());
        prompt.setUpdatedBy(SecurityUtils.getUserId());
        
        return this.updateById(prompt);
    }

    @Override
    public IPage<Prompt> getPromptListWithPermission(PromptListRequest request, Long userId, boolean isAdmin) {
        Page<Prompt> page = new Page<>(request.getPageNum(), request.getPageSize());
        QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
        
        // 基础查询条件
        queryWrapper.eq("is_delete", 0);
        
        // 权限过滤
        if (!isAdmin) {
            queryWrapper.and(wrapper -> wrapper
                .eq("user_id", userId)  // 自己创建的
                .or()
                .eq("is_public", 1)     // 或者公开的
            );
        }
        
        // 搜索条件
        if (StringUtils.isNotEmpty(request.getName())) {
            queryWrapper.like("name", request.getName());
        }
        
        if (StringUtils.isNotEmpty(request.getType())) {
            queryWrapper.eq("type", request.getType());
        }
        
        if (request.getStatus() != null) {
            queryWrapper.eq("status", request.getStatus());
        }
        
        if (request.getIsPublic() != null) {
            queryWrapper.eq("is_public", request.getIsPublic());
        }
        
        // 排序
        queryWrapper.orderByDesc("create_time");
        
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean batchDeletePrompts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return true;
        }
        
        Long currentUserId = SecurityUtils.getUserId();
        boolean isAdmin = currentUserId ==1L;
        
        // 检查权限
        if (!isAdmin) {
            QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", ids)
                        .eq("is_delete", 0)
                        .ne("user_id", currentUserId);
            if (this.count(queryWrapper) > 0) {
                throw new ServiceException("包含无权限删除的提示词");
            }
        }
        
        // 批量逻辑删除
        QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids).eq("is_delete", 0);
        
        Prompt updatePrompt = new Prompt();
        updatePrompt.setIsDelete(1);
        updatePrompt.setUpdatedAt(LocalDateTime.now());
        updatePrompt.setUpdatedBy(SecurityUtils.getUserId());
        
        return this.update(updatePrompt, queryWrapper);
    }

    @Override
    public Prompt copyPrompt(PromptCopyRequest request) {
        Prompt originalPrompt = this.getById(request.getSourceId());
        if (originalPrompt == null || originalPrompt.getIsDelete() == 1) {
            throw new ServiceException("源提示词不存在");
        }
        
        // 检查权限
        Long currentUserId = SecurityUtils.getUserId();
        if (!originalPrompt.getUserId().equals(currentUserId) && 
            currentUserId != 1L &&
            originalPrompt.getIsPublic() != 1) {
            throw new ServiceException("没有权限复制此提示词");
        }
        
        // 检查新名称是否重复
        QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", request.getNewName())
                    .eq("is_delete", 0);
        if (this.count(queryWrapper) > 0) {
            throw new ServiceException("提示词名称已存在");
        }
        
        Prompt newPrompt = new Prompt();
        BeanUtils.copyProperties(originalPrompt, newPrompt, "id", "name", "createTime", "createBy", "updateTime", "updateBy");
        newPrompt.setName(request.getNewName());
        newPrompt.setUserId(currentUserId);
        newPrompt.setCreatedAt(LocalDateTime.now());
        newPrompt.setCreatedBy(SecurityUtils.getUserId());
        newPrompt.setIsPublic(0); // 复制的提示词默认为私有
        
        this.save(newPrompt);
        return newPrompt;
    }

    @Override
    public IPage<Prompt> getPromptsByTags(String tags, Integer pageNum, Integer pageSize) {
        Page<Prompt> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
        
        queryWrapper.eq("is_delete", 0);
        
        if (StringUtils.isNotEmpty(tags)) {
            String[] tagArray = tags.split(",");
            for (String tag : tagArray) {
                queryWrapper.like("tags", tag.trim());
            }
        }
        
        queryWrapper.orderByDesc("create_time");
        
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean updatePromptStatus(Long id, Integer status) {
        Prompt prompt = this.getById(id);
        if (prompt == null || prompt.getIsDelete() == 1) {
            throw new ServiceException("提示词不存在");
        }
        
        // 检查权限
        Long currentUserId = SecurityUtils.getUserId();
        if (!prompt.getUserId().equals(currentUserId) && currentUserId != 1L) {
            throw new ServiceException("没有权限修改此提示词");
        }
        
        prompt.setStatus(status);
        prompt.setUpdatedAt(LocalDateTime.now());
        prompt.setUpdatedBy(SecurityUtils.getUserId());
        
        return this.updateById(prompt);
    }

    @Override
    public byte[] exportPrompts(PromptExportRequest request) {
        try {
            // 构建查询条件
            QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_delete", 0);
            
            // 如果指定了ID列表，则只导出这些ID的提示词
            if (request.getIds() != null && !request.getIds().isEmpty()) {
                queryWrapper.in("id", request.getIds());
            }
            
            // 按类型过滤
            if (StringUtils.isNotEmpty(request.getType())) {
                queryWrapper.eq("type", request.getType());
            }
            
            // 按是否公开过滤
            if (request.getIsPublic() != null) {
                queryWrapper.eq("is_public", request.getIsPublic());
            }
            
            // 查询提示词列表
            List<Prompt> prompts = this.list(queryWrapper);
            
            // 转换为导出格式
            List<Map<String, Object>> exportData = new ArrayList<>();
            for (Prompt prompt : prompts) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", prompt.getId());
                data.put("name", prompt.getName());
                data.put("content", prompt.getContent());
                data.put("type", prompt.getType());
                data.put("description", prompt.getDescription());
                data.put("tags", prompt.getTags());
                data.put("isPublic", prompt.getIsPublic());
                data.put("status", prompt.getStatus());
                data.put("userId", prompt.getUserId());
                data.put("createTime", prompt.getCreatedAt());
                data.put("createBy", prompt.getCreatedBy());
                data.put("updateTime", prompt.getUpdatedAt());
                data.put("updateBy", prompt.getUpdatedBy());
                exportData.add(data);
            }
            
            // 根据格式导出（目前只支持JSON格式）
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(exportData);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("导出提示词失败", e);
            throw new ServiceException("导出提示词失败: " + e.getMessage());
        }
    }

    @Override
    public int importPrompts(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        
        try {
            // 检查文件类型
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.toLowerCase().endsWith(".json")) {
                throw new ServiceException("只支持JSON格式的文件");
            }
            
            // 读取文件内容
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            
            // 解析JSON
            List<Map<String, Object>> importData;
            try {
                importData = objectMapper.readValue(content, new TypeReference<>() {});
            } catch (Exception e) {
                throw new ServiceException("JSON格式解析失败，请检查文件格式");
            }
            
            if (importData == null || importData.isEmpty()) {
                return 0;
            }
            
            Long currentUserId = SecurityUtils.getUserId();
            LocalDateTime now = LocalDateTime.now();
            int importCount = 0;
            
            // 遍历导入数据
            for (Map<String, Object> data : importData) {
                try {
                    String name = (String) data.get("name");
                    String promptContent = (String) data.get("content");
                    
                    // 验证必填字段
                    if (StringUtils.isEmpty(name) || StringUtils.isEmpty(promptContent)) {
                        log.warn("跳过缺少必填字段的提示词: {}", data);
                        continue;
                    }
                    
                    // 检查名称是否已存在
                    QueryWrapper<Prompt> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("name", name).eq("is_delete", 0);
                    if (this.count(queryWrapper) > 0) {
                        // 如果名称已存在，添加后缀
                        name = name + "_imported_" + System.currentTimeMillis();
                    }
                    
                    // 创建新的提示词
                    Prompt prompt = new Prompt();
                    prompt.setName(name);
                    prompt.setContent(promptContent);
                    prompt.setType((String) data.getOrDefault("type", "personal"));
                    prompt.setDescription((String) data.get("description"));
                    prompt.setTags((String) data.get("tags"));
                    prompt.setIsPublic(0); // 导入的提示词默认为私有
                    prompt.setStatus(1); // 默认启用
                    prompt.setUserId(currentUserId);
                    prompt.setCreatedAt(now);
                    prompt.setCreatedBy(SecurityUtils.getUserId());
                    prompt.setIsDelete(0);
                    
                    // 保存到数据库
                    if (this.save(prompt)) {
                        importCount++;
                    }
                    
                } catch (Exception e) {
                    log.error("导入提示词失败: {}", data, e);
                    // 继续处理下一条记录
                }
            }
            
            return importCount;
            
        } catch (IOException e) {
            log.error("读取导入文件失败", e);
            throw new ServiceException("读取导入文件失败: " + e.getMessage());
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入提示词失败", e);
            throw new ServiceException("导入提示词失败: " + e.getMessage());
        }
    }
}