package com.wudao.kms.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.unit.DataUnit;
import com.wudao.common.utils.DateFormats;
import com.wudao.kms.entity.KnowledgeFile;
import com.wudao.oss.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Service;
import oshi.software.os.OSService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 封面生成服务
 * 支持PDF、Word、文本等文件类型的封面生成
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CoverGenerationService {

    private final OssService ossService;
    
    private static final int COVER_WIDTH = 200;
    private static final int COVER_HEIGHT = 200;
    private static final String COVER_FORMAT = "PNG";
    
    // 中文字体候选列表
    private static final List<String> CHINESE_FONTS = Arrays.asList(
        "Microsoft YaHei",     // 微软雅黑
        "SimHei",              // 黑体
        "SimSun",              // 宋体
        "KaiTi",               // 楷体
        "FangSong",            // 仿宋
        "STHeiti",             // 华文黑体 (Mac)
        "STSong",              // 华文宋体 (Mac)
        "PingFang SC",         // 苹方 (Mac)
        "Hiragino Sans GB",    // 冬青黑体 (Mac)
        "WenQuanYi Micro Hei", // 文泉驿微米黑 (Linux)
        "Noto Sans CJK SC",    // Noto字体 (Linux)
        "Source Han Sans CN",  // 思源黑体
        "Dialog",              // Java默认字体
        "SansSerif"            // 系统无衬线字体
    );
    
    /**
     * 生成文件封面并上传到S3
     * @param knowledgeFile 知识文件对象
     * @return 封面S3路径
     */
    public String generateCover(KnowledgeFile knowledgeFile) {
        return generateCover(knowledgeFile, null);
    }

    /**
     * 生成文件封面并上传到S3（支持extraMd内容）
     * @param knowledgeFile 知识文件对象
     * @param extraMd 额外的markdown内容，对于非多模态格式使用此内容的前50个字生成封面
     * @return 封面S3路径
     */
    public String generateCover(KnowledgeFile knowledgeFile, String extraMd) {
        try {
            byte[] coverBytes = null;
            String fileType = FileUtil.getSuffix(knowledgeFile.getFileName());
            
            String uploadFormat = "png"; // 默认格式
            
            switch (fileType) {
                case "pdf":
                    coverBytes = generatePdfCover(knowledgeFile.getFilePath());
                    break;
                case "doc":
                case "docx":
                    coverBytes = generateWordCover(knowledgeFile.getFilePath());
                    break;
                case "txt":
                case "md":
                    // 对于非多模态格式，优先使用extraMd的前50个字
                    String textContent = getTextContentForCover(FileUtil.readUtf8String(extraMd), knowledgeFile.getContent());
                    coverBytes = generateTextCover(textContent);
                    break;
                case "png":
                case "jpg":
                case "jpeg":
                case "gif":
                case "bmp":
                case "webp":
                    // 对于图片文件，生成JPEG格式封面
                    coverBytes = generateImageCover(knowledgeFile.getFilePath());
                    uploadFormat = "jpeg";
                    break;
                default:
                    String fileName = FileUtil.getName(knowledgeFile.getFileName());
                    // 对于其他非多模态格式，也尝试使用extraMd内容
                    coverBytes = generateDefaultCover(fileName);
                    break;
            }
            
            if (coverBytes != null) {
                String coverPath = "cover/" + LocalDate.now().format(DateFormats.yyyyMMdd) + "/" + LocalTime.now().format(DateFormats.STANDARD_TIME) + FileUtil.getName(knowledgeFile.getFileName()) + ".png";
                 ossService.putObject(coverBytes, coverPath);
                log.info("封面生成成功，文件: {}, 路径: {}", knowledgeFile.getFileName(), coverPath);
                return ossService.getHttpUrl(coverPath);
            }
            
        } catch (Exception e) {
            log.error("封面生成失败，文件: {}", knowledgeFile.getFileName(), e);
        }
        
        return null;
    }
    
    /**
     * 生成PDF文件封面（第一页缩略图）
     */
    private byte[] generatePdfCover(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("PDF文件不存在: " + filePath);
        }
        
        try (PDDocument document = PDDocument.load(file)) {
            if (document.getNumberOfPages() == 0) {
                throw new IOException("PDF文件为空");
            }
            
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 150);
            
            // 调整图片大小
            BufferedImage resizedImage = resizeImage(image, COVER_WIDTH, COVER_HEIGHT);
            
            return imageToBytes(resizedImage);
        }
    }
    
    /**
     * 生成Word文件封面
     */
    private byte[] generateWordCover(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Word文件不存在: " + filePath);
        }
        
        try (FileInputStream fis = new FileInputStream(file)) {
            if (filePath.toLowerCase().endsWith(".docx")) {
                return generateDocxCover(fis);
            } else {
                // 对于.doc文件，生成默认封面
                return generateDefaultCover(file.getName());
            }
        }
    }
    
    /**
     * 生成DOCX文件封面
     */
    private byte[] generateDocxCover(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            // 创建封面画布
            BufferedImage coverImage = new BufferedImage(COVER_WIDTH, COVER_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = coverImage.createGraphics();
            
            // 设置背景色
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, COVER_WIDTH, COVER_HEIGHT);
            
            // 启用抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            int currentY = 20;
            boolean hasContent = false;
            
            // 处理文档的前几段
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                if (currentY >= COVER_HEIGHT - 20) break; // 避免超出边界
                
                // 检查段落中是否有图片
                for (XWPFRun run : paragraph.getRuns()) {
                    if (run.getEmbeddedPictures() != null && !run.getEmbeddedPictures().isEmpty()) {
                        // 处理图片
                        for (XWPFPicture picture : run.getEmbeddedPictures()) {
                            try {
                                byte[] imageData = picture.getPictureData().getData();
                                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
                                
                                if (image != null) {
                                    // 计算图片缩放尺寸
                                    int maxWidth = COVER_WIDTH - 40;
                                    int maxHeight = COVER_HEIGHT - currentY - 40;
                                    
                                    double scale = Math.min(
                                        (double) maxWidth / image.getWidth(),
                                        (double) maxHeight / image.getHeight()
                                    );
                                    
                                    if (scale > 1) scale = 1; // 不放大
                                    
                                    int scaledWidth = (int) (image.getWidth() * scale);
                                    int scaledHeight = (int) (image.getHeight() * scale);
                                    
                                    // 居中绘制图片
                                    int x = (COVER_WIDTH - scaledWidth) / 2;
                                    g2d.drawImage(image, x, currentY, scaledWidth, scaledHeight, null);
                                    
                                    currentY += scaledHeight + 10;
                                    hasContent = true;
                                }
                            } catch (Exception e) {
                                log.warn("处理DOCX图片失败", e);
                            }
                        }
                    }
                }
                
                // 处理文本
                String text = paragraph.getText();
                if (text != null && !text.trim().isEmpty()) {
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(getChineseFont(Font.PLAIN, 12));
                    
                    currentY = drawWrappedText(g2d, text, 20, currentY, COVER_WIDTH - 40, COVER_HEIGHT - currentY - 20);
                    currentY += 5; // 段落间距
                    hasContent = true;
                }
                
                if (currentY >= COVER_HEIGHT - 20) break;
            }
            
            // 如果没有内容，显示默认文本
            if (!hasContent) {
                g2d.setColor(Color.GRAY);
                g2d.setFont(getChineseFont(Font.PLAIN, 14));
                String defaultText = "Word文档";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (COVER_WIDTH - fm.stringWidth(defaultText)) / 2;
                int y = COVER_HEIGHT / 2;
                g2d.drawString(defaultText, x, y);
            }
            
            g2d.dispose();
            return imageToBytes(coverImage);
        }
    }
    
    /**
     * 生成文本文件封面（前500字）
     */
    private byte[] generateTextCover(String content) throws IOException {
        if (content == null || content.trim().isEmpty()) {
            return generateDefaultCover("文本文件");
        }
        
        String text = content.length() > 500 ? content.substring(0, 500) + "..." : content;
        return generateTextImageCover(text);
    }
    
    /**
     * 生成文本图片封面
     */
    private byte[] generateTextImageCover(String text) throws IOException {
        BufferedImage image = new BufferedImage(COVER_WIDTH, COVER_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置背景色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, COVER_WIDTH, COVER_HEIGHT);
        
        // 设置文字颜色和字体
        g2d.setColor(Color.BLACK);
        g2d.setFont(getChineseFont(Font.PLAIN, 12));
        
        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 绘制文本
        int finalY = drawWrappedText(g2d, text, 10, 20, COVER_WIDTH - 20, COVER_HEIGHT - 20);
        
        g2d.dispose();
        
        return imageToBytes(image);
    }

    /**
     * 生成图片封面（将各种格式的图片转成JPEG格式）
     * @param filePath 图片文件路径
     * @return JPEG格式的二进制数据
     */
    public byte[] generateImageCover(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("图片文件不存在: " + filePath);
        }
        
        try {
            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(file);
            if (originalImage == null) {
                throw new IOException("无法读取图片文件: " + filePath);
            }
            
            // 调整图片尺寸
            BufferedImage resizedImage = resizeImage(originalImage, COVER_WIDTH, COVER_HEIGHT);
            
            // 处理透明背景（针对PNG等格式）
            BufferedImage jpegImage = convertToJpeg(resizedImage);
            
            // 转换为JPEG格式的二进制数据
            return convertImageToJpegBytes(jpegImage);
            
        } catch (Exception e) {
            log.error("处理图片文件失败: {}", filePath, e);
            throw new IOException("图片处理失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将图片转换为JPEG格式（处理透明背景）
     */
    private BufferedImage convertToJpeg(BufferedImage originalImage) {
        // 如果图片已经是RGB格式，直接返回
        if (originalImage.getType() == BufferedImage.TYPE_INT_RGB) {
            return originalImage;
        }
        
        // 创建新的RGB图片
        BufferedImage jpegImage = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(), 
            BufferedImage.TYPE_INT_RGB
        );
        
        Graphics2D g2d = jpegImage.createGraphics();
        
        // 设置白色背景（处理透明背景）
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, originalImage.getWidth(), originalImage.getHeight());
        
        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制原图片到新图片上
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();
        
        return jpegImage;
    }
    
    /**
     * 将BufferedImage转换为JPEG格式的字节数组
     */
    private byte[] convertImageToJpegBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // 使用JPEG格式输出，并设置质量
        javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("JPEG").next();
        javax.imageio.ImageWriteParam param = writer.getDefaultWriteParam();
        
        // 设置JPEG压缩质量（0.9表示90%质量）
        if (param.canWriteCompressed()) {
            param.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.9f);
        }
        
        javax.imageio.stream.ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);
        writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
        
        writer.dispose();
        ios.close();
        
        return baos.toByteArray();
    }
    
    /**
     * 生成默认封面
     */
    private byte[] generateDefaultCover(String fileName) throws IOException {
        BufferedImage image = new BufferedImage(COVER_WIDTH, COVER_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置背景色（浅灰色）
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, COVER_WIDTH, COVER_HEIGHT);
        
        // 设置边框
        g2d.setColor(Color.GRAY);
        g2d.drawRect(0, 0, COVER_WIDTH - 1, COVER_HEIGHT - 1);
        
        // 设置文字颜色和字体
        g2d.setColor(Color.BLACK);
        g2d.setFont(getChineseFont(Font.BOLD, 14));
        
        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 绘制文件名
        String displayName = fileName.length() > 20 ? fileName.substring(0, 20) + "..." : fileName;
        FontMetrics fm = g2d.getFontMetrics();
        int x = (COVER_WIDTH - fm.stringWidth(displayName)) / 2;
        int y = COVER_HEIGHT / 2;
        g2d.drawString(displayName, x, y);
        
        g2d.dispose();
        
        return imageToBytes(image);
    }
    
    /**
     * 绘制自动换行文本
     * @return 绘制完成后的Y坐标
     */
    private int drawWrappedText(Graphics2D g2d, String text, int x, int y, int width, int height) {
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        int currentY = y;
        
        // 对于中文文本，按字符换行而不是按单词
        StringBuilder currentLine = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            
            // 处理换行符
            if (c == '\n') {
                if (currentLine.length() > 0) {
                    g2d.drawString(currentLine.toString(), x, currentY);
                    currentY += lineHeight;
                    currentLine = new StringBuilder();
                }
                continue;
            }
            
            String testLine = currentLine.toString() + c;
            
            if (fm.stringWidth(testLine) <= width) {
                currentLine.append(c);
            } else {
                // 当前行已满，绘制并开始新行
                if (currentLine.length() > 0) {
                    g2d.drawString(currentLine.toString(), x, currentY);
                    currentY += lineHeight;
                    currentLine = new StringBuilder().append(c);
                } else {
                    // 单个字符就超出宽度的情况（极少见）
                    g2d.drawString(String.valueOf(c), x, currentY);
                    currentY += lineHeight;
                }
            }
            
            // 检查是否超出高度
            if (currentY + lineHeight > y + height) {
                break;
            }
        }
        
        // 绘制最后一行
        if (currentLine.length() > 0 && currentY + lineHeight <= y + height) {
            g2d.drawString(currentLine.toString(), x, currentY);
            currentY += lineHeight;
        }
        
        return currentY;
    }
    
    /**
     * 调整图片大小
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        
        return resizedImage;
    }
    
    /**
     * 将图片转换为字节数组
     */
    private byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, COVER_FORMAT, baos);
        return baos.toByteArray();
    }
    
    /**
     * 获取用于封面生成的文本内容
     * 对于非多模态格式，优先使用extraMd的前50个字符
     *
     * @param extraMd 额外的markdown内容
     * @param fallbackContent 备用内容
     * @return 用于封面的文本内容
     */
    private String getTextContentForCover(String extraMd, String fallbackContent) {
        // 如果有extraMd内容且不为空，使用前50个字符
        if (extraMd != null && !extraMd.trim().isEmpty()) {
            String cleanedExtraMd = extraMd.trim();
            // 移除markdown语法标记，只保留纯文本
            cleanedExtraMd = cleanedExtraMd.replaceAll("#{1,6}\\s*", ""); // 移除标题标记
            cleanedExtraMd = cleanedExtraMd.replaceAll("\\*\\*(.+?)\\*\\*", "$1"); // 移除加粗标记
            cleanedExtraMd = cleanedExtraMd.replaceAll("\\*(.+?)\\*", "$1"); // 移除斜体标记
            cleanedExtraMd = cleanedExtraMd.replaceAll("`(.+?)`", "$1"); // 移除行内代码标记
            cleanedExtraMd = cleanedExtraMd.replaceAll("\\[(.+?)\\]\\(.+?\\)", "$1"); // 移除链接，保留文本
            cleanedExtraMd = cleanedExtraMd.replaceAll("!\\[.*?\\]\\(.+?\\)", ""); // 移除图片
            cleanedExtraMd = cleanedExtraMd.replaceAll("```[\\s\\S]*?```", ""); // 移除代码块
            cleanedExtraMd = cleanedExtraMd.replaceAll("\\n+", " "); // 将换行替换为空格
            cleanedExtraMd = cleanedExtraMd.trim();

            if (!cleanedExtraMd.isEmpty()) {
                // 取前50个字符
                if (cleanedExtraMd.length() > 50) {
                    return cleanedExtraMd.substring(0, 50) + "...";
                } else {
                    return cleanedExtraMd;
                }
            }
        }

        // 如果extraMd为空或处理后为空，使用备用内容
        return fallbackContent;
    }

    /**
     * 获取支持中文的字体
     * 按优先级尝试不同的中文字体，返回第一个可用的字体
     */
    private Font getChineseFont(int style, int size) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        // 转换为列表以便查找
        List<String> systemFonts = Arrays.asList(availableFonts);
        
        // 按优先级查找可用的中文字体
        for (String fontName : CHINESE_FONTS) {
            if (systemFonts.contains(fontName)) {
                Font font = new Font(fontName, style, size);
                // 验证字体是否能正确显示中文
                if (font.canDisplay('中') && font.canDisplay('文')) {
                    log.debug("使用字体: {}", fontName);
                    return font;
                }
            }
        }
        
        // 如果没有找到合适的字体，使用系统默认字体
        Font defaultFont = new Font(Font.SANS_SERIF, style, size);
        log.warn("未找到合适的中文字体，使用系统默认字体: {}", defaultFont.getFontName());
        return defaultFont;
    }
}