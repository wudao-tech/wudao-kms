package com.wudao.kms.vo;

import com.wudao.kms.dto.knowledgefile.KnowledgeFileUploadTempDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档分段处理进度VO
 */
@Data
@Schema(description = "文档分段处理进度")
public class DocumentSegmentProgressVO {

    @Schema(description = "事件类型：start|processing|success|error|complete")
    private String eventType;

    @Schema(description = "总体进度百分比(0-100)")
    private Double overallProgress;

    @Schema(description = "当前处理的文件索引(从1开始)")
    private Integer currentFileIndex;

    @Schema(description = "总文件数量")
    private Integer totalFiles;

    @Schema(description = "当前处理的文件信息")
    private FileProcessInfo currentFile;

    @Schema(description = "处理消息")
    private String message;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "时间戳")
    private LocalDateTime timestamp;

    /**
     * 文件处理信息
     */
    @Data
    @Schema(description = "文件处理信息")
    public static class FileProcessInfo {
        @Schema(description = "文件名")
        private String fileName;

        @Schema(description = "文件MD5")
        private String fileMd5;

        @Schema(description = "文件类型")
        private String fileType;

        @Schema(description = "文件大小")
        private Long fileSize;

        @Schema(description = "文件处理状态：pending|processing|completed|failed|skipped")
        private String status;

        @Schema(description = "分段数量")
        private Integer segmentCount;

        @Schema(description = "分段状态描述")
        private String segmentStatus;

        @Schema(description = "文件进度百分比(0-100)")
        private Integer fileProgress;

        @Schema(description = "处理开始时间")
        private LocalDateTime startTime;

        @Schema(description = "处理结束时间")
        private LocalDateTime endTime;

        @Schema(description = "错误原因")
        private String errorReason;

        /**
         * 从KnowledgeFileUploadTempDTO转换
         */
        public static FileProcessInfo fromTempDTO(KnowledgeFileUploadTempDTO tempDTO) {
            FileProcessInfo info = new FileProcessInfo();
            info.setFileName(tempDTO.getFileName());
            info.setFileMd5(tempDTO.getFileMd5());
            info.setFileType(tempDTO.getFileType());
            info.setFileSize(tempDTO.getFileSize());
            info.setSegmentCount(tempDTO.getSegmentCount());
            info.setSegmentStatus(tempDTO.getSegmentStatus());
            info.setStatus("pending");
            info.setFileProgress(0);
            return info;
        }
    }

    /**
     * 创建开始事件
     */
    public static DocumentSegmentProgressVO createStartEvent(int totalFiles) {
        DocumentSegmentProgressVO vo = new DocumentSegmentProgressVO();
        vo.setEventType("start");
        vo.setOverallProgress(0.0);
        vo.setCurrentFileIndex(0);
        vo.setTotalFiles(totalFiles);
        vo.setMessage("开始处理文档分段，共 " + totalFiles + " 个文件");
        vo.setTimestamp(LocalDateTime.now());
        return vo;
    }

    /**
     * 创建处理中事件
     */
    public static DocumentSegmentProgressVO createProcessingEvent(int currentIndex, int totalFiles, 
                                                                 FileProcessInfo fileInfo, String message) {
        DocumentSegmentProgressVO vo = new DocumentSegmentProgressVO();
        vo.setEventType("processing");
        vo.setCurrentFileIndex(currentIndex);
        vo.setTotalFiles(totalFiles);
        vo.setCurrentFile(fileInfo);
        vo.setMessage(message);
        vo.setTimestamp(LocalDateTime.now());
        
        // 计算总体进度：当前文件进度 + 已完成文件进度
        double completedFilesProgress = (currentIndex - 1) * 100.0 / totalFiles;
        double currentFileProgress = (fileInfo.getFileProgress() != null ? fileInfo.getFileProgress() : 0.0) / totalFiles;
        vo.setOverallProgress(completedFilesProgress + currentFileProgress);
        
        return vo;
    }

    /**
     * 创建成功事件
     */
    public static DocumentSegmentProgressVO createSuccessEvent(int currentIndex, int totalFiles, 
                                                              FileProcessInfo fileInfo) {
        DocumentSegmentProgressVO vo = new DocumentSegmentProgressVO();
        vo.setEventType("success");
        vo.setCurrentFileIndex(currentIndex);
        vo.setTotalFiles(totalFiles);
        vo.setCurrentFile(fileInfo);
        vo.setMessage("文件 " + fileInfo.getFileName() + " 分段处理成功");
        vo.setTimestamp(LocalDateTime.now());
        
        // 文件完成后的进度
        vo.setOverallProgress(currentIndex * 100.0 / totalFiles);
        
        return vo;
    }

    /**
     * 创建错误事件
     */
    public static DocumentSegmentProgressVO createErrorEvent(int currentIndex, int totalFiles, 
                                                            FileProcessInfo fileInfo, String errorMessage) {
        DocumentSegmentProgressVO vo = new DocumentSegmentProgressVO();
        vo.setEventType("error");
        vo.setCurrentFileIndex(currentIndex);
        vo.setTotalFiles(totalFiles);
        vo.setCurrentFile(fileInfo);
        vo.setMessage("文件 " + fileInfo.getFileName() + " 分段处理失败");
        vo.setErrorMessage(errorMessage);
        vo.setTimestamp(LocalDateTime.now());
        
        // 即使失败也推进进度
        vo.setOverallProgress(currentIndex * 100.0 / totalFiles);
        
        return vo;
    }

    /**
     * 创建完成事件
     */
    public static DocumentSegmentProgressVO createCompleteEvent(int totalFiles, int successCount, int failedCount) {
        DocumentSegmentProgressVO vo = new DocumentSegmentProgressVO();
        vo.setEventType("complete");
        vo.setOverallProgress(100.0);
        vo.setCurrentFileIndex(totalFiles);
        vo.setTotalFiles(totalFiles);
        vo.setMessage(String.format("分段处理完成！总计 %d 个文件，成功 %d 个，失败 %d 个", 
                totalFiles, successCount, failedCount));
        vo.setTimestamp(LocalDateTime.now());
        return vo;
    }
} 