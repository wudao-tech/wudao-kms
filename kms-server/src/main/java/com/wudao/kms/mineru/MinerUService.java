package com.wudao.kms.mineru;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.sdk.service.pai_dlc20201203.models.CreateJobResponseBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wudao.common.config.BasicConfig;
import com.wudao.common.exception.ServiceException;
import com.wudao.framework.service.SSEService;
import com.wudao.kms.entity.MinerUTask;
import com.wudao.kms.mapper.MinerUTaskMapper;
import com.wudao.kms.util.DlcUtil;
import com.wudao.kms.util.pai.Dlc;
import com.wudao.kms.util.pai.dto.DlcCreateOssDTO;
import com.wudao.oss.service.OssService;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.wudao.system.service.ISysConfigService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinerUService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final SSEService sseService;
    private final MinerUTaskMapper minerUTaskMapper;
    private final OssService ossService;
    private final ISysConfigService sysConfigService;
    private static final String MINER_U_BASE_URL = "https://mineru.net/api/v4";

    /**
     * 单个文件解析
     */
    public CompletableFuture<MinerUTask> parseFile(String filePath, Long userId) {
        log.info("开始解析单个文件: {}, 用户ID: {}", filePath, userId);

        String fileUuid = IdUtil.simpleUUID();
        String fileName = extractFileNameFromPath(filePath);
        String parseType = FileUtil.getSuffix(filePath).split("\\?")[0];

        // 创建MinerUTask记录
        MinerUTask task = createMinerUTask(fileUuid, filePath, fileName, parseType, userId);
        minerUTaskMapper.insert(task);

        try {
            // 发送SSE进度通知 - 开始解析
            sendProgressUpdate(userId, fileUuid, "pending", 0, "开始\"开始解析文件\"解析文件");

            // 调用MinerU API
            ParseResponse response = parseSingleFile(filePath, fileName, parseType, true, true);

            if ("success".equals(response.getStatus()) || response.getTaskId() != null) {
                // 更新任务ID和状态
                updateTaskStatus(task.getId(), response.getTaskId(), "running", null);
                sendProgressUpdate(userId, fileUuid, "running", 10, "文件已提交解析");

                // 异步轮询任务结果
                pollTaskResult(userId, task, response.getTaskId(), fileUuid);

            } else {
                // 解析失败
                updateTaskStatus(task.getId(), null, "failed", response.getMessage());
                sendProgressUpdate(userId, fileUuid, "failed", 0, "文件解析失败: " + response.getMessage());
            }

        } catch (Exception e) {
            log.error("文件解析异常: {}", e.getMessage(), e);
            updateTaskStatus(task.getId(), null, "failed", "文件解析异常: " + e.getMessage());
            sendProgressUpdate(userId, fileUuid, "failed", 0, "文件解析异常: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(task);
    }

    public static String s3PathPrefix(){
        return StrUtil.format("oss://oss-kms-bucket-hangzhou.oss-cn-hangzhou-internal.aliyuncs.com/");
    }

    public CompletableFuture<MinerUTask> parseFileDlc(String fileMd5, String filePath, Long userId) {
        log.info("开始解析单个文件: {}, 用户ID: {}", filePath, userId);

        String fileUuid = IdUtil.simpleUUID();
        String fileName = extractFileNameFromPath(filePath);
        String parseType = extractFileType(fileName);

        // 创建MinerUTask记录
        MinerUTask task = createMinerUTask(fileUuid, filePath, fileName, parseType, userId);
        minerUTaskMapper.insert(task);

        String s3Input = String.format("wudao/mineru/%s/input/",fileMd5);
        String s3Output =  String.format("wudao/mineru/%s/output",fileMd5);

        try {
            String staticPath = String.format("wudao/mineru/%s/output", fileMd5);
            List<String> childrenFile = ossService.childrenFile(staticPath);
            if (CollUtil.isNotEmpty(childrenFile)) {
                String extraFullMdPath = downloadAndProcessResultDlc(staticPath, task.getId(), FileUtil.getName(filePath));
                task.setLocalResultPath(extraFullMdPath);
                return CompletableFuture.completedFuture(task);
            }

            // 发送SSE进度通知 - 开始解析
            sendProgressUpdate(userId, fileUuid, "pending", 0, "开始\"开始解析文件\"解析文件");

            List<DlcCreateOssDTO> mountList;

            DlcCreateOssDTO extra = new DlcCreateOssDTO();
            extra.setOssUri(s3PathPrefix() + s3Input);
            extra.setMountPath("/app/input/");

            DlcCreateOssDTO output = new DlcCreateOssDTO();
            output.setOssUri(s3PathPrefix() + s3Output);
            output.setMountPath("/app/output/");
            mountList = Arrays.asList(extra, output);

            // 创建dlc任务
            String taskName = String.format("mineru_ocr%s", IdUtil.fastSimpleUUID());
            CreateJobResponseBody dlcTask = DlcUtil.createDlcTask(taskName, "registry-vpc.cn-hangzhou.aliyuncs.com/aidojo-registry/mineru:2025101401",
                    "ecs.gn7i-c16g1.4xlarge", mountList, String.format("mineru -p /app/input/%s -o /app/output --source modelscope",FileUtil.getName(filePath)));
            //监控运行完成
            pollDlcStatusRealTime(dlcTask, task);
            String processResultDlc = downloadAndProcessResultDlc(staticPath, task.getId(), fileName);
            task.setLocalResultPath(processResultDlc);
            ossService.deleteFile(filePath);
            return CompletableFuture.completedFuture(task);
        } catch (Exception e) {
            log.error("文件解析异常: {}", e.getMessage(), e);
            updateTaskStatus(task.getId(), null, "failed", "文件解析异常: " + e.getMessage());
            sendProgressUpdate(userId, fileUuid, "failed", 0, "文件解析异常: " + e.getMessage());
            return CompletableFuture.completedFuture(task);
        }
    }

    /**
     * 实时轮询DLC状态和进度 - 同步版本
     */
    public void pollDlcStatusRealTime(CreateJobResponseBody createJobResponseBody,
                                      MinerUTask task) {

        Dlc dlc = SpringUtil.getBean(Dlc.class);
        String jobId = createJobResponseBody.getJobId();

        // 初始化查询开始时间为8小时前（阿里云UTC时间适配）
        int timeoutCount = 0;
        int maxTimeoutRetries = 3;
        boolean isCompleted = false;

        log.info("开始实时轮询DLC任务状态: {}", jobId);

        while (!isCompleted) {
            try {
                // 查询DLC任务状态
                com.aliyun.sdk.service.pai_dlc20201203.models.GetJobResponse getJobResponse = dlc.getStatus(jobId);
                String status = getJobResponse.getBody().getStatus();
                log.info("DLC任务{}, 当前状态: {}", jobId, status);

                // 重置超时计数器
                timeoutCount = 0;

                // 检查任务状态并处理相应逻辑
                switch (status) {
                    case "Succeeded":
                        log.info("DLC任务执行成功，开始处理结果");
                        task.setState("done");
                        // DLC执行成功，处理结果，阶段3开始：保存处理结果(70% - 100%)
                        isCompleted = true;
                        break;
                    case "FailedReserving":
                    case "Failed":
                    case "Stopping":
                    case "Stopped":
                        task.setState("failed");
                        throw new ServiceException("DLC任务执行失败，状态: " + status);
                    case "Creating":
                    case "Queuing":
                        // 任务创建/排队中
                        log.info("DLC任务{}处于{}状态，等待中...", jobId, status);
                        Thread.sleep(5000); // 等待5秒
                        break;
                    case "Running":
                        // 正常运行状态
                        Thread.sleep(8000); // 等待8秒，比复制进度稍快一点
                        break;
                    default:
                        log.warn("未知的DLC任务状态: {}", status);
                        Thread.sleep(10000); // 默认等待10秒
                        break;
                }

            } catch (java.util.concurrent.TimeoutException | java.util.concurrent.ExecutionException e) {
                timeoutCount++;
                log.warn("DLC状态查询超时，重试次数: {}/{}", timeoutCount, maxTimeoutRetries);

                if (timeoutCount >= maxTimeoutRetries) {
                    log.error("DLC状态查询连续超时{}次，任务终止", maxTimeoutRetries);
                    throw new ServiceException("DLC任务查询超时");
                } else {
                    try {
                        Thread.sleep(30000); // 30秒后重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        isCompleted = true;
                    }
                }
            } catch (InterruptedException e) {
                log.info("DLC监听线程被中断");
                Thread.currentThread().interrupt();
                isCompleted = true;
            } catch (Exception e) {
                log.error("监听DLC日志发生其他错误", e);
                // 更新数据库进度到100%
                task.setState("failed");
                throw new ServiceException("DLC监听异常: " + e.getMessage());
            }
        }
        minerUTaskMapper.updateById(task);
        log.info("DLC状态轮询结束");
    }

    /**
     * 批量文件解析（拆分为多个单个文件处理）
     */
    @Async
    public CompletableFuture<Void> parseFile(List<String> filesPath, Long userId) {
        log.info("开始批量解析文件，数量: {}, 用户ID: {}", filesPath.size(), userId);

        // 为每个文件创建独立的解析任务
        for (String filePath : filesPath) {
            try {
                String fileUuid = IdUtil.simpleUUID();
                String fileName = extractFileNameFromPath(filePath);
                String parseType = extractFileType(fileName);

                // 创建MinerUTask记录
                MinerUTask task = createMinerUTask(fileUuid, filePath, fileName, parseType, userId);
                minerUTaskMapper.insert(task);

                // 发送SSE进度通知 - 开始解析
                sendProgressUpdate(userId, fileUuid, "pending", 0, "开始解析文件");

                // 调用MinerU API
                ParseResponse response = parseSingleFile(filePath, fileName, parseType, true, true);

                if ("success".equals(response.getStatus()) || response.getTaskId() != null) {
                    // 更新任务ID和状态
                    updateTaskStatus(task.getId(), response.getTaskId(), "running", null);
                    sendProgressUpdate(userId, fileUuid, "running", 10, "文件已提交解析");

                    // 异步轮询任务结果
                    pollTaskResult(userId, task, response.getTaskId(), fileUuid);

                } else {
                    // 解析失败
                    updateTaskStatus(task.getId(), null, "failed", response.getMessage());
                    sendProgressUpdate(userId, fileUuid, "failed", 0, "文件解析失败: " + response.getMessage());
                }

                // 添加延迟避免API频率限制
                Thread.sleep(200);

            } catch (Exception e) {
                log.error("批量解析文件 {} 异常: {}", filePath, e.getMessage(), e);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Data
    public static class MinerUExtractRequest {
        private String url;
        private boolean isOcr = true;
        private boolean enableFormula = false;
    }

    @Data
    public static class BatchFileRequest {
        private String url;
        private boolean isOcr = false;
        private String dataId;
        private String pageRanges;
    }

    @Data
    public static class BatchParseResponse {
        private String batchId;
        private String status;
        private String message;
    }

    @Data
    public static class ParseResponse {
        private String taskId;
        private String status;
        private String message;
    }

    @Data
    public static class TaskResultResponse {
        private String taskId;
        private String dataId;
        private String status;  // done, pending, running, failed, converting
        private String result;
        private String downloadUrl;
        private String errorMessage;
        private Integer extractedPages;
        private Integer totalPages;
        private String startTime;
    }

    @Data
    public static class BatchResultResponse {
        private String batchId;
        private List<BatchExtractResult> extractResults;
    }

    @Data
    public static class BatchExtractResult {
        private String fileName;
        private String state;  // done, waiting-file, pending, running, failed, converting
        private String fullZipUrl;
        private String errMsg;
        private String dataId;
        private ExtractProgress extractProgress;
    }

    @Data
    public static class ExtractProgress {
        private Integer extractedPages;
        private Integer totalPages;
        private String startTime;
    }

    /**
     * 单个文件解析 - 调用MinerU创建解析任务
     */
    public ParseResponse parseSingleFile(String fileUrl, String fileName, String parseType, boolean isOcr, boolean enableFormula) {
        return parseSingleFile(fileUrl, fileName, parseType, isOcr, enableFormula, true, "ch", null, null);
    }

    /**
     * 单个文件解析 - 完整参数版本
     */
    public ParseResponse parseSingleFile(String fileUrl, String fileName, String parseType,
                                         boolean isOcr, boolean enableFormula, boolean enableTable,
                                         String language, String dataId, String pageRanges) {
        try {
            log.info("开始解析单个文件: {}, URL: {}", fileName, fileUrl);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("url", fileUrl);
            requestBody.put("is_ocr", true);
            requestBody.put("enable_table", true);
            requestBody.put("language", language);
            requestBody.put("model_version","vlm");

            if (dataId != null && !dataId.isEmpty()) {
                requestBody.put("data_id", dataId);
            }
            if (pageRanges != null && !pageRanges.isEmpty()) {
                requestBody.put("page_ranges", pageRanges);
            }

            HttpHeaders headers = createHeaders();

            // 将Map转换为JSON字符串
            String jsonBody = JSONObject.toJSONString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // 添加调试日志
            log.info("发送请求到: {}", MINER_U_BASE_URL + "/extract/task");
            log.info("请求头: {}", headers);
            log.info("请求体JSON字符串: {}", jsonBody);

            ResponseEntity<String> response = restTemplate.exchange(
                    MINER_U_BASE_URL + "/extract/task",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("响应状态码: {}", response.getStatusCode());
            log.info("响应内容: {}", response.getBody());

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            ParseResponse parseResponse = new ParseResponse();

            // 根据官方文档解析返回结果
            if (jsonNode.has("code") && jsonNode.get("code").asInt() == 0) {
                // 成功响应
                JsonNode dataNode = jsonNode.get("data");
                if (dataNode != null && dataNode.has("task_id")) {
                    parseResponse.setTaskId(dataNode.get("task_id").asText());
                    parseResponse.setStatus("success");
                    parseResponse.setMessage(jsonNode.has("msg") ? jsonNode.get("msg").asText() : "任务创建成功");
                } else {
                    parseResponse.setStatus("error");
                    parseResponse.setMessage("返回数据格式错误");
                }
            } else {
                // 错误响应
                parseResponse.setStatus("error");
                String errorMsg = jsonNode.has("msg") ? jsonNode.get("msg").asText() : "创建任务失败";
                parseResponse.setMessage(errorMsg);
                log.error("创建解析任务失败: code={}, msg={}",
                        jsonNode.has("code") ? jsonNode.get("code").asInt() : "unknown", errorMsg);
            }

            log.info("单个文件解析提交完成，任务ID: {}, 状态: {}", parseResponse.getTaskId(), parseResponse.getStatus());
            return parseResponse;

        } catch (Exception e) {
            log.error("单个文件解析失败: {}", e.getMessage(), e);
            ParseResponse errorResponse = new ParseResponse();
            errorResponse.setStatus("error");
            errorResponse.setMessage("文件解析失败: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 获取任务结果 - 调用MinerU获取解析结果
     */
    public TaskResultResponse getSingleTaskResult(String taskId) {
        try {
            log.info("获取任务结果: {}", taskId);

            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    MINER_U_BASE_URL + "/extract/task/" + taskId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            TaskResultResponse resultResponse = new TaskResultResponse();
            resultResponse.setTaskId(taskId);

            // 根据官方文档解析返回结果
            if (jsonNode.has("code") && jsonNode.get("code").asInt() == 0) {
                JsonNode dataNode = jsonNode.get("data");
                if (dataNode != null) {
                    // 获取任务状态
                    String state = dataNode.has("state") ? dataNode.get("state").asText() : "unknown";
                    resultResponse.setStatus(state);

                    // 获取data_id
                    if (dataNode.has("data_id")) {
                        resultResponse.setDataId(dataNode.get("data_id").asText());
                    }

                    // 获取下载链接
                    if (dataNode.has("full_zip_url")) {
                        resultResponse.setDownloadUrl(dataNode.get("full_zip_url").asText());
                    }

                    // 获取错误信息
                    if (dataNode.has("err_msg")) {
                        resultResponse.setErrorMessage(dataNode.get("err_msg").asText());
                    }

                    // 获取解析进度信息（当状态为running时）
                    if (dataNode.has("extract_progress")) {
                        JsonNode progressNode = dataNode.get("extract_progress");
                        if (progressNode.has("extracted_pages")) {
                            resultResponse.setExtractedPages(progressNode.get("extracted_pages").asInt());
                        }
                        if (progressNode.has("total_pages")) {
                            resultResponse.setTotalPages(progressNode.get("total_pages").asInt());
                        }
                        if (progressNode.has("start_time")) {
                            resultResponse.setStartTime(progressNode.get("start_time").asText());
                        }
                    }
                }
            } else {
                resultResponse.setStatus("failed");
                String errorMsg = jsonNode.has("msg") ? jsonNode.get("msg").asText() : "获取结果失败";
                resultResponse.setErrorMessage(errorMsg);
                log.error("获取任务结果失败: code={}, msg={}",
                        jsonNode.has("code") ? jsonNode.get("code").asInt() : "unknown", errorMsg);
            }

            log.info("获取任务结果成功，状态: {}, 任务ID: {}", resultResponse.getStatus(), taskId);
            return resultResponse;

        } catch (Exception e) {
            log.error("获取任务结果失败: {}", e.getMessage(), e);
            TaskResultResponse errorResponse = new TaskResultResponse();
            errorResponse.setTaskId(taskId);
            errorResponse.setStatus("error");
            errorResponse.setErrorMessage("获取任务结果失败: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 将MinerU的状态映射为内部状态
     */
    private String mapMinerUStatus(String minerUStatus) {
        return switch (minerUStatus.toLowerCase()) {
            case "completed", "success", "finished" -> "done";
            case "processing", "running" -> "running";
            case "pending", "waiting" -> "pending";
            case "failed", "error" -> "failed";
            default -> minerUStatus.toLowerCase();
        };
    }

    /**
     * 批量URL文件解析 - 调用MinerU批量创建解析任务
     */
    public BatchParseResponse parseBatchFiles(List<BatchFileRequest> files, boolean enableFormula, boolean enableTable, String language) {
        try {
            log.info("开始批量解析文件，数量: {}", files.size());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("enable_formula", enableFormula);
            requestBody.put("enable_table", enableTable);
            requestBody.put("language", language);

            List<Map<String, Object>> filesList = new ArrayList<>();
            for (BatchFileRequest file : files) {
                Map<String, Object> fileMap = new HashMap<>();
                fileMap.put("url", file.getUrl());
                fileMap.put("is_ocr", file.isOcr());
                if (file.getDataId() != null && !file.getDataId().isEmpty()) {
                    fileMap.put("data_id", file.getDataId());
                }
                if (file.getPageRanges() != null && !file.getPageRanges().isEmpty()) {
                    fileMap.put("page_ranges", file.getPageRanges());
                }
                filesList.add(fileMap);
            }
            requestBody.put("files", filesList);

            HttpHeaders headers = createHeaders();

            // 将Map转换为JSON字符串
            String jsonBody = JSONObject.toJSONString(requestBody);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            log.info("批量解析请求体: {}", jsonBody);

            ResponseEntity<String> response = restTemplate.exchange(
                    MINER_U_BASE_URL + "/extract/task/batch",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            BatchParseResponse batchResponse = new BatchParseResponse();

            // 根据官方文档解析返回结果
            if (jsonNode.has("code") && jsonNode.get("code").asInt() == 0) {
                JsonNode dataNode = jsonNode.get("data");
                if (dataNode != null && dataNode.has("batch_id")) {
                    batchResponse.setBatchId(dataNode.get("batch_id").asText());
                    batchResponse.setStatus("success");
                    batchResponse.setMessage(jsonNode.has("msg") ? jsonNode.get("msg").asText() : "批量任务创建成功");
                } else {
                    batchResponse.setStatus("error");
                    batchResponse.setMessage("返回数据格式错误");
                }
            } else {
                batchResponse.setStatus("error");
                String errorMsg = jsonNode.has("msg") ? jsonNode.get("msg").asText() : "创建批量任务失败";
                batchResponse.setMessage(errorMsg);
                log.error("创建批量解析任务失败: code={}, msg={}",
                        jsonNode.has("code") ? jsonNode.get("code").asInt() : "unknown", errorMsg);
            }

            log.info("批量文件解析提交完成，批次任务ID: {}, 状态: {}", batchResponse.getBatchId(), batchResponse.getStatus());
            return batchResponse;

        } catch (Exception e) {
            log.error("批量文件解析失败: {}", e.getMessage(), e);
            BatchParseResponse errorResponse = new BatchParseResponse();
            errorResponse.setStatus("error");
            errorResponse.setMessage("批量文件解析失败: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 批量获取任务结果 - 调用MinerU获取批量解析结果
     */
    public BatchResultResponse getBatchResults(String batchId) {
        try {
            log.info("获取批量任务结果: {}", batchId);

            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    MINER_U_BASE_URL + "/extract-results/batch/" + batchId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            BatchResultResponse batchResponse = new BatchResultResponse();

            // 根据官方文档解析返回结果
            if (jsonNode.has("code") && jsonNode.get("code").asInt() == 0) {
                JsonNode dataNode = jsonNode.get("data");
                if (dataNode != null) {
                    batchResponse.setBatchId(dataNode.has("batch_id") ? dataNode.get("batch_id").asText() : batchId);

                    JsonNode extractResultNode = dataNode.get("extract_result");
                    if (extractResultNode != null && extractResultNode.isArray()) {
                        List<BatchExtractResult> extractResults = new ArrayList<>();
                        for (JsonNode resultNode : extractResultNode) {
                            BatchExtractResult extractResult = new BatchExtractResult();
                            extractResult.setFileName(resultNode.has("file_name") ? resultNode.get("file_name").asText() : "");
                            extractResult.setState(resultNode.has("state") ? resultNode.get("state").asText() : "");
                            extractResult.setFullZipUrl(resultNode.has("full_zip_url") ? resultNode.get("full_zip_url").asText() : "");
                            extractResult.setErrMsg(resultNode.has("err_msg") ? resultNode.get("err_msg").asText() : "");
                            extractResult.setDataId(resultNode.has("data_id") ? resultNode.get("data_id").asText() : "");

                            // 解析进度信息
                            if (resultNode.has("extract_progress")) {
                                JsonNode progressNode = resultNode.get("extract_progress");
                                ExtractProgress progress = new ExtractProgress();
                                progress.setExtractedPages(progressNode.has("extracted_pages") ? progressNode.get("extracted_pages").asInt() : 0);
                                progress.setTotalPages(progressNode.has("total_pages") ? progressNode.get("total_pages").asInt() : 0);
                                progress.setStartTime(progressNode.has("start_time") ? progressNode.get("start_time").asText() : "");
                                extractResult.setExtractProgress(progress);
                            }

                            extractResults.add(extractResult);
                        }
                        batchResponse.setExtractResults(extractResults);
                    }
                }
            } else {
                log.error("获取批量任务结果失败: code={}, msg={}",
                        jsonNode.has("code") ? jsonNode.get("code").asInt() : "unknown",
                        jsonNode.has("msg") ? jsonNode.get("msg").asText() : "unknown");
            }

            log.info("获取批量任务结果成功，批次ID: {}, 结果数量: {}",
                    batchResponse.getBatchId(),
                    batchResponse.getExtractResults() != null ? batchResponse.getExtractResults().size() : 0);

            return batchResponse;

        } catch (Exception e) {
            log.error("获取批量任务结果失败: {}", e.getMessage(), e);
            BatchResultResponse errorResponse = new BatchResultResponse();
            errorResponse.setBatchId(batchId);
            return errorResponse;
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String minerUApikey = sysConfigService.getValue("mineru_apikey");
        headers.set("Authorization", "Bearer " + minerUApikey);
        headers.set("Content-Type", "application/json; charset=utf-8");
        headers.set("Accept", "*/*");
        return headers;
    }

    /**
     * 创建MinerUTask实体
     */
    private MinerUTask createMinerUTask(String fileUuid, String filePath, String fileName,
                                        String parseType, Long userId) {
        MinerUTask task = new MinerUTask();
        task.setFileUuid(fileUuid);
        task.setOriginalFileUrl(filePath);
        task.setOriginalFileName(fileName);
        task.setParseType(parseType);
        task.setState("pending");
        task.setStartTime(LocalDateTime.now());
        task.setExtractedPages(0);
        task.setTotalPages(0);
        // BaseEntity字段会由MyBatis Plus自动填充
        return task;
    }

    /**
     * 更新任务状态
     */
    private void updateTaskStatus(Long taskId, String minerUTaskId, String state, String errorMessage) {
        MinerUTask task = minerUTaskMapper.selectById(taskId);
        if (task != null) {
            if (minerUTaskId != null) {
                task.setTaskId(minerUTaskId);
            }
            task.setState(state);
            if (errorMessage != null) {
                task.setErrorMessage(errorMessage);
            }
            if ("done".equals(state)) {
                task.setCompleteTime(LocalDateTime.now());
            }
            minerUTaskMapper.updateById(task);
        }
    }

    /**
     * 发送SSE进度更新
     */
    private void sendProgressUpdate(Long userId, String fileUuid, String status, int progress, String message) {
        try {
            Map<String, Object> progressData = new HashMap<>();
            progressData.put("fileUuid", fileUuid);
            progressData.put("status", status);
            progressData.put("progress", progress);
            progressData.put("message", message);
            progressData.put("timestamp", LocalDateTime.now());
            progressData.put("userId", userId);

            sseService.send(userId.toString(), "parseFileProgress", progressData);
            log.debug("发送SSE进度更新: fileUuid={}, status={}, progress={}", fileUuid, status, progress);
        } catch (Exception e) {
            log.error("发送SSE进度更新失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 轮询任务结果
     */
    @Async
    public void pollTaskResult(Long userId, MinerUTask task, String minerUTaskId, String fileUuid) {
        int maxAttempts = 60; // 最多轮询60次
        int attemptInterval = 5000; // 每5秒轮询一次

        label:
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Thread.sleep(attemptInterval);

                TaskResultResponse result = getSingleTaskResult(minerUTaskId);

                // 解析完成
                switch (result.getStatus()) {
                    case "done":
                        if (task != null) {
                            task.setState("done");
                            task.setResultContent(result.getResult());
                            task.setFullZipUrl(result.getDownloadUrl());
                            task.setCompleteTime(LocalDateTime.now());

                            // 下载并处理MinerU结果
                            if (result.getDownloadUrl() != null && !result.getDownloadUrl().isEmpty()) {
                                try {
                                    sendProgressUpdate(userId, fileUuid, "processing", 95, "正在下载和处理结果文件...");
                                    String processedMarkdown = downloadAndProcessResult(result.getDownloadUrl(), task.getId());
                                    // 设置本地结果路径
//                                    task.setLocalResultPath(processedMarkdown);
                                    task.setResultContent(processedMarkdown);
                                    log.info("MinerU结果处理完成，任务ID: {}, markdown长度: {}",
                                            task.getId(), processedMarkdown.length());
                                } catch (Exception e) {
                                    log.error("处理MinerU结果失败，任务ID: {}, 错误: {}", task.getId(), e.getMessage(), e);
                                    task.setErrorMessage("结果处理失败: " + e.getMessage());
                                }
                            }
                            // 这里可以根据实际返回结果设置总页数和已解析页数
                            minerUTaskMapper.updateById(task);
                        }

                        sendProgressUpdate(userId, fileUuid, "done", 100, "文件解析完成");
                        break label;

                    case "failed":
                        // 解析失败
                        updateTaskStatus(task.getId(), null, "failed", result.getErrorMessage());
                        sendProgressUpdate(userId, fileUuid, "failed", 0, "文件解析失败: " + result.getErrorMessage());
                        break label;

                    case "running":
                        // 仍在运行中，计算进度
                        int progress = Math.min(10 + (attempt * 80 / maxAttempts), 90);
                        sendProgressUpdate(userId, fileUuid, "running", progress, "文件解析中...");

                        break;
                    case null:
                    default:
                        log.debug("任务 {} 状态: {}, 第 {} 次轮询", minerUTaskId, result.getStatus(), attempt);
                        break;
                }

            } catch (Exception e) {
                log.error("轮询任务结果异常: {}", e.getMessage(), e);
                if (attempt == maxAttempts) {
                    updateTaskStatus(task.getId(), null, "failed", "轮询超时");
                    sendProgressUpdate(userId, fileUuid, "failed", 0, "轮询超时，请手动检查任务状态");
                }
            }
        }
    }

    /**
     * 从文件路径提取文件名
     */
    private String extractFileNameFromPath(String filePath) {
        if (filePath == null) {
            return "unknown";
        }
        int lastSlash = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        return lastSlash >= 0 ? filePath.substring(lastSlash + 1) : filePath;
    }

    /**
     * 从文件名提取文件类型
     */
    private String extractFileType(String fileName) {
        if (fileName == null) {
            return "pdf";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "pdf"; // 默认为pdf
    }

    /**
     * 下载并处理MinerU结果
     *
     * @param downloadUrl 完整结果压缩包下载链接
     * @param taskId      任务ID
     * @return 处理后的markdown内容
     */
    public String downloadAndProcessResult(String downloadUrl, Long taskId) {
        try {
            log.info("开始下载并处理MinerU结果，下载链接: {}", downloadUrl);

            // 1. 创建处理目录结构
            String profile = SpringUtil.getBean(BasicConfig.class).getProfile();

            // 创建基础目录：/mineru-results/{profile}/{taskId}/
            String mineruResultPath = String.format(profile + "/mineru-results/%s", taskId);
            FileUtil.mkdir(mineruResultPath);
            Path baseDir = Paths.get(mineruResultPath);
            Files.createDirectories(baseDir);

            // 创建子目录
            Path downloadDir = baseDir.resolve("download");
            Path extractDir = baseDir.resolve("extracted");
            Path processedDir = baseDir.resolve("processed");
            Files.createDirectories(downloadDir);
            Files.createDirectories(extractDir);
            Files.createDirectories(processedDir);

            Path zipFile = downloadDir.resolve("result.zip");

            downloadFile(downloadUrl, zipFile.toFile());
            log.info("下载完成，文件保存到: {}", zipFile);

            // 2. 解压zip文件
            unzipFile(zipFile.toFile(), extractDir.toFile());
            log.info("解压完成，文件解压到: {}", extractDir);

            // 3. 查找full.md文件
            Path markdownFile = findMarkdownFile(extractDir);
            if (markdownFile == null) {
                throw new RuntimeException("未找到full.md文件");
            }

            // 4. 处理图片并替换路径
            String processedContent = processMarkdownImages(markdownFile, extractDir);

            // 5. 保存处理后的markdown文件
            Path processedMarkdownFile = processedDir.resolve("processed_full.md");
            Files.writeString(processedMarkdownFile, processedContent);
            log.info("处理后的markdown文件已保存到: {}", processedMarkdownFile);

            // 6. 保留处理结果目录，不清理
            log.info("MinerU处理结果已保存到目录: {}", baseDir);

            FileUtil.del(downloadDir);
            FileUtil.del(extractDir);
            return processedContent;

        } catch (Exception e) {
            log.error("下载并处理MinerU结果失败: {}", e.getMessage(), e);
            throw new RuntimeException("处理MinerU结果失败: " + e.getMessage(), e);
        }
    }

    public String downloadAndProcessResultDlc(String ossOutput, Long taskId, String fileName) {
        try {
            log.info("开始下载并处理MinerU结果，下载链接: {}", ossOutput);

            // 1. 创建处理目录结构
            String profile = SpringUtil.getBean(BasicConfig.class).getProfile();

            // 创建基础目录：/mineru-results/{profile}/{taskId}/
            String mineruResultPath = String.format(profile + "/mineru-results/%s", taskId);
            FileUtil.mkdir(mineruResultPath);
            Path baseDir = Paths.get(mineruResultPath);
            Files.createDirectories(baseDir);

            // 创建子目录
            Path downloadDir = baseDir.resolve("download");
            Path extractDir = baseDir.resolve("extracted");
            Path processedDir = baseDir.resolve("processed");
            Files.createDirectories(downloadDir);
            Files.createDirectories(extractDir);
            Files.createDirectories(processedDir);

            // 下载images和full.md
            String prefix = FileUtil.getPrefix(fileName);
            ossService.downloadDirectory(mineruResultPath+"/extracted/images",ossOutput+"/"+ prefix +"/auto/images");
            ossService.downloadPath(mineruResultPath+"/processed/full.md",ossOutput+"/"+ prefix +"/auto/"+ prefix +".md");

            // 4. 处理图片并替换路径
            String processedContent = processMarkdownImages(Path.of(mineruResultPath+"/processed/full.md"), extractDir);

            // 5. 保存处理后的markdown文件
            Path processedMarkdownFile = processedDir.resolve("full.md");
            Files.writeString(processedMarkdownFile, processedContent);
            log.info("处理后的markdown文件已保存到: {}", processedMarkdownFile);

            // 6. 保留处理结果目录，不清理
            log.info("MinerU处理结果已保存到目录: {}", baseDir);

            FileUtil.del(downloadDir);
            FileUtil.del(extractDir);
            return processedMarkdownFile.toFile().getPath();

        } catch (Exception e) {
            log.error("下载并处理MinerU结果失败: {}", e.getMessage(), e);
            throw new RuntimeException("处理MinerU结果失败: " + e.getMessage(), e);
        }
    }

    /**
     * 下载文件
     */
    private void downloadFile(String downloadUrl, File targetFile) throws Exception {
        log.info("开始下载文件: {} -> {}", downloadUrl, targetFile.getAbsolutePath());

        try (InputStream in = URI.create(downloadUrl).toURL().openStream();
             FileOutputStream out = new FileOutputStream(targetFile)) {
            IoUtil.copy(in, out);
        }

        log.info("文件下载完成，大小: {} bytes", targetFile.length());
    }

    /**
     * 解压zip文件
     */
    private void unzipFile(File zipFile, File destDir) throws Exception {
        log.info("开始解压文件: {} -> {}", zipFile.getAbsolutePath(), destDir.getAbsolutePath());

        if (!destDir.exists()) {
            FileUtil.mkdir(destDir);
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(destDir, entry.getName());

                if (entry.isDirectory()) {
                    FileUtil.mkdir(file);
                } else {
                    // 确保父目录存在
                    FileUtil.mkdir(file.getParentFile());

                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        IoUtil.copy(zis, fos);
                    }
                }
                zis.closeEntry();
            }
        }

        log.info("文件解压完成");
    }

    /**
     * 查找markdown文件
     */
    private Path findMarkdownFile(Path extractDir) throws Exception {
        // 优先查找full.md
        Path fullMd = extractDir.resolve("full.md");
        if (Files.exists(fullMd)) {
            return fullMd;
        }

        // 如果没有找到full.md，查找其他.md文件
        return Files.walk(extractDir)
                .filter(path -> path.toString().endsWith(".md"))
                .findFirst()
                .orElse(null);
    }

    /**
     * 处理markdown中的图片并替换路径
     */
    private String processMarkdownImages(Path markdownFile, Path extractDir) throws Exception {
        log.info("开始处理markdown文件中的图片: {}", markdownFile);

        // 读取markdown内容
        String content = Files.readString(markdownFile);

        // 查找images文件夹
        Path imagesDir = extractDir.resolve("images");
        if (!Files.exists(imagesDir)) {
            // 也尝试查找同级目录的其他可能的图片文件夹
            imagesDir = markdownFile.getParent().resolve("imgs");
            if (!Files.exists(imagesDir)) {
                log.warn("未找到images文件夹，跳过图片处理");
                return content;
            }
        }

        // 正则表达式匹配markdown图片语法：![alt](images/xxx.png) 或 ![alt](./images/xxx.png)
        Pattern imagePattern = Pattern.compile("!\\[([^\\]]*)\\]\\((\\.?/?images/[^\\)]+)\\)");
        Matcher matcher = imagePattern.matcher(content);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String altText = matcher.group(1);
            String imagePath = matcher.group(2);

            // 处理相对路径，移除开头的./或/
            String cleanImagePath = imagePath.replaceFirst("^\\.\\/", "").replaceFirst("^\\/", "");

            try {
                // 上传图片到OSS并获取HTTPS地址
                String httpsUrl = uploadImageToOss(extractDir.resolve(cleanImagePath));

                // 替换为新的URL
                String replacement = String.format("![%s](%s)", altText, httpsUrl);
                matcher.appendReplacement(result, replacement);

                log.info("图片处理成功: {} -> {}", imagePath, httpsUrl);

            } catch (Exception e) {
                log.error("处理图片失败: {}, 错误: {}", imagePath, e.getMessage());
                // 保持原样
                matcher.appendReplacement(result, matcher.group(0));
            }
        }
        matcher.appendTail(result);

        log.info("markdown图片处理完成");
        return result.toString();
    }

    /**
     * 上传图片到OSS
     */
    private String uploadImageToOss(Path imagePath) throws Exception {
        if (!Files.exists(imagePath)) {
            throw new FileNotFoundException("图片文件不存在: " + imagePath);
        }

        String fileName = imagePath.getFileName().toString();
        String fileExtension = FileUtil.extName(fileName);

        // 生成唯一的S3路径
        String s3Key = String.format("wudao/kms/mineru-images/%s/%s",
                LocalDateTime.now().toLocalDate(),
                IdUtil.randomUUID() + "." + fileExtension);
        // 上传到OSS
        ossService.putObject(imagePath.toString(), s3Key);
        // 获取HTTPS访问地址
        String httpsUrl = ossService.getHttpUrl(s3Key);
        return httpsUrl;
    }
}
