package com.wudao.kms.service;

import com.wudao.kms.dto.knowledgefile.KnowLedgeSegmentConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Java实现的文档分段服务
 * 替代Python API，在本地实现文档分段功能
 */
@Slf4j
@Service
public class JavaDocumentSegmentService {

    /**
     * 处理文档分段
     * 
     * @param fileContent 文件内容
     * @param segmentConfig 分段配置
     * @return 分段结果列表
     */
    public List<String> processDocumentSegmentation(String fileContent, KnowLedgeSegmentConfig segmentConfig) {
        try {
            log.info("开始Java分段处理，内容长度: {}", fileContent.length());
            
            // 首先按图片标签分段
            List<String> imageSegments = splitByImageTags(fileContent);
            
            // 根据分段方式进行处理
            String splitMethod = segmentConfig.getSplitMethod();
            Integer maxLength = segmentConfig.getCustomSegmentSize();
            Integer overlap = segmentConfig.getCustomSegmentOverlap();
            
            // 设置默认值
            if (maxLength == null || maxLength <= 0) {
                maxLength = 800;
            }
            if (overlap == null || overlap < 0) {
                overlap = 0;
            }

            List<String> finalSegments = new ArrayList<>();

            // 对每个初始分段进行进一步处理（不考虑overlap）
            for (String segment : imageSegments) {
                // 对非图片标签内容进行常规分段
                List<String> subSegments;
                if ("normal".equals(segmentConfig.getSegmentMethod())) {
                    subSegments = splitByLength(segment, maxLength, 0); // 暂时不考虑overlap
                } else {
                    subSegments = splitByDelimiter(segment, splitMethod, maxLength, 0); // 暂时不考虑overlap
                }
                finalSegments.addAll(subSegments);
            }
            
            // 分段完成后，统一应用overlap
            if (overlap > 0) {
                finalSegments = addOverlap(finalSegments, overlap);
            }
            
            log.info("分段完成，生成 {} 个段落", finalSegments.size());
            return finalSegments;
            
        } catch (Exception e) {
            log.error("Java分段处理失败", e);
            throw new RuntimeException("文档分段处理失败: " + e.getMessage());
        }
    }

    /**
     * 按分隔符分段
     */
    private List<String> splitByDelimiter(String content, String splitMethod, int maxLength, int overlap) {
        List<String> segments = new ArrayList<>();
        StringBuilder currentSegment = new StringBuilder();
        int i = 0;
        
        while (i < content.length()) {
            // 检查是否遇到图片标签开始
            if (content.startsWith("<img src=", i)) {
                // 保存之前的文本（如果有）
                if (!currentSegment.isEmpty()) {
                    String text = currentSegment.toString().trim();
                    if (!text.isEmpty()) {
                        // 按长度和重叠切分文本
                        segments.addAll(splitTextByLength(text, maxLength, overlap));
                    }
                    currentSegment = new StringBuilder();
                }
                
                // 找到图片标签的结束位置
                int endIndex = content.indexOf("/>", i);
                if (endIndex != -1) {
                    // 添加图片标签作为独立段落
                    segments.add(content.substring(i, endIndex + 2));
                    i = endIndex + 2;
                } else {
                    // 如果没找到结束标签，当作普通文本处理
                    currentSegment.append(content.charAt(i));
                    i++;
                }
            } else {
                currentSegment.append(content.charAt(i));
                i++;
                
                // 检查是否达到分隔符
                String currentText = currentSegment.toString();
                boolean shouldSplit = false;

                if (StringUtil.isNotBlank(splitMethod)) {
                    switch (splitMethod) {
                        case "double_newline":
                            shouldSplit = currentText.endsWith("\n\n");
                            break;
                        case "newline":
                            shouldSplit = currentText.endsWith("\n");
                            break;
                        case "period":
                            shouldSplit = currentText.matches(".*[。！？.!?]\\s*$");
                            break;
                        case "comma":
                            shouldSplit = currentText.matches(".*[，,]\\s*$");
                            break;
                        case "semicolon":
                            shouldSplit = currentText.matches(".*[；;]\\s*$");
                            break;
                        case "space":
                            shouldSplit = currentText.endsWith(" ");
                            break;
                        default:
                            // 默认不分割
                            break;
                    }
                }
                
                if (shouldSplit || currentSegment.length() >= maxLength) {
                    String text = currentSegment.toString().trim();
                    if (!text.isEmpty()) {
                        segments.addAll(splitTextByLength(text, maxLength, overlap));
                    }
                    currentSegment = new StringBuilder();
                }
            }
        }
        
        // 添加最后一个段落（如果有）
        if (currentSegment.length() > 0) {
            String text = currentSegment.toString().trim();
            if (!text.isEmpty()) {
                segments.addAll(splitTextByLength(text, maxLength, overlap));
            }
        }
        
        return segments;
    }
    
    /**
     * 对文本内容进行分段
     */
    private List<String> splitTextContent(String content, String splitMethod) {
        List<String> segments;
        
        switch (splitMethod) {
            case "double_newline":
                // 双换行分段
                segments = Arrays.asList(content.split("\\n\\s*\\n"));
                break;
            case "newline":
                // 单换行分段
                segments = Arrays.asList(content.split("\\n"));
                break;
            case "space":
                // 空格分段（按句子）
                segments = Arrays.asList(content.split("\\s+"));
                break;
            case "period":
                // 句号分段
                segments = Arrays.asList(content.split("[。！？.!?]+"));
                break;
            case "comma":
                // 逗号分段
                segments = Arrays.asList(content.split("[，,]+"));
                break;
            case "semicolon":
                // 分号分段
                segments = Arrays.asList(content.split("[；;]+"));
                break;
            default:
                // 自定义符号分段
                if (splitMethod.startsWith("custom:")) {
                    String customDelimiter = splitMethod.substring(7);
                    segments = Arrays.asList(content.split(Pattern.quote(customDelimiter)));
                } else {
                    // 默认按双换行
                    segments = Arrays.asList(content.split("\\n\\s*\\n"));
                }
                break;
        }
        
        return segments;
    }
    
    /**
     * 按长度分段（不使用分隔符）
     */
    private List<String> splitByLength(String content, int maxLength, int overlap) {
        List<String> segments = new ArrayList<>();
        StringBuilder currentSegment = new StringBuilder();
        int i = 0;
        
        while (i < content.length()) {
            // 检查是否遇到图片标签开始
            if (content.startsWith("<img src=", i)) {
                // 保存之前的文本（如果有）
                if (!currentSegment.isEmpty()) {
                    String text = currentSegment.toString().trim();
                    if (!text.isEmpty()) {
                        segments.addAll(splitTextByLength(text, maxLength, overlap));
                    }
                    currentSegment = new StringBuilder();
                }
                
                // 找到图片标签的结束位置
                int endIndex = content.indexOf("/>", i);
                if (endIndex != -1) {
                    // 添加图片标签作为独立段落
                    segments.add(content.substring(i, endIndex + 2));
                    i = endIndex + 2;
                } else {
                    // 如果没找到结束标签，当作普通文本处理
                    currentSegment.append(content.charAt(i));
                    i++;
                }
            } else {
                currentSegment.append(content.charAt(i));
                
                // 检查是否达到最大长度
                if (currentSegment.length() >= maxLength) {
                    String text = currentSegment.toString().trim();
                    if (!text.isEmpty()) {
                        segments.addAll(splitTextByLength(text, maxLength, overlap));
                    }
                    currentSegment = new StringBuilder();
                }
                i++;
            }
        }
        
        // 添加最后一个段落（如果有）
        if (!currentSegment.isEmpty()) {
            String text = currentSegment.toString().trim();
            if (!text.isEmpty()) {
                segments.addAll(splitTextByLength(text, maxLength, overlap));
            }
        }
        
        return segments;
    }
    
    /**
     * 按照指定长度切分文本（不处理重叠）
     */
    private List<String> splitTextByLength(String text, int maxLength, int overlap) {
        List<String> segments = new ArrayList<>();
        
        if (text.length() <= maxLength) {
            segments.add(text);
            return segments;
        }
        
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxLength, text.length());
            
            // 尝试在单词边界切分（避免切断单词）
            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);
                int lastNewline = text.lastIndexOf('\n', end);
                int breakPoint = Math.max(lastSpace, lastNewline);
                
                // 如果找到合适的切分点，且不会使段落过短
                if (breakPoint > start + maxLength / 2) {
                    end = breakPoint;
                }
            }
            
            String segment = text.substring(start, end).trim();
            if (!segment.isEmpty()) {
                segments.add(segment);
            }
            
            // 移动到下一个开始位置（不考虑重叠）
            start = end;
        }
        
        return segments;
    }
    
    /**
     * 调整段落长度，合并过短段落，切分过长段落
     */
    private List<String> adjustSegmentLength(List<String> segments, int maxLength, int overlap) {
        List<String> adjustedSegments = new ArrayList<>();
        StringBuilder currentSegment = new StringBuilder();
        
        for (String segment : segments) {
            // 如果是图片标签，直接添加并重置currentSegment
            if (segment.trim().matches("<img\\s+src=.*?/>")) {
                // 先添加当前累积的文本段落（如果有）
                if (currentSegment.length() > 0) {
                    adjustedSegments.add(currentSegment.toString().trim());
                    currentSegment = new StringBuilder();
                }
                // 添加图片标签
                adjustedSegments.add(segment.trim());
                continue;
            }
            
            // 处理非图片内容
            if (currentSegment.length() == 0) {
                currentSegment.append(segment);
            } else {
                // 检查合并后是否超长
                int combinedLength = currentSegment.length() + segment.length() + 1; // +1 for space
                
                if (combinedLength <= maxLength) {
                    // 可以合并
                    currentSegment.append(" ").append(segment);
                } else {
                    // 不能合并，输出当前段落
                    adjustedSegments.add(currentSegment.toString().trim());
                    currentSegment = new StringBuilder(segment);
                }
            }
            
            // 如果当前段落超长，需要切分
            if (currentSegment.length() > maxLength) {
                List<String> splitSegments = splitByLength(currentSegment.toString(), maxLength, overlap);
                adjustedSegments.addAll(splitSegments);
                currentSegment = new StringBuilder();
            }
        }
        
        // 添加最后一个段落
        if (currentSegment.length() > 0) {
            if (currentSegment.length() > maxLength) {
                List<String> splitSegments = splitByLength(currentSegment.toString(), maxLength, overlap);
                adjustedSegments.addAll(splitSegments);
            } else {
                adjustedSegments.add(currentSegment.toString().trim());
            }
        }
        
        return adjustedSegments;
    }
    
    /**
     * 生成段落重叠
     */
    private List<String> addOverlap(List<String> segments, int overlap) {
        if (overlap <= 0 || segments.size() <= 1) {
            return segments;
        }
        
        List<String> overlappedSegments = new ArrayList<>();
        
        for (int i = 0; i < segments.size(); i++) {
            String segment = segments.get(i);
            
            // 如果当前段落是图片标签，直接添加，不添加重叠
            if (segment.trim().matches("<img\\s+src=.*?/>")) {
                overlappedSegments.add(segment);
                continue;
            }
            
            // 为文本段落添加前一个文本段落的尾部作为重叠
            if (i > 0) {
                // 向前查找最近的文本段落（跳过图片标签）
                for (int j = i - 1; j >= 0; j--) {
                    String prevSegment = segments.get(j);
                    // 如果前一个段落是文本（不是图片标签）
                    if (!prevSegment.trim().matches("<img\\s+src=.*?/>")) {
                        if (prevSegment.length() > overlap) {
                            String prevTail = prevSegment.substring(prevSegment.length() - overlap);
                            segment = prevTail + " " + segment;
                        } else if (prevSegment.length() > 0) {
                            // 如果前一个段落长度小于overlap，全部添加
                            segment = prevSegment + " " + segment;
                        }
                        break;
                    }
                }
            }
            
            overlappedSegments.add(segment);
        }
        
        return overlappedSegments;
    }
    
    /**
     * 验证分段配置
     */
    public boolean validateSegmentConfig(KnowLedgeSegmentConfig config) {
        if (config == null) {
            return false;
        }
        
        Integer maxLength = config.getMaxLength();
        if (maxLength != null && (maxLength < 50 || maxLength > 1800)) {
            return false;
        }
        
        Integer overlap = config.getOverlap();
        if (overlap != null && overlap < 0) {
            return false;
        }
        
        if (overlap != null && maxLength != null && overlap >= maxLength) {
            return false;
        }
        
        return true;
    }

    /**
     * 按图片标签分段
     */
    private List<String> splitByImageTags(String content) {
        List<String> segments = new ArrayList<>();
        Pattern pattern = Pattern.compile("(<img *?/>)");
        String[] parts = pattern.split(content);
        java.util.regex.Matcher matcher = pattern.matcher(content);
        
        int currentIndex = 0;
        while (currentIndex < parts.length) {
            // 添加非图片标签内容
            if (!parts[currentIndex].trim().isEmpty()) {
                segments.add(parts[currentIndex].trim());
            }
            
            // 如果找到图片标签，添加到结果中
            if (matcher.find()) {
                segments.add(matcher.group(1));
            }
            
            currentIndex++;
        }
        
        return segments;
    }
} 