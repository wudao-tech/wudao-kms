package com.wudao.kms.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import com.wudao.common.utils.DateFormats;
import com.wudao.common.utils.file.ImageUtil;
import com.wudao.kms.dto.DocumentContentResult;
import com.wudao.kms.dto.DocumentContentResult.DocumentImage;
import com.wudao.kms.dto.DocumentContentResult.DocumentSegment;
import com.wudao.oss.service.OssService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件内容提取服务
 * 用于从不同类型的文件中提取文本内容和图片
 */
@Slf4j
@Service
public class FileContentExtractorService {

    @Resource
    private OssService ossService;

    @Resource
    private JavaDocumentSegmentService javaDocumentSegmentService;

    /**
     * 从本地文件提取文档内容和图片
     */
    public String extractContentFromLocalFileContent(String filePath) {
        try {
            Path path = Paths.get(filePath);
            String fileName = path.getFileName().toString().toLowerCase();

            if (fileName.endsWith(".txt") || fileName.endsWith(".md")) {
                return Files.readString(path, StandardCharsets.UTF_8);
            } else if (fileName.endsWith(".pdf")) {
                return extractPdfContent(Files.newInputStream(path)).getRawContent();
            } else if (fileName.endsWith(".docx")) {
                return extractDocxContent(Files.newInputStream(path));
            } else if (fileName.endsWith(".doc")) {
                return extractDocxContent(Files.newInputStream(path));
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")
                    || fileName.endsWith(".mp3") || fileName.endsWith(".mp4")) {
                return extractMediaContent(filePath);
            } else {
                log.warn("文件类型 {} 暂时按文本文件处理", fileName);
                return Files.readString(path, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("提取文件内容失败: {}", filePath, e);
            throw new RuntimeException("提取文件内容失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("提取文件内容失败: {}", filePath, e);
            throw new RuntimeException("提取文件内容失败: " + e.getMessage());
        }
    }

    /**
     * 提取PDF文档内容
     */
    public DocumentContentResult extractPdfContent(InputStream inputStream) throws IOException {
        DocumentContentResult result = new DocumentContentResult();
        StringBuilder fullText = new StringBuilder();
        Map<String, String> imageCache = new HashMap<>();
        List<DocumentSegment> segments = new ArrayList<>();

        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();

            // 逐页提取文本和图片
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                int pageNumber = pageIndex + 1;

                // 提取当前页文本
                stripper.setStartPage(pageNumber);
                stripper.setEndPage(pageNumber);
                String pageText = stripper.getText(document);

                // 提取当前页图片
                PDPage page = document.getPage(pageIndex);
                PDResources resources = page.getResources();

                // 处理图片
                if (resources != null) {
                    for (org.apache.pdfbox.cos.COSName name : resources.getXObjectNames()) {
                        PDXObject xObject = resources.getXObject(name);
                        if (xObject instanceof PDImageXObject) {
                            PDImageXObject imageObject = (PDImageXObject) xObject;
                            BufferedImage bufferedImage = imageObject.getImage();

                            // 将图片转换为字节数组
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "PNG", baos);
                            byte[] imageData = baos.toByteArray();
                            if (imageData.length == 0) {
                                continue;
                            }

                            // 使用MD5作为图片的唯一标识
                            String imageHash = DigestUtils.md5Hex(imageData);
                            String url;

                            // 检查图片是否已经上传过
                            if (imageCache.containsKey(imageHash)) {
                                url = imageCache.get(imageHash);
                            } else {
                                String imageName = "img_" + imageHash + ".png";
                                url = uploadImageToS3AndGetUrl(imageData, imageName);
                                imageCache.put(imageHash, url);
                            }

                            // 在页面文本的适当位置插入图片标签
                            String imgTag = String.format("<img src=\"%s\" alt=\"page_%d_img_%s\"/>", url, pageNumber, imageHash);

                            // 在每个段落之间插入图片
                            if (pageText.trim().isEmpty()) {
                                pageText = imgTag;
                            } else {
                                pageText += "\n" + imgTag + "\n";
                            }
                        }
                    }
                }

                if (!pageText.trim().isEmpty()) {
                    fullText.append(pageText);

                    // 按段落分割文本
                    String[] paragraphs = pageText.split("\\n\\s*\\n");
                    for (String paragraph : paragraphs) {
                        String trimmedParagraph = paragraph.trim();
                        if (!trimmedParagraph.isEmpty()) {
                            DocumentSegment segment = new DocumentSegment();
                            segment.setIndex(segments.size());
                            segment.setContent(trimmedParagraph);
                            segment.setType(trimmedParagraph.startsWith("<img") ? "image" : "text");
                            segment.setLength(trimmedParagraph.length());
                            segment.setPageNumber(pageNumber);
                            segments.add(segment);
                        }
                    }
                }
            }

            result.setSegments(segments);
            result.setRawContent(fullText.toString());
            result.setTotalCharacters(fullText.length());
            // 由于图片已经内嵌在文本中，这里可以使用空列表
            result.setImages(new ArrayList<>());
        }

        return result;
    }

    public String extractDocxFast(InputStream inputStream) throws Exception {
        StringBuilder fullText = new StringBuilder();
        Map<String, String> imageCache = new HashMap<>();

        try (XWPFDocument doc = new XWPFDocument(inputStream)) {
            for (XWPFParagraph para : doc.getParagraphs()) {
                StringBuilder paraText = new StringBuilder();

                for (XWPFRun run : para.getRuns()) {
                    String text = run.text();
                    if (text != null && !text.isEmpty()) {
                        paraText.append(text);
                    }

                    for (XWPFPicture pic : run.getEmbeddedPictures()) {
                        XWPFPictureData picData = pic.getPictureData();
                        byte[] data = picData.getData();
                        String imageHash = DigestUtils.md5Hex(data);
                        String url;

                        if (data.length == 0) {
                            continue; // 跳过空图片
                        }

                        if (imageCache.containsKey(imageHash)) {
                            url = imageCache.get(imageHash);
                        } else {
                            String imageName = "img_" + imageHash + "." + getImageExtension(picData.getPictureType());
                            url = uploadImageToS3AndGetUrl(data, imageName);
                            imageCache.put(imageHash, url);
                        }

                        paraText.append("<img src=\"").append(url).append("\" alt=\"img\"/>");
                    }
                }

                if (!paraText.toString().trim().isEmpty()) {
                    fullText.append(paraText.toString().trim()).append("\n");
                }
            }
        }

        return fullText.toString();
    }

    private String extractMediaContent(String filePath) {
        try {
            String mediaS3Key = "kms/document-media/"+ DateUtil.format(new Date(),"yyyy/MM/dd") + "/" + FileUtil.getName(filePath);
            ossService.putObject(filePath,mediaS3Key);
            String mediaUrl = ossService.getHttpUrl(mediaS3Key);
            return String.format("<img src=\"%s\" alt=\"%s\" />", mediaUrl, FileUtil.getName(filePath));
        } catch (Exception e) {
            log.error("解析媒体文件失败", e);
            return  e.getMessage();
        }
    }

    /**
     * 提取DOCX文档内容（包括表格）
     */
    private String extractDocxContent(InputStream inputStream) throws Exception {
        StringBuilder fullText = new StringBuilder();

        try (XWPFDocument doc = new XWPFDocument(inputStream)) {
            // 处理文档中的所有元素
            for (IBodyElement element : doc.getBodyElements()) {
                if (element instanceof XWPFParagraph) {
                    // 处理段落（包括段落中的文本框内容）
                    XWPFParagraph para = (XWPFParagraph) element;
                    String paraContent = processParagraph(para);
                    if (!paraContent.trim().isEmpty()) {
                        fullText.append(paraContent).append("\n");
                    }
                } else if (element instanceof XWPFTable) {
                    // 处理表格（包括嵌套表格）
                    XWPFTable table = (XWPFTable) element;
                    String tableContent = processTable(table);
                    if (!tableContent.trim().isEmpty()) {
                        fullText.append(tableContent).append("\n");
                    }
                }
            }

            // 处理页眉页脚中的内容
            for (XWPFHeader header : doc.getHeaderList()) {
                for (IBodyElement element : header.getBodyElements()) {
                    if (element instanceof XWPFParagraph) {
                        XWPFParagraph para = (XWPFParagraph) element;
                        String content = processParagraph(para);
                        if (!content.trim().isEmpty()) {
                            fullText.append(content).append("\n");
                        }
                    } else if (element instanceof XWPFTable) {
                        XWPFTable table = (XWPFTable) element;
                        String content = processTable(table);
                        if (!content.trim().isEmpty()) {
                            fullText.append(content).append("\n");
                        }
                    }
                }
            }

            for (XWPFFooter footer : doc.getFooterList()) {
                for (IBodyElement element : footer.getBodyElements()) {
                    if (element instanceof XWPFParagraph) {
                        XWPFParagraph para = (XWPFParagraph) element;
                        String content = processParagraph(para);
                        if (!content.trim().isEmpty()) {
                            fullText.append(content).append("\n");
                        }
                    } else if (element instanceof XWPFTable) {
                        XWPFTable table = (XWPFTable) element;
                        String content = processTable(table);
                        if (!content.trim().isEmpty()) {
                            fullText.append(content).append("\n");
                        }
                    }
                }
            }
        }

        return fullText.toString().trim();
    }

    /**
     * 段落：文本 + 图片 + 文本框(形状)里的内容
     */
    private String processParagraph(XWPFParagraph para) {
        StringBuilder paraText = new StringBuilder();

        for (XWPFRun run : para.getRuns()) {
            // 普通文本
            String text = run.text();
            if (text != null && !text.isEmpty()) {
                paraText.append(text);
            }

            // 运行内的图片
            for (XWPFPicture pic : run.getEmbeddedPictures()) {
                XWPFPictureData pd = pic.getPictureData();
                if (pd != null && pd.getData().length > 0) {
                    String ext = getImageExtension(pd.getPictureType());
                    String name = "image_" + System.currentTimeMillis() + "." + ext;
                    String url = uploadImageToS3AndGetUrl(pd.getData(), name);
                    paraText.append(String.format("<img src=\"%s\" alt=\"%s\" />", url, name));
                }
            }

            // 关键：提取文本框/形状里的内容（包括“假表格”）
            paraText.append(extractTextBoxesFromRun(run, para.getBody()));
        }

        return paraText.toString().trim();
    }

    /**
     * 从 run 的 Drawing/VML 里把 w:txbxContent 取出，递归成段落/表格文本
     */
    private String extractTextBoxesFromRun(XWPFRun run, IBody body) {
        StringBuilder sb = new StringBuilder();

        // DrawingML 文本框（w:drawing -> ... -> w:txbxContent）
        for (CTDrawing d : run.getCTR().getDrawingList()) {
            XmlCursor c = d.newCursor();
            // 声明 w 命名空间，搜索所有 txbxContent
            c.selectPath(
                    "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' " +
                            ".//w:txbxContent"
            );
            while (c.toNextSelection()) {
                XmlObject txbx = c.getObject();
                // 把 txbxContent 里的段落和表格都捞出来
                XmlCursor c2 = txbx.newCursor();
                c2.selectPath(
                        "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' " +
                                "./w:p | ./w:tbl | .//w:p | .//w:tbl"
                );
                while (c2.toNextSelection()) {
                    XmlObject inner = c2.getObject();
                    if (inner instanceof CTP) {
                        // 构造“临时”段落对象复用你的处理逻辑
                        XWPFParagraph p = new XWPFParagraph((CTP) inner, body);
                        sb.append(processParagraph(p)).append("\n");
                    } else if (inner instanceof CTTbl) {
                        XWPFTable t = new XWPFTable((CTTbl) inner, body);
                        sb.append(processTable(t)).append("\n");
                    }
                }
                c2.dispose();
            }
            c.dispose();
        }

        // VML 文本框（w:pict -> v:shape -> v:textbox -> w:txbxContent），WPS/套红里常见
        for (CTPicture pict : run.getCTR().getPictList()) {
            XmlCursor c = pict.newCursor();
            c.selectPath(
                    "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' " +
                            ".//w:txbxContent"
            );
            while (c.toNextSelection()) {
                XmlObject txbx = c.getObject();
                XmlCursor c2 = txbx.newCursor();
                c2.selectPath(
                        "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' " +
                                "./w:p | ./w:tbl | .//w:p | .//w:tbl"
                );
                while (c2.toNextSelection()) {
                    XmlObject inner = c2.getObject();
                    if (inner instanceof CTP) {
                        XWPFParagraph p = new XWPFParagraph((CTP) inner, body);
                        sb.append(processParagraph(p)).append("\n");
                    } else if (inner instanceof CTTbl) {
                        XWPFTable t = new XWPFTable((CTTbl) inner, body);
                        sb.append(processTable(t)).append("\n");
                    }
                }
                c2.dispose();
            }
            c.dispose();
        }

        return sb.toString();
    }

    /**
     * 递归处理表格内容（支持嵌套表格）
     */
    private String processTable(XWPFTable table) {
        StringBuilder sb = new StringBuilder();

        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                // 先处理单元格里的段落
                for (XWPFParagraph p : cell.getParagraphs()) {
                    sb.append(processParagraph(p)).append("\n");
                }
                // 再处理单元格里的嵌套表格
                for (XWPFTable nested : cell.getTables()) {
                    sb.append(processTable(nested)).append("\n");
                }
            }
        }

        return sb.toString().trim();
    }

    /**
     * 上传图片到S3并返回URL
     */
    private String uploadImageToS3AndGetUrl(byte[] imageData, String imageName) {
        try {
            // 上传到S3
            // 将这个imageData压缩成jpeg格式，缩小图片大小
            BufferedImage image = ImgUtil.toImage(imageData);
//            // 转成jpeg格式
//            ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
//            ImageIO.write(image, "JPEG", jpegOutputStream);
//            byte[] jpegImageData = jpegOutputStream.toByteArray();


            // 根据压缩后的JPEG数据创建一个临时性文件，使用完成后删除
//            File file = FileUtil.writeBytes(jpegImageData, "/uploadPath/knowledge/temp" + imageName);
            File file = ImageUtil.writeFile(image, FileUtil.getSuffix(imageName), "/uploadPath/knowledge/temp/" + LocalDateTime.now().format(DateFormats.yyyyMMddHHmmss) + imageName);
            String s3Path = "kms/document-images/" + imageName;
            ossService.putObject(file, s3Path);
            FileUtil.del(file); // 删除临时文件
            // 获取访问URL
            return ossService.getHttpUrl(s3Path);
        } catch (Exception e) {
            log.error("图片上传失败: {}", imageName, e);
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 提取DOC文档内容
     */
    private DocumentContentResult extractDocContent(InputStream inputStream) throws IOException {
        DocumentContentResult result = new DocumentContentResult();
        List<DocumentSegment> segments = new ArrayList<>();
        List<DocumentImage> images = new ArrayList<>();

        try (HWPFDocument document = new HWPFDocument(inputStream)) {
            Range range = document.getRange();
            String fullText = range.text();

            // 按段落分割
            String[] paragraphs = fullText.split("\\r");
            for (int i = 0; i < paragraphs.length; i++) {
                String paragraph = paragraphs[i].trim();
                if (!paragraph.isEmpty()) {
                    DocumentSegment segment = new DocumentSegment();
                    segment.setIndex(segments.size());
                    segment.setContent(paragraph);
                    segment.setType(determineSegmentType(paragraph));
                    segment.setLength(paragraph.length());
                    segments.add(segment);
                }
            }

            // 提取图片
            List<Picture> pictures = document.getPicturesTable().getAllPictures();
            for (int i = 0; i < pictures.size(); i++) {
                Picture picture = pictures.get(i);

                String imageName = "image_" + i + "." + picture.suggestFileExtension();
                String format = picture.suggestFileExtension();

                // 上传图片到S3并创建DocumentImage对象
                DocumentImage image = uploadImageToS3(picture.getContent(), imageName, format, i, null);
                images.add(image);
            }

            result.setSegments(segments);
            result.setImages(images);
            result.setRawContent(fullText);
            result.setTotalCharacters(fullText.length());
        }

        return result;
    }

    /**
     * 提取纯文本内容
     */
    private DocumentContentResult extractTextContent(String content) {
        DocumentContentResult result = new DocumentContentResult();
        List<DocumentSegment> segments = new ArrayList<>();

        // 按段落分割文本
        String[] paragraphs = content.split("\\n\\s*\\n");
        for (int i = 0; i < paragraphs.length; i++) {
            String paragraph = paragraphs[i].trim();
            if (!paragraph.isEmpty()) {
                DocumentSegment segment = new DocumentSegment();
                segment.setIndex(segments.size());
                segment.setContent(paragraph);
                segment.setType(determineSegmentType(paragraph));
                segment.setLength(paragraph.length());
                segments.add(segment);
            }
        }

        result.setSegments(segments);
        result.setImages(new ArrayList<>());
        result.setRawContent(content);
        result.setTotalCharacters(content.length());

        return result;
    }


    /**
     * 从PDF资源中提取图片
     */
    private void extractImagesFromResources(PDResources resources, List<DocumentImage> images, int pageNumber) throws IOException {
        if (resources != null) {
            for (org.apache.pdfbox.cos.COSName name : resources.getXObjectNames()) {
                PDXObject xObject = resources.getXObject(name);
                if (xObject instanceof PDImageXObject imageObject) {
                    BufferedImage bufferedImage = imageObject.getImage();

                    // 将图片转换为字节数组
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "PNG", baos);
                    byte[] imageBytes = baos.toByteArray();

                    String imageName = name.getName() + ".png";
                    String format = "png";

                    // 上传图片到S3并创建DocumentImage对象
                    DocumentImage image = uploadImageToS3(imageBytes, imageName, format, images.size(), pageNumber);
                    images.add(image);
                }
            }
        }
    }

    /**
     * 上传图片到S3并创建DocumentImage对象
     */
    private DocumentImage uploadImageToS3(byte[] imageData, String imageName, String format, int position, Integer pageNumber) {
        try {
            // 确定内容类型
            String contentType = getContentType(format);
            File file = FileUtil.writeBytes(imageData, "/uploadPath/knowledge/temp" + imageName);
            // 上传到S3
            String s3Path = "kms/document-images/" + imageName;
            ossService.putObject(file, s3Path);

            // 获取访问URL
            String url = ossService.getHttpUrl(s3Path);

            // 改成文件的md5，编程<img src="url" alt="imageName" />的方式返回
            String markdownLink = "![" + imageName + "](" + url + ")";

            // 创建DocumentImage对象
            DocumentImage image = new DocumentImage();
            image.setName(imageName);
            image.setS3Key(s3Path);
            image.setUrl(url);
            image.setMarkdownLink(markdownLink);
            image.setFormat(format);
            image.setPosition(position);
            image.setPageNumber(pageNumber);
            image.setSize(imageData.length);

            log.info("图片上传成功: {}, S3路径: {}", imageName, s3Path);
            return image;
        } catch (Exception e) {
            log.error("图片上传失败: {}", imageName, e);
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 根据图片格式获取内容类型
     */
    private String getContentType(String format) {
        switch (format.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            default:
                return "image/png";
        }
    }

    /**
     * 判断段落类型
     */
    private String determineSegmentType(String text) {
        if (text.length() < 50 && (text.matches("^#+\\s+.*") || text.matches("^\\d+\\.\\s+.*") || text.matches("^[A-Z][^.]*$"))) {
            return "heading";
        } else if (text.matches("^[-*+]\\s+.*") || text.matches("^\\d+\\.\\s+.*")) {
            return "list";
        } else {
            return "paragraph";
        }
    }

    /**
     * 获取图片扩展名
     */
    private String getImageExtension(int pictureType) {
        if (pictureType == XWPFDocument.PICTURE_TYPE_JPEG) {
            return "jpg";
        } else if (pictureType == XWPFDocument.PICTURE_TYPE_PNG) {
            return "png";
        } else if (pictureType == XWPFDocument.PICTURE_TYPE_GIF) {
            return "gif";
        } else if (pictureType == XWPFDocument.PICTURE_TYPE_BMP) {
            return "bmp";
        } else {
            return "png";
        }
    }

    /**
     * 判断文件类型是否支持内容提取
     */
    public boolean isSupportedFileType(String fileName) {
        if (fileName == null) {
            return false;
        }

        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(".txt") ||
                lowerFileName.endsWith(".md") ||
                lowerFileName.endsWith(".pdf") ||
                lowerFileName.endsWith(".doc") ||
                lowerFileName.endsWith(".docx");
    }

    /**
     * 根据文件扩展名获取文件类型
     */
    public String getFileType(String fileName) {
        if (fileName == null) {
            return "unknown";
        }

        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".txt")) {
            return "txt";
        } else if (lowerFileName.endsWith(".md")) {
            return "md";
        } else if (lowerFileName.endsWith(".pdf")) {
            return "pdf";
        } else if (lowerFileName.endsWith(".doc")) {
            return "doc";
        } else if (lowerFileName.endsWith(".docx")) {
            return "docx";
        }

        return "unknown";
    }
} 