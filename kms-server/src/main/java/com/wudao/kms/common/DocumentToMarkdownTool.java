package com.wudao.kms.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.wudao.common.utils.DateFormats;
import com.wudao.oss.service.OssService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DocumentToMarkdownTool {

    private final OssService ossService;
    @Value("${basic.profile}")
    private String basicProfile;
    private String imageUploadPath = "documents/images/"; // OSS上传路径前缀

    /**
     * 主转换方法 - 根据文件类型自动选择转换方式
     */
    public String convertToMarkdown(String filePath) throws IOException {
        String extension = getFileExtension(new File(filePath).getName()).toLowerCase();

        switch (extension) {
            case "docx":
                return convertDocxToMarkdown(filePath);
            case "pdf":
                return convertPdfToMarkdown(filePath);
            case "txt":
                return convertTxtToMarkdown(filePath);
            case "csv":
                try {
                    return convertCsvToMarkdown(filePath);
                } catch (CsvValidationException e) {
                    throw new IOException("CSV文件格式错误: " + e.getMessage(), e);
                }
            case "xlsx":
                return convertXlsxToMarkdown(filePath);
            case "md":
                return convertMdToMarkdown(filePath);
            case "html":
                return convertHtmlToMarkdown(filePath);
            case "pptx":
                return convertPptxToMarkdown(filePath);
            default:
                throw new IOException("不支持的文件类型: " + extension);
        }
    }

    /**
     * 主转换方法（保存为文件） - 根据文件类型自动选择转换方式
     */
    public String convertToMdFile(String filePath) throws IOException {
        String extension = getFileExtension(new File(filePath).getName()).toLowerCase();

        switch (extension) {
            case "docx":
                return convertDocxToMdFile(filePath);
            case "pdf":
                return convertPdfToMdFile(filePath);
            case "txt":
                return convertTxtToMdFile(filePath);
            case "csv":
                try {
                    return convertCsvToMdFile(filePath);
                } catch (CsvValidationException e) {
                    throw new IOException("CSV文件格式错误: " + e.getMessage(), e);
                }
            case "xlsx":
                return convertXlsxToMdFile(filePath);
            case "md":
                return convertMdToMdFile(filePath);
            case "html":
                return convertHtmlToMdFile(filePath);
            case "pptx":
                return convertPptxToMdFile(filePath);
            default:
                throw new IOException("不支持的文件类型: " + extension);
        }
    }

    /**
     * DOCX转换方法 - 按文档顺序处理所有内容
     */
    public String convertDocxToMarkdown(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        XWPFDocument document = new XWPFDocument(fis);

        StringBuilder markdown = new StringBuilder();
        // 提取所有图片到临时目录
        Map<String, String> imageMapping = extractAndUploadImages(document, filePath);

        // 按文档顺序处理所有内容元素
        List<IBodyElement> bodyElements = document.getBodyElements();

        for (IBodyElement element : bodyElements) {
            String elementMarkdown = convertElementToMarkdown(element, imageMapping);
            if (!elementMarkdown.trim().isEmpty()) {
                markdown.append(elementMarkdown).append("\n\n");
            }
        }

        document.close();
        fis.close();

        return markdown.toString();
    }

    /**
     * DOCX转换并保存为文件
     */
    public String convertDocxToMdFile(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        XWPFDocument document = new XWPFDocument(fis);

        StringBuilder markdown = new StringBuilder();

        // 提取所有图片到临时目录
        Map<String, String> imageMapping = extractAndUploadImages(document, filePath);

        // 按文档顺序处理所有内容元素
        List<IBodyElement> bodyElements = document.getBodyElements();

        for (IBodyElement element : bodyElements) {
            String elementMarkdown = convertElementToMarkdown(element, imageMapping);
            if (!elementMarkdown.trim().isEmpty()) {
                markdown.append(elementMarkdown).append("\n\n");
            }
        }

        document.close();
        fis.close();

        String mdFilePath = basicProfile + "/change_md/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss) + ".md";
        FileUtil.writeBytes(markdown.toString().getBytes(),mdFilePath);
        return mdFilePath;
    }

    /**
     * 转换单个文档元素
     */
    private String convertElementToMarkdown(IBodyElement element, Map<String, String> imageMapping) {
        if (element instanceof XWPFParagraph) {
            return convertParagraphToMarkdown((XWPFParagraph) element, imageMapping);
        } else if (element instanceof XWPFTable) {
            return convertTableToMarkdown((XWPFTable) element);
        }
        return "";
    }

    /**
     * 转换段落（包含图片处理）
     */
    private String convertParagraphToMarkdown(XWPFParagraph paragraph, Map<String, String> imageMapping) {
        StringBuilder result = new StringBuilder();

        // 先提取图片
        List<String> images = extractImagesFromParagraph(paragraph, imageMapping);

        // 提取文本内容
        String textContent = extractFormattedText(paragraph);

        // 如果段落只有图片没有文本，直接返回图片
        if (textContent.trim().isEmpty() && !images.isEmpty()) {
            return String.join("\n\n", images);
        }

        // 如果段落有文本内容，先检测列表，再检测标题
        if (!textContent.trim().isEmpty()) {
            String listMarkdown = detectAndFormatList(paragraph, textContent);
            if (listMarkdown.equals(textContent)) {
                // 不是列表，检测是否为标题
                String titleMarkdown = detectAndFormatTitle(paragraph, textContent);
                result.append(titleMarkdown);
            } else {
                // 是列表，直接使用列表格式
                result.append(listMarkdown);
            }
        }

        // 添加图片（如果有的话）
        for (String imageMarkdown : images) {
            if (!result.isEmpty()) {
                result.append("\n\n");
            }
            result.append(imageMarkdown);
        }

        return result.toString();
    }

    // ➕ 新增方法 - 列表检测和格式化
    private String detectAndFormatList(XWPFParagraph paragraph, String textContent) {
        try {
            // 检查段落是否有编号属性
            if (paragraph.getCTPPr() != null && paragraph.getCTPPr().getNumPr() != null) {
                // 获取列表ID和级别
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPr numPr = paragraph.getCTPPr().getNumPr();

                if (numPr.getNumId() != null && numPr.getIlvl() != null) {
                    int listLevel = numPr.getIlvl().getVal().intValue();

                    // 生成缩进
                    String indent = "  ".repeat(listLevel); // 每级缩进2个空格

                    // 检查编号格式来判断是有序还是无序列表
                    String numFmt = paragraph.getNumFmt();

                    if (numFmt != null) {
                        // 有序列表的格式通常包含数字
                        if (isOrderedListFormat(numFmt)) {
                            // 尝试从段落文本中提取实际序号
                            String actualNumber = extractListNumber(textContent, paragraph);
                            // 检查文本内容是否已经包含序号
                            String cleanContent = removeListNumber(textContent.trim());
                            return indent + actualNumber + ". " + cleanContent;
                        } else {
                            // 无序列表
                            return indent + "- " + textContent.trim();
                        }
                    } else {
                        // 默认为无序列表
                        return indent + "- " + textContent.trim();
                    }
                }
            }

            // 通过文本内容检测列表（备用方法）
            String trimmedText = textContent.trim();

            // 检测以数字开头的有序列表 - 保持原始序号
            if (trimmedText.matches("^\\d+[.)\\s].*")) {
                // 保持原始序号，不改为1
                return trimmedText;
            }

            // 检测以符号开头的无序列表
            if (trimmedText.matches("^[•·▪▫◦‣⁃-]\\s.*")) {
                String content = trimmedText.replaceFirst("^[•·▪▫◦‣⁃-]\\s+", "");
                return "- " + content;
            }

            // 检测缩进的列表项（可能是子列表）
            if (trimmedText.startsWith("  ") || trimmedText.startsWith("\t")) {
                String content = trimmedText.trim();
                // 检查是否以列表符号开头
                if (content.matches("^[•·▪▫◦‣⁃-]\\s.*") || content.matches("^\\d+[.)\\s].*")) {
                    if (content.matches("^\\d+[.)\\s].*")) {
                        // 保持原始序号
                        return "  " + content;
                    } else {
                        content = content.replaceFirst("^[•·▪▫◦‣⁃-]\\s+", "");
                        return "  - " + content;
                    }
                }
            }

        } catch (Exception e) {
            // 如果处理过程中出现异常，记录日志但不影响主流程
            System.err.println("列表检测失败: " + e.getMessage());
        }

        // 如果不是列表，返回原文本
        return textContent;
    }

    // 提取列表项的实际序号
    private String extractListNumber(String textContent, XWPFParagraph paragraph) {
        try {
            // 方法1: 从文本内容中直接提取序号
            String trimmedText = textContent.trim();
            if (trimmedText.matches("^\\d+[.)\\s].*")) {
                // 提取开头的数字
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^(\\d+)[.)\\s]");
                java.util.regex.Matcher matcher = pattern.matcher(trimmedText);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }

            // 方法2: 检查段落中Run的内容，有时序号会单独在一个Run中
            for (org.apache.poi.xwpf.usermodel.XWPFRun run : paragraph.getRuns()) {
                String runText = run.getText(0);
                if (runText != null && runText.matches("^\\d+[.)\\s]*$")) {
                    // 找到只包含数字和标点的Run
                    String number = runText.replaceAll("[^\\d]", "");
                    if (!number.isEmpty()) {
                        return number;
                    }
                }
            }

        } catch (Exception e) {
            // 静默处理异常
        }

        // 如果无法提取到具体序号，返回默认值
        return "1";
    }

    // 移除文本开头的列表序号
    private String removeListNumber(String text) {
        if (text == null) return "";

        // 移除开头的数字序号格式 (1. 2) 3 等)
        String cleaned = text.replaceFirst("^\\d+[.)\\s]+", "");

        // 移除开头的项目符号
        cleaned = cleaned.replaceFirst("^[•·▪▫◦‣⁃-]\\s+", "");

        return cleaned.trim();
    }

    // 判断编号格式是否为有序列表
    private boolean isOrderedListFormat(String numFmt) {
        if (numFmt == null) return false;

        String lowerFmt = numFmt.toLowerCase();
        // 常见的有序列表格式
        return lowerFmt.contains("decimal") ||      // 阿拉伯数字
               lowerFmt.contains("lowerroman") ||   // 小写罗马数字
               lowerFmt.contains("upperroman") ||   // 大写罗马数字
               lowerFmt.contains("lowerletter") ||  // 小写字母
               lowerFmt.contains("upperletter") ||  // 大写字母
               lowerFmt.contains("number") ||       // 数字
               lowerFmt.matches("\\d+");            // 纯数字
    }

    // ➕ 新增方法 - 增强的标题检测
    private String detectAndFormatTitle(XWPFParagraph paragraph, String textContent) {
        String numFmt = paragraph.getNumFmt();
        // 方法1: 样式检测 - 扩展更多样式名称
        String style = paragraph.getStyle();
        if (style != null) {
            String lowerStyle = style.toLowerCase();
            // H1 标题的各种可能样式名称
            if (lowerStyle.contains("heading1") || lowerStyle.contains("标题1") ||
                lowerStyle.contains("title") || lowerStyle.contains("h1") ||
                lowerStyle.equals("1") || lowerStyle.contains("heading 1")) {
                return "# " + textContent;
            }
            // H2 标题
            else if (lowerStyle.contains("heading2") || lowerStyle.contains("标题2") ||
                     lowerStyle.contains("h2") || lowerStyle.equals("2") ||
                     lowerStyle.contains("heading 2")) {
                return "## " + textContent;
            }
            // H3 标题
            else if (lowerStyle.contains("heading3") || lowerStyle.contains("标题3") ||
                     lowerStyle.contains("h3") || lowerStyle.equals("3") ||
                     lowerStyle.contains("heading 3")) {
                return "### " + textContent;
            }
            // H4 标题
            else if (lowerStyle.contains("heading4") || lowerStyle.contains("标题4") ||
                     lowerStyle.contains("h4") || lowerStyle.equals("4") ||
                     lowerStyle.contains("heading 4")) {
                return "#### " + textContent;
            }
            // H5 标题
            else if (lowerStyle.contains("heading5") || lowerStyle.contains("标题5") ||
                     lowerStyle.contains("h5") || lowerStyle.equals("5") ||
                     lowerStyle.contains("heading 5")) {
                return "##### " + textContent;
            }
            // H6 标题
            else if (lowerStyle.contains("heading6") || lowerStyle.contains("标题6") ||
                     lowerStyle.contains("h6") || lowerStyle.equals("6") ||
                     lowerStyle.contains("heading 6")) {
                return "###### " + textContent;
            }
        }

        // 方法2: 大纲级别检测
        if (numFmt != null && !numFmt.isEmpty()) {
            try {
                int outlineLevel = Integer.parseInt(numFmt);
                if (outlineLevel >= 1 && outlineLevel <= 6) {
                    String[] headers = {"# ", "## ", "### ", "#### ", "##### ", "###### "};
                    return headers[outlineLevel - 1] + textContent;
                }
            } catch (NumberFormatException e) {
                // numFmt 不是数字，继续其他检测方法
                System.out.println("numFmt 不是数字: " + numFmt);
            }
        }

        // 方法3: 格式检测（字体大小、加粗）- 改进阈值
        boolean isBold = false;
        boolean hasLargeFont = false;
        int maxFontSize = 0;
        int totalRuns = 0;
        int boldRuns = 0;

        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText != null && !runText.trim().isEmpty()) {
                totalRuns++;
                if (run.isBold()) {
                    isBold = true;
                    boldRuns++;
                }

                int fontSize = run.getFontSize();
                if (fontSize > 0) {
                    maxFontSize = Math.max(maxFontSize, fontSize);
                    if (fontSize >= 14) {
                        hasLargeFont = true;
                    }
                }
            }
        }

        // 判断是否为标题的条件：
        // 1. 全部文本都是粗体，或者大部分文本是粗体
        // 2. 字体大小超过阈值
        // 3. 文本长度适中（不太长，像是标题）
        boolean isMostlyBold = totalRuns > 0 && (boldRuns * 1.0 / totalRuns) >= 0.7;
        boolean isReasonableLength = textContent.length() <= 100; // 标题通常不会太长

        if ((isBold || isMostlyBold || hasLargeFont) && isReasonableLength) {
            // 根据字体大小和粗体程度判断标题级别
            if (maxFontSize >= 22 || (maxFontSize >= 18 && isMostlyBold)) {
                return "# " + textContent;
            } else if (maxFontSize >= 18 || (maxFontSize >= 16 && isMostlyBold)) {
                return "## " + textContent;
            } else if (maxFontSize >= 16 || (maxFontSize >= 14 && isMostlyBold)) {
                return "### " + textContent;
            } else if (maxFontSize >= 14 || isBold) {
                return "#### " + textContent;
            }
        }

        // 方法4: 段落位置检测 - 如果是文档开头的粗体大字，可能是标题
        if (isBold && textContent.length() <= 50) {
            return "### " + textContent; // 默认为三级标题
        }

        return textContent; // 不是标题，返回原文本
    }

    /**
     * 从段落中提取图片并上传
     */
    private List<String> extractImagesFromParagraph(XWPFParagraph paragraph, Map<String, String> imageMapping) {
        List<String> images = new ArrayList<>();

        for (XWPFRun run : paragraph.getRuns()) {
            List<XWPFPicture> pictures = run.getEmbeddedPictures();

            for (XWPFPicture picture : pictures) {
                try {
                    String imageMarkdown = processImage(picture, imageMapping);
                    if (imageMarkdown != null) {
                        images.add(imageMarkdown);
                    }
                } catch (Exception e) {
                    System.err.println("处理图片失败: " + e.getMessage());
                    // 添加占位符
                    images.add("![图片加载失败](image_error.png)");
                }
            }
        }

        return images;
    }

    /**
     * 处理单个图片
     */
    private String processImage(XWPFPicture picture, Map<String, String> imageMapping) throws IOException {
        // 获取图片数据
        XWPFPictureData pictureData = picture.getPictureData();
        byte[] imageBytes = pictureData.getData();

        // 生成唯一文件名
        String originalFileName = pictureData.getFileName();
        String extension = getFileExtension(originalFileName);
        String uniqueFileName = generateUniqueImageName(extension);

        // 上传到OSS
        String ossKey = imageUploadPath + uniqueFileName;
        ossService.putObject(imageBytes, ossKey);

        // 获取访问URL
        String imageUrl = ossService.getHttpUrl(ossKey);

        // 生成Markdown图片语法
        String alt = "图片"; // 可以从图片属性中获取更多信息
        return String.format("![%s](%s)", alt, imageUrl);
    }

    /**
     * 提取并上传所有图片
     */
    private Map<String, String> extractAndUploadImages(XWPFDocument document, String docFilePath) {
        Map<String, String> imageMapping = new HashMap<>();

        try {
            // 获取文档中的所有图片
            List<XWPFPictureData> pictures = document.getAllPictures();

            for (int i = 0; i < pictures.size(); i++) {
                XWPFPictureData pictureData = pictures.get(i);

                // 生成唯一文件名
                String originalFileName = pictureData.getFileName();
                String extension = getFileExtension(originalFileName);
                String uniqueFileName = generateUniqueImageName(extension);

                // 上传图片
                byte[] imageBytes = pictureData.getData();
                String ossKey = imageUploadPath + uniqueFileName;

                ossService.putObject(imageBytes,ossKey);
                String imageUrl = ossService.getHttpUrl(ossKey);

                // 建立映射关系
                imageMapping.put(pictureData.getChecksum() + "", imageUrl);

                System.out.println("图片已上传: " + originalFileName + " -> " + imageUrl);
            }

        } catch (Exception e) {
            System.err.println("提取图片失败: " + e.getMessage());
        }

        return imageMapping;
    }

    /**
     * 提取带格式的文本
     */
    private String extractFormattedText(XWPFParagraph paragraph) {
        StringBuilder text = new StringBuilder();

        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText == null) continue;

            // 跳过图片的占位文本
            if (run.getEmbeddedPictures().size() > 0) {
                continue;
            }

            // 处理文本格式
            if (run.isBold() && run.isItalic()) {
                runText = "***" + runText + "***";
            } else if (run.isBold()) {
                runText = "**" + runText + "**";
            } else if (run.isItalic()) {
                runText = "*" + runText + "*";
            }

            // 处理下划线
            if (run.getUnderline() != UnderlinePatterns.NONE) {
                runText = "<u>" + runText + "</u>";
            }

            text.append(runText);
        }

        return text.toString();
    }

    /**
     * 转换表格到Markdown（保持原有方法）
     */
    private String convertTableToMarkdown(XWPFTable table) {
        if (table == null || table.getRows().isEmpty()) {
            return "";
        }

        StringBuilder markdown = new StringBuilder();
        List<XWPFTableRow> rows = table.getRows();

        // 分析表格结构
        int maxColumns = getMaxColumns(rows);

        // 生成表格
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            XWPFTableRow row = rows.get(rowIndex);
            List<String> cellContents = extractRowCells(row, maxColumns);

            // 生成表格行
            markdown.append("|");
            for (String cellContent : cellContents) {
                String cleanContent = cleanCellContent(cellContent);
                markdown.append(" ").append(cleanContent).append(" |");
            }
            markdown.append("\n");

            // 在第一行后添加分隔符
            if (rowIndex == 0) {
                markdown.append("|");
                for (int i = 0; i < maxColumns; i++) {
                    markdown.append(" --- |");
                }
                markdown.append("\n");
            }
        }

        return markdown.toString();
    }

    /**
     * 获取表格最大列数
     */
    private int getMaxColumns(List<XWPFTableRow> rows) {
        int maxColumns = 0;
        for (XWPFTableRow row : rows) {
            maxColumns = Math.max(maxColumns, row.getTableCells().size());
        }
        return maxColumns;
    }

    /**
     * 提取行中的单元格内容
     */
    private List<String> extractRowCells(XWPFTableRow row, int maxColumns) {
        List<String> cellContents = new ArrayList<>();
        List<XWPFTableCell> cells = row.getTableCells();

        for (int i = 0; i < maxColumns; i++) {
            if (i < cells.size()) {
                String cellContent = extractCellContent(cells.get(i));
                cellContents.add(cellContent);
            } else {
                cellContents.add("");
            }
        }

        return cellContents;
    }

    /**
     * 提取单元格内容
     */
    private String extractCellContent(XWPFTableCell cell) {
        StringBuilder content = new StringBuilder();

        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            String paragraphText = extractFormattedText(paragraph);
            if (!paragraphText.trim().isEmpty()) {
                if (content.length() > 0) {
                    content.append("<br>");
                }
                content.append(paragraphText.trim());
            }
        }

        return content.toString();
    }

    /**
     * 清理单元格内容
     */
    private String cleanCellContent(String content) {
        if (content == null) return "";

        content = content.trim().replaceAll("\\s+", " ");
        content = content.replace("|", "\\|");
        content = content.replace("\n", "<br>");
        content = content.replace("\r", "");

        return content.isEmpty() ? "&nbsp;" : content;
    }

    /**
     * 生成唯一图片文件名
     */
    private String generateUniqueImageName(String extension) {
        return System.currentTimeMillis() + "_" + IdUtil.fastSimpleUUID() + "." + extension;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "png"; // 默认扩展名
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }

        return "png"; // 默认扩展名
    }

    /**
     * 获取文件名（不含扩展名）
     */
    private String getFileName(String filePath) {
        String fileName = new File(filePath).getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }

    // ========================= PDF 处理方法 =========================

    /**
     * PDF转换为Markdown
     */
    public String convertPdfToMarkdown(String filePath) throws IOException {
        PDDocument document = PDDocument.load(new File(filePath));
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    /**
     * PDF转换并保存为文件
     */
    public String convertPdfToMdFile(String filePath) throws IOException {
        String markdown = convertPdfToMarkdown(filePath);
        String mdFilePath = basicProfile + "/change_md/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss) + ".md";
        FileUtil.writeBytes(markdown.getBytes(), mdFilePath);
        return mdFilePath;
    }

    // ========================= TXT 处理方法 =========================

    /**
     * TXT转换为Markdown
     */
    public String convertTxtToMarkdown(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

    /**
     * TXT转换并保存为文件
     */
    public String convertTxtToMdFile(String filePath) throws IOException {
        String markdown = convertTxtToMarkdown(filePath);
        String mdFilePath = basicProfile + "/change_md/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss) + ".md";
        FileUtil.writeBytes(markdown.getBytes(), mdFilePath);
        return mdFilePath;
    }

    // ========================= CSV 处理方法 =========================

    /**
     * CSV转换为Markdown表格
     */
    public String convertCsvToMarkdown(String filePath) throws IOException, CsvValidationException {
        StringBuilder markdown = new StringBuilder();

        try (FileReader fileReader = new FileReader(filePath);
             CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {

            String[] headers = csvReader.readNext();
            if (headers != null) {
                // 表头
                markdown.append("|");
                for (String header : headers) {
                    markdown.append(" ").append(header.trim()).append(" |");
                }
                markdown.append("\n");

                // 分隔线
                markdown.append("|");
                for (int i = 0; i < headers.length; i++) {
                    markdown.append(" --- |");
                }
                markdown.append("\n");

                // 数据行
                String[] row;
                while ((row = csvReader.readNext()) != null) {
                    markdown.append("|");
                    for (int i = 0; i < headers.length; i++) {
                        String cell = i < row.length ? row[i].trim() : "";
                        markdown.append(" ").append(cell).append(" |");
                    }
                    markdown.append("\n");
                }
            }
        }

        return markdown.toString();
    }

    /**
     * CSV转换并保存为文件
     */
    public String convertCsvToMdFile(String filePath) throws IOException, CsvValidationException {
        String markdown = convertCsvToMarkdown(filePath);
        String mdFilePath = basicProfile + "/change_md/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss) + ".md";
        FileUtil.writeBytes(markdown.getBytes(), mdFilePath);
        return mdFilePath;
    }

    // ========================= XLSX 处理方法 =========================

    /**
     * XLSX转换为Markdown表格
     */
    public String convertXlsxToMarkdown(String filePath) throws IOException {
        StringBuilder markdown = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                
                // 处理表格数据
                boolean isFirstRow = true;
                for (Row row : sheet) {
                    if (isRowEmpty(row)) continue;

                    markdown.append("|");
                    for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex);
                        String cellValue = getCellValue(cell);
                        markdown.append(" ").append(cellValue).append(" |");
                    }
                    markdown.append("\n");

                    // 第一行后添加分隔符
                    if (isFirstRow) {
                        markdown.append("|");
                        for (int i = 0; i < row.getLastCellNum(); i++) {
                            markdown.append(" --- |");
                        }
                        markdown.append("\n");
                        isFirstRow = false;
                    }
                }
                markdown.append("\n"); // sheet之间空行分隔
            }
        }

        return markdown.toString();
    }

    /**
     * XLSX转换并保存为文件
     */
    public String convertXlsxToMdFile(String filePath) throws IOException {
        String markdown = convertXlsxToMarkdown(filePath);
        String mdFilePath = basicProfile + "/change_md/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss) + ".md";
        FileUtil.writeBytes(markdown.getBytes(), mdFilePath);
        return mdFilePath;
    }

    /**
     * 检查行是否为空
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && !getCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取单元格值
     */
    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        String value;
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue().toString();
                } else {
                    // 避免 .0
                    double val = cell.getNumericCellValue();
                    if (val == (long) val) {
                        value = String.format("%d", (long) val);
                    } else {
                        value = String.valueOf(val);
                    }
                }
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                try {
                    value = cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    value = String.valueOf(cell.getNumericCellValue());
                }
                break;
            default:
                value = "";
        }
        
        // 清理特殊字符，防止破坏 Markdown 表格结构
        return value.replace("|", "\\|").replace("\n", "<br>").replace("\r", "");
    }

    // ========================= MD 处理方法 =========================

    /**
     * MD文件直接读取
     */
    public String convertMdToMarkdown(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

    /**
     * MD转换并保存为文件（实际就是复制）
     */
    public String convertMdToMdFile(String filePath) throws IOException {
        String markdown = convertMdToMarkdown(filePath);
        String mdFilePath = basicProfile + "/change_md/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss) + ".md";
        FileUtil.writeBytes(markdown.getBytes(), mdFilePath);
        return mdFilePath;
    }

    // ========================= HTML 处理方法 =========================

    /**
     * HTML转换为Markdown
     */
    public String convertHtmlToMarkdown(String filePath) throws IOException {
        String html = Files.readString(Paths.get(filePath));
        Document doc = Jsoup.parse(html);

        StringBuilder markdown = new StringBuilder();

        // 转换标题
        for (int i = 1; i <= 6; i++) {
            Elements headers = doc.select("h" + i);
            for (Element header : headers) {
                String prefix = "#".repeat(i);
                header.before("\\n" + prefix + " " + header.text() + "\\n\\n");
                header.remove();
            }
        }

        // 转换段落
        Elements paragraphs = doc.select("p");
        for (Element p : paragraphs) {
            p.before("\\n" + p.text() + "\\n\\n");
        }

        // 转换表格
        Elements tables = doc.select("table");
        for (Element table : tables) {
            StringBuilder tableMarkdown = new StringBuilder();
            Elements rows = table.select("tr");

            boolean isFirstRow = true;
            for (Element row : rows) {
                Elements cells = row.select("td, th");

                tableMarkdown.append("|");
                for (Element cell : cells) {
                    tableMarkdown.append(" ").append(cell.text().replace("|", "\\|")).append(" |");
                }
                tableMarkdown.append("\\n");

                if (isFirstRow) {
                    tableMarkdown.append("|");
                    for (int i = 0; i < cells.size(); i++) {
                        tableMarkdown.append(" --- |");
                    }
                    tableMarkdown.append("\\n");
                    isFirstRow = false;
                }
            }

            table.before("\\n" + tableMarkdown.toString() + "\\n");
            table.remove();
        }

        // 转换其他元素
        Elements bolds = doc.select("b, strong");
        for (Element bold : bolds) {
            bold.before("**" + bold.text() + "**");
            bold.remove();
        }

        Elements italics = doc.select("i, em");
        for (Element italic : italics) {
            italic.before("*" + italic.text() + "*");
            italic.remove();
        }

        // 移除HTML标签，保留文本
        markdown.append(doc.text());

        return markdown.toString().replaceAll("\\\\n", "\n");
    }

    /**
     * HTML转换并保存为文件
     */
    public String convertHtmlToMdFile(String filePath) throws IOException {
        String markdown = convertHtmlToMarkdown(filePath);
        String mdFilePath = basicProfile + "/change_md/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss) + ".md";
        FileUtil.writeBytes(markdown.getBytes(), mdFilePath);
        return mdFilePath;
    }

    // ========================= PPTX 处理方法 =========================

    /**
     * PPTX转换为Markdown
     */
    public String convertPptxToMarkdown(String filePath) throws IOException {
        StringBuilder markdown = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(filePath);
             XMLSlideShow ppt = new XMLSlideShow(fis)) {

            int slideNumber = 1;
            for (XSLFSlide slide : ppt.getSlides()) {
                // 添加幻灯片标题
                markdown.append("## 幻灯片 ").append(slideNumber++).append("\n\n");

                // 提取幻灯片中的文本
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        String text = textShape.getText();
                        if (text != null && !text.trim().isEmpty()) {
                            markdown.append(text.trim()).append("\n\n");
                        }
                    }
                }
            }
        }

        return markdown.toString();
    }

    /**
     * PPTX转换并保存为文件
     */
    public String convertPptxToMdFile(String filePath) throws IOException {
        String markdown = convertPptxToMarkdown(filePath);
        String mdFilePath = basicProfile + "/change_md/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss) + ".md";
        FileUtil.writeBytes(markdown.getBytes(), mdFilePath);
        return mdFilePath;
    }
}
