package com.wudao.kms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.wudao.common.exception.ServiceException;
import com.wudao.common.model.entity.SysUser;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.utils.DateFormats;
import com.wudao.common.utils.ServletUtils;
import com.wudao.common.utils.edoc.CsvUtils;
import com.wudao.common.utils.edoc.ExcelUtils;
import com.wudao.common.utils.edoc.Getters;
import com.wudao.kms.dto.SystemDictionaryImportDTO;
import com.wudao.kms.dto.SystemDictionaryQueryDTO;
import com.wudao.kms.entity.QaImprove;
import com.wudao.kms.entity.SystemDictionary;
import com.wudao.kms.mapper.SystemDictionaryMapper;
import com.wudao.kms.vo.SystemDictionaryVO;
import com.wudao.system.mapper.SysUserMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统词库Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemDictionaryService extends MPJBaseServiceImpl<SystemDictionaryMapper, SystemDictionary> {

    private final SysUserMapper sysUserMapper;

    /**
     * 分页查询系统词库
     */
    public PageDomain<SystemDictionaryVO> page(SystemDictionaryQueryDTO queryDTO, PageDomain<SystemDictionaryVO> page) {
        // 构建查询条件
        LambdaQueryWrapper<SystemDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(queryDTO.getDictType()), SystemDictionary::getDictType, queryDTO.getDictType())
                .like(StringUtils.hasText(queryDTO.getDictKey()), SystemDictionary::getDictKey, queryDTO.getDictKey())
                .like(StringUtils.hasText(queryDTO.getDictValue()), SystemDictionary::getDictValue, queryDTO.getDictValue())
                .ge(queryDTO.getStartTime() != null, SystemDictionary::getCreatedAt, queryDTO.getStartTime())
                .le(queryDTO.getEndTime() != null, SystemDictionary::getCreatedAt, queryDTO.getEndTime())
                .eq(queryDTO.getStatus() != null, SystemDictionary::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getSpaceId() != null, SystemDictionary::getSpaceId, queryDTO.getSpaceId())
                .orderByAsc(SystemDictionary::getDictType)
                .orderByAsc(SystemDictionary::getSortOrder)
                .orderByDesc(SystemDictionary::getUpdatedAt);

        // 分页查询
        IPage<SystemDictionary> pageResult = this.page(new Page<>(page.getPageNum(), page.getPageSize()), wrapper);
        page.setTotal(pageResult.getTotal());

        List<SystemDictionary> records = pageResult.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            page.setData(new ArrayList<>());
            return page;
        }

        // 转换为VO并设置额外信息
        List<SystemDictionaryVO> voList = convertToVoList(records);
        page.setData(voList);

        return page;
    }

    /**
     * 查询词库列表（不分页）
     */
    public List<SystemDictionaryVO> list(SystemDictionaryQueryDTO queryDTO) {
        LambdaQueryWrapper<SystemDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(queryDTO.getDictType()), SystemDictionary::getDictType, queryDTO.getDictType())
                .like(StringUtils.hasText(queryDTO.getDictKey()), SystemDictionary::getDictKey, queryDTO.getDictKey())
                .like(StringUtils.hasText(queryDTO.getDictValue()), SystemDictionary::getDictValue, queryDTO.getDictValue())
                .eq(queryDTO.getStatus() != null, SystemDictionary::getStatus, queryDTO.getStatus())
                .orderByAsc(SystemDictionary::getDictType)
                .orderByAsc(SystemDictionary::getSortOrder)
                .orderByDesc(SystemDictionary::getUpdatedAt);

        List<SystemDictionary> records = this.list(wrapper);
        return convertToVoList(records);
    }

    public void export(SystemDictionaryQueryDTO queryDTO, HttpServletResponse response) throws Exception {
        // 构建查询条件
        LambdaQueryWrapper<SystemDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(queryDTO.getDictType()), SystemDictionary::getDictType, queryDTO.getDictType())
                .like(StringUtils.hasText(queryDTO.getDictKey()), SystemDictionary::getDictKey, queryDTO.getDictKey())
                .like(StringUtils.hasText(queryDTO.getDictValue()), SystemDictionary::getDictValue, queryDTO.getDictValue())
                .ge(queryDTO.getStartTime() != null, SystemDictionary::getCreatedAt, queryDTO.getStartTime())
                .le(queryDTO.getEndTime() != null, SystemDictionary::getCreatedAt, queryDTO.getEndTime())
                .eq(queryDTO.getStatus() != null, SystemDictionary::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getSpaceId() != null, SystemDictionary::getSpaceId, queryDTO.getSpaceId())
                .eq(SystemDictionary::getDeleteFlag, false)
                .orderByAsc(SystemDictionary::getDictType)
                .orderByAsc(SystemDictionary::getSortOrder)
                .orderByDesc(SystemDictionary::getUpdatedAt);
        List<SystemDictionary> systemDictionaries = this.list(wrapper);
        Getters<SystemDictionary> getters = new Getters();
        String fileName ="";
        switch (queryDTO.getDictType()) {
            case "PROPER_NOUN":
                getters.addText("专用名字", SystemDictionary::getDictKey);
                fileName = "专有名词_" + LocalDateTime.now().format(DateFormats.yyyyMMdd_HHmmss) + ".xlsx";
                break;
            case "SYNONYM":
                getters.addText("特征词", SystemDictionary::getDictKey);
                getters.addText("同义词", SystemDictionary::getDictValue);
                fileName = "同义词_" + LocalDateTime.now().format(DateFormats.yyyyMMdd_HHmmss) + ".xlsx";
                break;
            case "CORRECTION":
                getters.addText("纠错词", SystemDictionary::getDictKey);
                getters.addText("特征词", SystemDictionary::getDictValue);
                fileName = "纠错词_" + LocalDateTime.now().format(DateFormats.yyyyMMdd_HHmmss) + ".xlsx";
                break;
            case "SENSITIVE":
                getters.addText("敏感词", SystemDictionary::getDictKey);
                fileName = "敏感词_" + LocalDateTime.now().format(DateFormats.yyyyMMdd_HHmmss) + ".xlsx";
                break;
            case "SUGGESTION":
                getters.addText("搜索联想词", SystemDictionary::getDictKey);
                fileName = "搜索联想词_" + LocalDateTime.now().format(DateFormats.yyyyMMdd_HHmmss) + ".xlsx";
                break;
        }
        DateTimeFormatter dateTimeFormatter = DateFormats.STANDARD;
        getters.addText("更新时间", SystemDictionary::getUpdatedAt, dateTimeFormatter::format);
        ServletUtils.setFileName(response, fileName);
        ExcelUtils.write(systemDictionaries, getters, response.getOutputStream());
    }

    /**
     * 根据ID查询词库
     */
    public SystemDictionaryVO getById(Long id) {
        SystemDictionary dictionary = super.getById(id);
        if (dictionary == null || dictionary.getDeleteFlag()) {
            return null;
        }

        List<SystemDictionaryVO> voList = convertToVoList(List.of(dictionary));
        return voList.isEmpty() ? null : voList.get(0);
    }

    /**
     * 创建词库
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(SystemDictionary dictionary) {
        // 检查键是否重复
        if (checkKeyExists(dictionary.getDictType(), dictionary.getDictKey(), dictionary.getSpaceId(), null)) {
            throw new ServiceException("该类型下词库键已存在");
        }

        // 设置默认值
        if (dictionary.getStatus() == null) {
            dictionary.setStatus(1);
        }
        if (dictionary.getSortOrder() == null) {
            dictionary.setSortOrder(0);
        }
        dictionary.setDeleteFlag(false);

        return this.save(dictionary);
    }

    /**
     * 更新词库
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(SystemDictionary dictionary) {
        // 检查词库是否存在
        SystemDictionary existing = super.getById(dictionary.getId());
        if (existing == null || existing.getDeleteFlag()) {
            throw new ServiceException("词库不存在");
        }

        // 检查键是否重复（排除自己）
        if (StringUtils.hasText(dictionary.getDictKey()) &&
                checkKeyExists(dictionary.getDictType() != null ? dictionary.getDictType() : existing.getDictType(),
                        dictionary.getDictKey(), existing.getSpaceId(), dictionary.getId())) {
            throw new ServiceException("该类型下词库键已存在");
        }

        return this.updateById(dictionary);
    }

    /**
     * 删除词库
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        return this.removeById(id);
    }

    /**
     * 批量删除词库
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDelete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return true;
        }

        List<SystemDictionary> dictionaries = this.listByIds(ids);
        for (SystemDictionary dictionary : dictionaries) {
            if (!dictionary.getDeleteFlag()) {
                dictionary.setDeleteFlag(true);
            }
        }

        return this.updateBatchById(dictionaries);
    }

    /**
     * 批量导入词库
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchImport(SystemDictionaryImportDTO importDTO) {
        if (CollectionUtils.isEmpty(importDTO.getDictItems())) {
            throw new ServiceException("导入数据不能为空");
        }

        List<SystemDictionary> dictionaries = new ArrayList<>();
        for (SystemDictionaryImportDTO.DictItem item : importDTO.getDictItems()) {
            // 检查键是否重复
            if (checkKeyExists(importDTO.getDictType(), item.getDictKey(), importDTO.getSpaceId(), null)) {
                log.warn("词库键已存在，跳过：{}", item.getDictKey());
                continue;
            }

            SystemDictionary dictionary = new SystemDictionary();
            dictionary.setDictType(importDTO.getDictType());
            dictionary.setDictKey(item.getDictKey());
            dictionary.setDictValue(item.getDictValue());
            dictionary.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : 0);
            dictionary.setStatus(1);
            dictionary.setDeleteFlag(false);

            dictionaries.add(dictionary);
        }

        if (dictionaries.isEmpty()) {
            throw new ServiceException("没有可导入的数据");
        }

        return this.saveBatch(dictionaries);
    }

    /**
     * 根据类型查询词库
     */
    public List<SystemDictionaryVO> listByType(String dictType) {
        LambdaQueryWrapper<SystemDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictionary::getDictType, dictType)
                .eq(SystemDictionary::getStatus, 1)
                .eq(SystemDictionary::getDeleteFlag, false)
                .orderByAsc(SystemDictionary::getSortOrder)
                .orderByDesc(SystemDictionary::getUpdatedAt);

        List<SystemDictionary> records = this.list(wrapper);
        return convertToVoList(records);
    }

    /**
     * 获取所有词库类型
     */
    public List<Map<String, String>> getDictTypes() {
        List<Map<String, String>> types = new ArrayList<>();

        Map<String, String> properNoun = new HashMap<>();
        properNoun.put("code", SystemDictionary.DictType.PROPER_NOUN);
        properNoun.put("name", "专有名词");
        types.add(properNoun);

        Map<String, String> synonym = new HashMap<>();
        synonym.put("code", SystemDictionary.DictType.SYNONYM);
        synonym.put("name", "同义词");
        types.add(synonym);

        Map<String, String> correction = new HashMap<>();
        correction.put("code", SystemDictionary.DictType.CORRECTION);
        correction.put("name", "纠错词");
        types.add(correction);

        Map<String, String> sensitive = new HashMap<>();
        sensitive.put("code", SystemDictionary.DictType.SENSITIVE);
        sensitive.put("name", "敏感词");
        types.add(sensitive);

        Map<String, String> suggestion = new HashMap<>();
        suggestion.put("code", SystemDictionary.DictType.SUGGESTION);
        suggestion.put("name", "搜索联想词");
        types.add(suggestion);

        return types;
    }

    /**
     * 检查键是否已存在
     */
    private boolean checkKeyExists(String dictType, String dictKey, Long spaceId, Long excludeId) {
        LambdaQueryWrapper<SystemDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemDictionary::getDictType, dictType)
                .eq(SystemDictionary::getDictKey, dictKey)
                .eq(SystemDictionary::getDeleteFlag, false)
                .eq(SystemDictionary::getSpaceId, spaceId)
                .ne(excludeId != null, SystemDictionary::getId, excludeId);
        return this.count(wrapper) > 0;
    }

    /**
     * 转换为VO列表
     */
    private List<SystemDictionaryVO> convertToVoList(List<SystemDictionary> records) {
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }

        // 收集所有需要查询的用户ID
        List<Long> userIds = new ArrayList<>();
        for (SystemDictionary record : records) {
            if (record.getCreatedBy() != null) {
                userIds.add(record.getCreatedBy());
            }
            if (record.getUpdatedBy() != null) {
                userIds.add(record.getUpdatedBy());
            }
        }

        // 批量查询用户信息
        Map<Long, String> userNameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectByIds(userIds.stream().distinct().collect(Collectors.toList()));
            userNameMap = users.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));
        }

        // 类型名称映射
        Map<String, String> typeNameMap = new HashMap<>();
        typeNameMap.put(SystemDictionary.DictType.PROPER_NOUN, "专有名词");
        typeNameMap.put(SystemDictionary.DictType.SYNONYM, "同义词");
        typeNameMap.put(SystemDictionary.DictType.CORRECTION, "纠错词");
        typeNameMap.put(SystemDictionary.DictType.SENSITIVE, "敏感词");
        typeNameMap.put(SystemDictionary.DictType.SUGGESTION, "搜索联想词");

        // 转换为VO
        List<SystemDictionaryVO> voList = new ArrayList<>();
        for (SystemDictionary record : records) {
            SystemDictionaryVO vo = new SystemDictionaryVO();
            // 复制基本属性
            vo.setId(record.getId());
            vo.setDictType(record.getDictType());
            vo.setDictKey(record.getDictKey());
            vo.setDictValue(record.getDictValue());
            vo.setSortOrder(record.getSortOrder());
            vo.setStatus(record.getStatus());
            vo.setDeleteFlag(record.getDeleteFlag());
            vo.setCreatedAt(record.getCreatedAt());
            vo.setCreatedBy(record.getCreatedBy());
            vo.setUpdatedAt(record.getUpdatedAt());
            vo.setUpdatedBy(record.getUpdatedBy());

            // 设置扩展属性
            vo.setCreatedByName(userNameMap.get(record.getCreatedBy()));
            vo.setUpdatedByName(userNameMap.get(record.getUpdatedBy()));
            vo.setDictTypeName(typeNameMap.get(record.getDictType()));

            voList.add(vo);
        }

        return voList;
    }
} 