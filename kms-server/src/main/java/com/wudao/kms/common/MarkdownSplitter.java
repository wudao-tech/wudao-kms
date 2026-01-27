package com.wudao.kms.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown智能分割器 - Java版本
 * 基于token限制，保护Markdown格式
 */
public class MarkdownSplitter {

    private static final Pattern IMG_PATTERN = Pattern.compile("!\\[[^\\]]*\\]\\([^)]+\\)");
    private static final Pattern LINK_PATTERN = Pattern.compile("\\[[^\\]]+\\]\\([^)]+\\)");
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```[^`]*```", Pattern.DOTALL);
    private static final Pattern INLINE_CODE_PATTERN = Pattern.compile("`[^`\n]+`");
    private static final Pattern CHINESE_CHAR_PATTERN = Pattern.compile("[\\u4e00-\\u9fff]");
    private static final Pattern ENGLISH_WORD_PATTERN = Pattern.compile("\\b[a-zA-Z]+\\b");
    private static final Pattern SENTENCE_SPLIT_PATTERN = Pattern.compile("([。！？.!?]+)");

    /**
     * 保护结果类
     */
    public static class ProtectionResult {
        public final String protectedText;
        public final Map<String, String> placeholders;

        public ProtectionResult(String protectedText, Map<String, String> placeholders) {
            this.protectedText = protectedText;
            this.placeholders = placeholders;
        }
    }

    /**
     * 表格块位置
     */
    public static class TableBlock {
        public final int start;
        public final int end;

        public TableBlock(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * 分割结果
     */
    public static class SplitResult {
        public final String content;
        public final Map<String, Object> metadata;

        public SplitResult(String content, Map<String, Object> metadata) {
            this.content = content;
            this.metadata = metadata;
        }
    }

    /**
     * 保护Markdown语法元素
     */
    public ProtectionResult protectMarkdownSyntax(String text) {
        Map<String, String> placeholders = new HashMap<>();
        String protectedText = text;
        int counter = 0;

        // 保护图片 ![alt](url)
        Matcher imgMatcher = IMG_PATTERN.matcher(protectedText);
        while (imgMatcher.find()) {
            String placeholder = "__IMG_PLACEHOLDER_" + counter + "__";
            placeholders.put(placeholder, imgMatcher.group(0));
            protectedText = protectedText.replace(imgMatcher.group(0), placeholder);
            counter++;
            imgMatcher = IMG_PATTERN.matcher(protectedText); // 重新创建matcher
        }

        // 保护链接 [text](url)
        Matcher linkMatcher = LINK_PATTERN.matcher(protectedText);
        while (linkMatcher.find()) {
            String placeholder = "__LINK_PLACEHOLDER_" + counter + "__";
            placeholders.put(placeholder, linkMatcher.group(0));
            protectedText = protectedText.replace(linkMatcher.group(0), placeholder);
            counter++;
            linkMatcher = LINK_PATTERN.matcher(protectedText);
        }

        // 保护代码块 ```code```
        Matcher codeMatcher = CODE_BLOCK_PATTERN.matcher(protectedText);
        while (codeMatcher.find()) {
            String placeholder = "__CODE_PLACEHOLDER_" + counter + "__";
            placeholders.put(placeholder, codeMatcher.group(0));
            protectedText = protectedText.replace(codeMatcher.group(0), placeholder);
            counter++;
            codeMatcher = CODE_BLOCK_PATTERN.matcher(protectedText);
        }

        // 保护行内代码 `code`
        Matcher inlineMatcher = INLINE_CODE_PATTERN.matcher(protectedText);
        while (inlineMatcher.find()) {
            String placeholder = "__INLINE_PLACEHOLDER_" + counter + "__";
            placeholders.put(placeholder, inlineMatcher.group(0));
            protectedText = protectedText.replace(inlineMatcher.group(0), placeholder);
            counter++;
            inlineMatcher = INLINE_CODE_PATTERN.matcher(protectedText);
        }

        return new ProtectionResult(protectedText, placeholders);
    }

    /**
     * 恢复保护的Markdown语法元素
     */
    public String restoreMarkdownSyntax(String text, Map<String, String> placeholders) {
        String restored = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            restored = restored.replace(entry.getKey(), entry.getValue());
        }
        return restored;
    }

    /**
     * 检测表格块的位置
     */
    public List<TableBlock> detectTableBlocks(String text) {
        List<TableBlock> tableBlocks = new ArrayList<>();
        String[] lines = text.split("\n");
        Integer currentTableStart = null;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmedLine = line.trim();
            
            // 更健壮的表格行检测逻辑
            // 1. 必须包含管道符
            // 2. 使用 -1 参数确保 split 包含末尾空字符串，防止因末尾空单元格导致列数计算错误
            // 3. 至少有2个管道符（即至少1列，如 | data |）或者如果不以管道符开头结尾，至少有1个分隔符（data | data）
            boolean hasPipe = line.contains("|");
            boolean isTableLine = false;
            
            if (hasPipe) {
                int pipeCount = line.length() - line.replace("|", "").length();
                // 如果以管道符开头，通常是规范的Markdown表格
                if (trimmedLine.startsWith("|")) {
                     isTableLine = pipeCount >= 2;
                } else {
                    // 如果不以管道符开头，可能是简写形式，要求列数>=2 (即至少1个中间的管道符)
                    // 但为了保险，我们主要针对规范表格，或者至少看起来像表格的行
                    // 这里使用 split 长度判断，split长度 = 管道符数 + 1 (如果首尾都没有管道符)
                    // 比如 a | b -> ["a ", " b"] len=2
                    isTableLine = line.split("\\|", -1).length >= 2;
                }
            }
            
            // 分隔行检测 (|---|---|)
            boolean isSeparator = trimmedLine.contains("---") && trimmedLine.contains("|");

            if (isTableLine || isSeparator) {
                if (currentTableStart == null) {
                    currentTableStart = i;
                }
            } else {
                if (currentTableStart != null) {
                    tableBlocks.add(new TableBlock(currentTableStart, i - 1));
                    currentTableStart = null;
                }
            }
        }

        // 处理文档末尾的表格
        if (currentTableStart != null) {
            tableBlocks.add(new TableBlock(currentTableStart, lines.length - 1));
        }

        return tableBlocks;
    }

    /**
     * 估算token数量（按字符数计算）
     */
    public int estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        // 直接返回字符数作为token数
        // embedding模型的token是按字符计算的
        return text.length();
    }

    /**
     * 智能Markdown分割
     */
    public List<String> smartSplitMarkdown(String text, int maxTokens, int overlap) {
        return smartSplitMarkdown(text, maxTokens, overlap, null);
    }

    /**
     * 智能Markdown分割（带自定义分隔符）
     */
    public List<String> smartSplitMarkdown(String text, int maxTokens, int overlap, String customSeparators) {
        // 1. 处理自定义分隔符
        List<String> separators = parseCustomSeparators(customSeparators);

        // 2. 如果有自定义分隔符，先尝试按分隔符分割
        if (!separators.isEmpty()) {
            List<String> headerChunks = splitByCustomSeparators(text, separators, maxTokens);
            if (!headerChunks.isEmpty()) {
                // 对每个header chunk进行进一步处理
                List<String> finalChunks = new ArrayList<>();
                for (String chunk : headerChunks) {
                    if (estimateTokens(chunk) <= maxTokens) {
                        finalChunks.add(chunk);
                    } else {
                        // 如果header chunk太大，使用智能分割
                        List<String> subChunks = smartSplitMarkdownInternal(chunk, maxTokens);
                        finalChunks.addAll(subChunks);
                    }
                }

                // 添加重叠
                if (overlap > 0) {
                    finalChunks = addOverlapToChunks(finalChunks, overlap, maxTokens);
                }
                return finalChunks;
            }
        }

        // 3. 使用智能分割（无自定义分隔符或分隔符分割失败）
        return smartSplitMarkdownInternal(text, maxTokens, overlap);
    }

    /**
     * 解析自定义分隔符
     */
    private List<String> parseCustomSeparators(String customSeparators) {
        List<String> separators = new ArrayList<>();
        if (customSeparators != null && !customSeparators.trim().isEmpty()) {
            for (String sep : customSeparators.split(",")) {
                sep = sep.trim();
                if (!sep.isEmpty()) {
                    // 确保分隔符以#开头
                    if (!sep.startsWith("#")) {
                        sep = "#" + sep;
                    }
                    separators.add(sep);
                }
            }
        }
        return separators;
    }

    /**
     * 按自定义分隔符分割
     */
    private List<String> splitByCustomSeparators(String text, List<String> separators, int maxTokens) {
        List<String> chunks = new ArrayList<>();
        String[] lines = text.split("\n");
        List<String> currentChunk = new ArrayList<>();

        for (String line : lines) {
            boolean isHeaderLine = false;

            // 检查是否是分隔符行
            for (String separator : separators) {
                if (line.trim().startsWith(separator + " ") || line.trim().equals(separator)) {
                    isHeaderLine = true;
                    break;
                }
            }

            if (isHeaderLine && !currentChunk.isEmpty()) {
                // 保存当前chunk
                String chunkText = String.join("\n", currentChunk);
                if (!chunkText.trim().isEmpty()) {
                    chunks.add(chunkText.trim());
                }
                currentChunk = new ArrayList<>();
                currentChunk.add(line);
            } else {
                currentChunk.add(line);
            }
        }

        // 添加最后一个chunk
        if (!currentChunk.isEmpty()) {
            String chunkText = String.join("\n", currentChunk);
            if (!chunkText.trim().isEmpty()) {
                chunks.add(chunkText.trim());
            }
        }

        return chunks;
    }

    /**
     * 内部智能分割方法
     */
    private List<String> smartSplitMarkdownInternal(String text, int maxTokens) {
        return smartSplitMarkdownInternal(text, maxTokens, 0);
    }

    /**
     * 内部智能分割方法（带重叠）
     */
    private List<String> smartSplitMarkdownInternal(String text, int maxTokens, int overlap) {
        // 1. 保护Markdown语法
        ProtectionResult protection = protectMarkdownSyntax(text);
        String protectedText = protection.protectedText;
        Map<String, String> placeholders = protection.placeholders;

        // 2. 检测表格位置
        String[] lines = protectedText.split("\n");
        List<TableBlock> tableBlocks = detectTableBlocks(protectedText);

        // 3. 基于token限制的智能分割
        List<String> chunks = new ArrayList<>();
        List<String> currentChunkLines = new ArrayList<>();
        int currentTokens = 0;
        int i = 0;

        while (i < lines.length) {
            String line = lines[i];

            // 检查是否在表格块中
            boolean inTable = false;
            TableBlock currentTableBlock = null;
            for (TableBlock block : tableBlocks) {
                if (i >= block.start && i <= block.end) {
                    inTable = true;
                    currentTableBlock = block;
                    break;
                }
            }

            if (inTable) {
                // 处理完整表格块
                List<String> tableLines = new ArrayList<>();
                for (int j = currentTableBlock.start; j <= currentTableBlock.end; j++) {
                    tableLines.add(lines[j]);
                }

                String tableText = String.join("\n", tableLines);
                int tableTokens = estimateTokens(tableText);

                if (currentTokens + tableTokens <= maxTokens) {
                    currentChunkLines.addAll(tableLines);
                    currentTokens += tableTokens;
                } else {
                    // 保存当前chunk
                    if (!currentChunkLines.isEmpty()) {
                        String chunkText = String.join("\n", currentChunkLines);
                        String restoredChunk = restoreMarkdownSyntax(chunkText, placeholders);
                        chunks.add(restoredChunk);
                    }

                    // 表格作为新chunk
                    if (tableTokens <= maxTokens) {
                        currentChunkLines = new ArrayList<>(tableLines);
                        currentTokens = tableTokens;
                    } else {
                        // 表格太大，进行按行分割，每段都保留表头
                        List<String> headerLines = new ArrayList<>();
                        List<String> bodyLines = new ArrayList<>();

                        // 识别表头（通常前两行是表头和分隔符）
                        if (tableLines.size() >= 2 && tableLines.get(1).contains("---")) {
                            headerLines.add(tableLines.get(0));
                            headerLines.add(tableLines.get(1));
                            for (int k = 2; k < tableLines.size(); k++) bodyLines.add(tableLines.get(k));
                        } else if (!tableLines.isEmpty()) {
                            // 只有一行或者没有标准分隔符，默认第一行为头
                            headerLines.add(tableLines.get(0));
                            for (int k = 1; k < tableLines.size(); k++) bodyLines.add(tableLines.get(k));
                        }

                        String headerText = String.join("\n", headerLines);
                        int headerTokens = estimateTokens(headerText);

                        List<String> currentTableChunk = new ArrayList<>(headerLines);
                        int currentTableChunkTokens = headerTokens;

                        for (String row : bodyLines) {
                            int rowTokens = estimateTokens(row);
                            // +1 是为了计算换行符
                            if (currentTableChunkTokens + rowTokens + 1 > maxTokens && currentTableChunk.size() > headerLines.size()) {
                                // 当前片段已满，保存
                                String chunkText = String.join("\n", currentTableChunk);
                                if (!chunkText.trim().isEmpty()) {
                                    chunks.add(restoreMarkdownSyntax(chunkText, placeholders));
                                }

                                // 开启新片段，重置为仅包含表头
                                currentTableChunk = new ArrayList<>(headerLines);
                                currentTableChunkTokens = headerTokens;
                            }

                            currentTableChunk.add(row);
                            currentTableChunkTokens += rowTokens + 1;
                        }

                        // 保存最后一个片段
                        if (currentTableChunk.size() > headerLines.size()) {
                            String chunkText = String.join("\n", currentTableChunk);
                            if (!chunkText.trim().isEmpty()) {
                                chunks.add(restoreMarkdownSyntax(chunkText, placeholders));
                            }
                        }

                        currentChunkLines = new ArrayList<>();
                        currentTokens = 0;
                    }
                }

                i = currentTableBlock.end + 1;
                continue;
            }

            // 普通行处理
            int lineTokens = estimateTokens(line);

            if (currentTokens + lineTokens <= maxTokens) {
                currentChunkLines.add(line);
                currentTokens += lineTokens;
            } else {
                // 保存当前chunk
                if (!currentChunkLines.isEmpty()) {
                    String chunkText = String.join("\n", currentChunkLines);
                    if (!chunkText.trim().isEmpty()) {
                        String restoredChunk = restoreMarkdownSyntax(chunkText, placeholders);
                        chunks.add(restoredChunk);
                    }

                    currentChunkLines = new ArrayList<>();
                    currentChunkLines.add(line);
                    currentTokens = lineTokens;
                } else {
                    // 单行超过限制，强制分割
                    if (lineTokens > maxTokens) {
                        // 按maxTokens分割行内容
                        int start = 0;
                        while (start < line.length()) {
                            int end = Math.min(start + maxTokens, line.length());
                            String part = line.substring(start, end);
                            if (!part.trim().isEmpty()) {
                                String restoredPart = restoreMarkdownSyntax(part, placeholders);
                                chunks.add(restoredPart);
                            }
                            start = end;
                        }
                        currentChunkLines = new ArrayList<>();
                        currentTokens = 0;
                    } else {
                        currentChunkLines.add(line);
                        currentTokens = lineTokens;
                    }
                }
            }

            i++;
        }

        // 处理最后一个chunk
        if (!currentChunkLines.isEmpty()) {
            String chunkText = String.join("\n", currentChunkLines);
            if (!chunkText.trim().isEmpty()) {
                String restoredChunk = restoreMarkdownSyntax(chunkText, placeholders);
                chunks.add(restoredChunk);
            }
        }

        // 添加重叠内容
        if (overlap > 0) {
            chunks = addOverlapToChunks(chunks, overlap, maxTokens);
        }

        return chunks;
    }

    /**
     * 为chunks添加重叠内容
     */
    public List<String> addOverlapToChunks(List<String> chunks, int overlapSize, int maxTokens) {
        if (chunks == null || chunks.isEmpty() || overlapSize <= 0) {
            return chunks;
        }

        List<String> overlapped = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);

            if (i == 0) {
                overlapped.add(chunk);
            } else {
                String prevChunk = chunks.get(i - 1);

                // 获取重叠内容，优先在句子边界
                String[] sentences = SENTENCE_SPLIT_PATTERN.split(prevChunk);
                StringBuilder overlapText = new StringBuilder();
                int totalLen = 0;

                // 从后往前取句子
                for (int j = sentences.length - 1; j >= 0; j--) {
                    if (totalLen + sentences[j].length() <= overlapSize) {
                        overlapText.insert(0, sentences[j]);
                        totalLen += sentences[j].length();
                    } else {
                        break;
                    }
                }

                if (overlapText.length() == 0) {
                    // 如果没有合适的句子边界，直接截取
                    int start = Math.max(0, prevChunk.length() - overlapSize);
                    overlapText.append(prevChunk.substring(start));
                }

                String overlappedChunk = overlapText.toString().trim() + "\n\n" + chunk;

                // 检查添加重叠后是否超过maxTokens，如果超过则截断
                if (estimateTokens(overlappedChunk) > maxTokens) {
                    // 截断到maxTokens长度
                    overlappedChunk = overlappedChunk.substring(0, Math.min(maxTokens, overlappedChunk.length()));
                }

                overlapped.add(overlappedChunk);
            }
        }

        return overlapped;
    }

    /**
     * 创建分割结果
     */
    public List<SplitResult> createSplitResults(List<String> chunks, int overlap) {
        List<SplitResult> results = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            Map<String, Object> metadata = new HashMap<>();

            metadata.put("chunk_index", i);
            metadata.put("chunk_size", chunk.length());
            metadata.put("token_count", estimateTokens(chunk));
            metadata.put("split_method", "token_based_smart");
            metadata.put("has_overlap", i > 0 && overlap > 0);

            results.add(new SplitResult(chunk, metadata));
        }

        return results;
    }

    /**
     * 主要的公共接口
     */
    public List<SplitResult> splitMarkdownText(String text, int maxTokens, int overlap) {
        return splitMarkdownText(text, maxTokens, overlap, null);
    }

    /**
     * 主要的公共接口（带自定义分隔符）
     */
    public List<SplitResult> splitMarkdownText(String text, int maxTokens, int overlap, String customSeparators) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> chunks = smartSplitMarkdown(text, maxTokens, overlap, customSeparators);
        return createSplitResults(chunks, overlap, customSeparators);
    }

    /**
     * 按段落级别分割Markdown文档
     *
     * @param text 文本内容
     * @param maxTokens 最大token数
     * @param overlap 重叠长度
     * @param maxParagraph 最大段落深度（1=只按#分割，2=按#和##分割，3=按#、##、###分割，null=所有级别）
     * @return 分割结果
     */
    public List<SplitResult> splitMarkdownByParagraph(String text, int maxTokens, int overlap, Integer maxParagraph) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 保护Markdown语法
        ProtectionResult protection = protectMarkdownSyntax(text);
        String protectedText = protection.protectedText;
        Map<String, String> placeholders = protection.placeholders;

        // 2. 按段落级别分割
        List<String> chunks = splitByParagraphLevel(protectedText, maxTokens, maxParagraph);

        // 3. 恢复Markdown语法
        List<String> restoredChunks = new ArrayList<>();
        for (String chunk : chunks) {
            String restored = restoreMarkdownSyntax(chunk, placeholders);
            restoredChunks.add(restored);
        }

        // 4. 添加重叠内容
        if (overlap > 0) {
            restoredChunks = addOverlapToChunks(restoredChunks, overlap, maxTokens);
        }

        return createSplitResults(restoredChunks, overlap, "paragraph");
    }

    /**
     * 按段落级别分割
     */
    private List<String> splitByParagraphLevel(String text, int maxTokens, Integer maxParagraph) {
        List<String> chunks = new ArrayList<>();
        String[] lines = text.split("\n");
        List<String> currentChunkLines = new ArrayList<>();
        int currentTokens = 0;

        for (String line : lines) {
            String trimmedLine = line.trim();
            boolean isHeaderLine = false;
            int headerLevel = 0;

            // 检查是否是标题行
            if (trimmedLine.startsWith("#")) {
                // 计算标题级别
                for (char c : trimmedLine.toCharArray()) {
                    if (c == '#') {
                        headerLevel++;
                    } else {
                        break;
                    }
                }

                // 如果设置了最大段落深度，检查是否超过限制
                if (maxParagraph == null || headerLevel <= maxParagraph) {
                    isHeaderLine = true;
                }
            }

            int lineTokens = estimateTokens(line);

            // 如果遇到标题行且当前chunk不为空，保存当前chunk
            if (isHeaderLine && !currentChunkLines.isEmpty()) {
                String chunkText = String.join("\n", currentChunkLines);
                if (!chunkText.trim().isEmpty()) {
                    chunks.add(chunkText.trim());
                }
                currentChunkLines = new ArrayList<>();
                currentTokens = 0;
            }

            // 添加当前行
            if (currentTokens + lineTokens <= maxTokens) {
                currentChunkLines.add(line);
                currentTokens += lineTokens;
            } else {
                // 保存当前chunk（如果不为空）
                if (!currentChunkLines.isEmpty()) {
                    String chunkText = String.join("\n", currentChunkLines);
                    if (!chunkText.trim().isEmpty()) {
                        chunks.add(chunkText.trim());
                    }
                    currentChunkLines = new ArrayList<>();
                    currentTokens = 0;
                }

                // 如果单行就超过限制，强制分割
                if (lineTokens > maxTokens) {
                    // 按token限制截断行
                    String truncated = line.substring(0, Math.min(line.length(), maxTokens));
                    chunks.add(truncated);

                    // 剩余部分继续处理
                    String remaining = line.substring(Math.min(line.length(), maxTokens));
                    if (!remaining.trim().isEmpty()) {
                        currentChunkLines.add(remaining);
                        currentTokens = estimateTokens(remaining);
                    }
                } else {
                    currentChunkLines.add(line);
                    currentTokens = lineTokens;
                }
            }
        }

        // 处理最后一个chunk
        if (!currentChunkLines.isEmpty()) {
            String chunkText = String.join("\n", currentChunkLines);
            if (!chunkText.trim().isEmpty()) {
                chunks.add(chunkText.trim());
            }
        }

        return chunks;
    }

    /**
     * 创建分割结果（带自定义分隔符信息）
     */
    public List<SplitResult> createSplitResults(List<String> chunks, int overlap, String customSeparators) {
        List<SplitResult> results = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            Map<String, Object> metadata = new HashMap<>();

            metadata.put("chunk_index", i);
            metadata.put("chunk_size", chunk.length());
            metadata.put("token_count", estimateTokens(chunk));

            // 根据分割方式设置方法标识
            if ("paragraph".equals(customSeparators)) {
                metadata.put("split_method", "paragraph_level");
            } else if (customSeparators != null && !customSeparators.trim().isEmpty()) {
                metadata.put("split_method", "custom_separators_then_smart");
                metadata.put("custom_separators", customSeparators);
            } else {
                metadata.put("split_method", "token_based_smart");
            }

            metadata.put("has_overlap", i > 0 && overlap > 0);

            results.add(new SplitResult(chunk, metadata));
        }

        return results;
    }
}