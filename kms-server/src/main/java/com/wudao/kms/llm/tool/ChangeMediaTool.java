package com.wudao.kms.llm.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeTypeUtils;

import java.net.URI;
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

                    // 创建Media对象
                    Media media = Media.builder().data(new URI(url)).mimeType(MimeTypeUtils.parseMimeType(mimeType)).build();
                    mediaList.add(media);

                    log.debug("转换文件为Media: {} -> {}", url, mimeType);
                }
            } catch (Exception e) {
                log.warn("转换文件URL失败: {}, error: {}", url, e.getMessage());
            }
        }

        log.info("成功转换 {}/{} 个文件为Media格式", mediaList.size(), fileUrls.size());
        return mediaList;
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
