package com.wudao.kms.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

/**
 * 文档内容提取结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentContentResult {
    
    /**
     * 文本段落列表
     */
    private List<DocumentSegment> segments = new ArrayList<>();
    
    /**
     * 图片信息列表
     */
    private List<DocumentImage> images = new ArrayList<>();
    
    /**
     * 文档总字符数
     */
    private int totalCharacters;
    
    /**
     * 提取的原始文本内容
     */
    private String rawContent;
    
    /**
     * 文档段落
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentSegment {
        /**
         * 段落序号
         */
        private int index;
        
        /**
         * 段落内容
         */
        private String content;
        
        /**
         * 段落类型（标题、正文、列表等）
         */
        private String type;
        
        /**
         * 字符长度
         */
        private int length;
        
        /**
         * 页码（仅PDF有效）
         */
        private Integer pageNumber;
    }
    
    /**
     * 文档图片
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentImage {
        /**
         * 图片名称
         */
        private String name;
        
        /**
         * 图片S3存储路径
         */
        private String s3Key;
        
        /**
         * 图片访问URL
         */
        private String url;
        
        /**
         * Markdown格式的图片链接
         */
        private String markdownLink;
        
        /**
         * 图片格式（png, jpg等）
         */
        private String format;
        
        /**
         * 图片在文档中的位置
         */
        private int position;
        
        /**
         * 页码（仅PDF有效）
         */
        private Integer pageNumber;
        
        /**
         * 图片大小（字节）
         */
        private long size;
    }
} 