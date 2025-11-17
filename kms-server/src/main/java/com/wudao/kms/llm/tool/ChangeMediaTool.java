package com.wudao.kms.llm.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MimeTypeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 转换media工具
 */
@Slf4j
public class ChangeMediaTool {
    /**
     * 将文件URL转换为Media格式
     * @param fileUrls 文件URL列表
     * @return Media列表
     */
    public static List<Media> convertUrlsToMedia(List<String> fileUrls) {
        List<Media> mediaList = new ArrayList<>();

        if (fileUrls == null || fileUrls.isEmpty()) {
            return mediaList;
        }

        for (String url : fileUrls) {
            try {
                if (url != null && !url.trim().isEmpty()) {
                    // 根据URL扩展名确定MIME类型
                    String mimeType = getMimeTypeFromUrl(url);

                    // 将文件下载到本地
                    File localFile = downloadFileToLocal(url);

                    // 使用 FileSystemResource 创建 Media 对象
                    Media media = new Media(
                        MimeTypeUtils.parseMimeType(mimeType),
                        new FileSystemResource(localFile)
                    );
                    mediaList.add(media);

                    log.debug("转换文件为Media: {} -> 本地文件: {}, MIME: {}", url, localFile.getAbsolutePath(), mimeType);
                }
            } catch (Exception e) {
                log.warn("转换文件URL失败: {}, error: {}", url, e.getMessage());
            }
        }

        log.info("成功转换 {}/{} 个文件为Media格式", mediaList.size(), fileUrls.size());
        return mediaList;
    }

    /**
     * 下载文件到本地临时目录
     * @param url 文件URL
     * @return 本地文件
     */
    private static File downloadFileToLocal(String url) throws Exception {
        log.info("开始下载文件: {}", url);

        // 创建临时目录
        String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "media_downloads";
        FileUtil.mkdir(tempDir);

        // 获取文件扩展名
        String extension = getFileExtension(url);

        // 生成唯一的本地文件名
        String localFileName = IdUtil.fastSimpleUUID() + extension;
        File localFile = new File(tempDir, localFileName);

        // 下载文件
        URL fileUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000); // 10秒连接超时
        connection.setReadTimeout(30000);    // 30秒读取超时
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(localFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }

        log.info("文件下载完成: {} -> {}, 大小: {} bytes", url, localFile.getAbsolutePath(), localFile.length());
        return localFile;
    }

    /**
     * 获取文件扩展名
     * @param url 文件URL
     * @return 扩展名（包含点号），例如：.jpg
     */
    private static String getFileExtension(String url) {
        // 移除URL参数
        String urlWithoutParams = url.split("\\?")[0];

        int lastDotIndex = urlWithoutParams.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < urlWithoutParams.length() - 1) {
            return urlWithoutParams.substring(lastDotIndex);
        }

        // 默认扩展名
        return ".tmp";
    }

    /**
     * 根据URL获取MIME类型
     * @param url 文件URL
     * @return MIME类型
     */
    public static String getMimeTypeFromUrl(String url) {
        String lowerUrl = url.toLowerCase();

        // 图片类型
        if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg")) {
            return MimeTypeUtils.IMAGE_JPEG_VALUE;
        } else if (lowerUrl.endsWith(".png")) {
            return MimeTypeUtils.IMAGE_PNG_VALUE;
        } else if (lowerUrl.endsWith(".gif")) {
            return MimeTypeUtils.IMAGE_GIF_VALUE;
        } else if (lowerUrl.endsWith(".webp")) {
            return "image/webp";
        }

        // 文档类型
        else if (lowerUrl.endsWith(".pdf")) {
            return MimeTypeUtils.TEXT_PLAIN_VALUE;
        } else if (lowerUrl.endsWith(".doc") || lowerUrl.endsWith(".docx")) {
            return "application/msword";
        } else if (lowerUrl.endsWith(".txt")) {
            return MimeTypeUtils.TEXT_PLAIN_VALUE;
        }

        // 音频类型
        else if (lowerUrl.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (lowerUrl.endsWith(".wav")) {
            return "audio/wav";
        }

        // 视频类型
        else if (lowerUrl.endsWith(".mp4")) {
            return "video/mp4";
        } else if (lowerUrl.endsWith(".avi")) {
            return "video/x-msvideo";
        }

        // 默认为二进制文件
        else {
            return MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
        }
    }
}
