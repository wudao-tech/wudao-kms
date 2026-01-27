package com.wudao.kms.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.sdk.service.pai_dlc20201203.models.CreateJobResponseBody;
import com.aliyun.sdk.service.pai_dlc20201203.models.GetJobResponse;
import com.aliyun.sdk.service.pai_dlc20201203.models.GetPodLogsResponse;
import com.wudao.kms.util.pai.Dlc;
import com.wudao.kms.util.pai.dto.DlcCreateDTO;
import com.wudao.kms.util.pai.dto.DlcCreateOssDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * DLC统一工具类
 *
 * @author AI Assistant
 */
@Slf4j
public class DlcUtil {

    /**
     * 创建DLC任务
     *
     * @param taskName     任务名称
     * @param image        镜像名称
     * @param instanceType 实例类型
     * @param mountList    挂载列表
     * @param command      执行命令
     * @return DLC任务响应体
     */
    public static CreateJobResponseBody createDlcTask(String taskName, String image, String instanceType,
                                                      List<DlcCreateOssDTO> mountList, String command) {
        try {
            log.info("开始创建DLC任务: {}, 镜像: {}, 实例类型: {}", taskName, image, instanceType);
            Dlc dlc = SpringUtil.getBean(Dlc.class);

            DlcCreateDTO createDTO = DlcCreateDTO.builder()
                    .taskName(taskName)
                    .image(image)
                    .instanceType(instanceType)
                    .jobType("PyTorchJob")
                    .podCount(1L)
                    .mountList(mountList)
                    .command(command)
                    .build();

            CreateJobResponseBody createJobResponseBody = dlc.createTask(createDTO);

            if (createJobResponseBody == null || StrUtil.isBlank(createJobResponseBody.getJobId())) {
                log.error("DLC任务创建失败，未获取到JobId");
                return null;
            }

            log.info("DLC任务创建成功，JobId: {}", createJobResponseBody.getJobId());
            return createJobResponseBody;

        } catch (RuntimeException e) {
            if (e.getMessage().contains("超时")) {
                log.error("DLC任务创建超时，任务名: {}", taskName, e);
            } else {
                log.error("DLC任务创建失败，任务名: {}", taskName, e);
            }
            return null;
        } catch (Exception e) {
            log.error("DLC任务创建异常，任务名: {}", taskName, e);
            return null;
        }
    }

    /**
     * 监听DLC任务日志和状态
     *
     * @param createJobResponseBody DLC任务响应体
     * @param progressHandler       进度处理器，参数为(logLine, status)
     * @param logHandler            日志处理器，参数为日志行
     * @return 任务是否成功完成
     */
    public static boolean listenDlcLog(CreateJobResponseBody createJobResponseBody,
                                       Consumer<String> progressHandler,
                                       Consumer<String> logHandler) {
        if (createJobResponseBody == null || StrUtil.isBlank(createJobResponseBody.getJobId())) {
            return false;
        }

        boolean whileFlag = true;
        StringBuilder sb = new StringBuilder();
        Dlc dlc = SpringUtil.getBean(Dlc.class);

        // 初始化查询开始时间为8小时前（阿里云UTC时间适配）
        LocalDateTime lastQueryTime = LocalDateTime.now().minusHours(8);

        // 记录上次处理的日志数量，用于只推送新日志
        int lastProcessedLogCount = 0;

        int timeoutCount = 0;
        int maxTimeoutRetries = 3; // 最多重试3次超时
        String currentStatus = ""; // 当前任务状态

        while (whileFlag) {
            try {
                GetJobResponse getJobResponse = dlc.getStatus(createJobResponseBody.getJobId());
                String status = getJobResponse.getBody().getStatus();
                currentStatus = status; // 保存当前状态
                log.info("DLC任务{}, 状态: {}", createJobResponseBody.getJobId(), status);

                // 重置超时计数器
                timeoutCount = 0;

                // 检查任务状态
                switch (status) {
                    case "Succeeded":
                        sb.append("finish");
                        whileFlag = false;
                        break;
                    case "Stopping":
                    case "Stopped":
                        sb.append("任务停止");
                        whileFlag = false;
                        break;
                    case "FailedReserving":
                    case "Failed":
                        sb.append("fail");
                        whileFlag = false;
                        break;
                    case "Creating":
                    case "Queuing":
                        // 任务创建/排队中，减少查询频率避免频繁超时
                        log.info("DLC任务{}处于{}状态，等待30秒后再次查询", createJobResponseBody.getJobId(), status);
                        Thread.sleep(20000); // 创建阶段等待更长时间
                        break;
                    case "Running":
                        // 正常运行状态，继续等待
                        break;
                    default:
                        log.warn("未知的DLC任务状态: {}", status);
                        break;
                }

                // 获取日志
                GetPodLogsResponse dlcLog = dlc.getLog(createJobResponseBody.getJobId(),
                        lastQueryTime.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));

                if (dlcLog == null) {
                    Thread.sleep(2000);
                    continue;
                }

                List<String> allLogs = dlcLog.getBody().getLogs();
                int currentLogCount = allLogs.size();

                // 只处理新的日志（从上次处理位置开始）
                List<String> newLogs = new ArrayList<>();
                if (currentLogCount > lastProcessedLogCount) {
                    newLogs = allLogs.subList(lastProcessedLogCount, currentLogCount);
                    log.info("获取到{}条新日志（总日志{}条，上次处理{}条）", newLogs.size(), currentLogCount, lastProcessedLogCount);
                }

                // 将新日志添加到总体日志中
                newLogs.forEach(sb::append);

                // 处理新日志
                for (String logLine : newLogs) {
                    // 调用日志处理器
                    if (logHandler != null) {
                        logHandler.accept(logLine);
                    }

                    // 处理进度日志
                    if (logLine.contains("progress") && progressHandler != null) {
                        progressHandler.accept(logLine);
                    }
                }

                // 检查是否完成
                if (status.equals("Succeeded") || status.equals("Failed") || status.equals("Stopped")) {
                    whileFlag = false;
                }

                // 更新已处理的日志数量
                lastProcessedLogCount = currentLogCount;

                // 根据任务状态调整等待时间
                if ("Creating".equals(currentStatus) || "Queuing".equals(currentStatus)) {
                    // 创建/排队阶段已经在上面等待过了，这里短暂等待即可
                    Thread.sleep(5000);
                } else {
                    // 运行状态，正常等待
                    Thread.sleep(10000);
                }
            } catch (java.util.concurrent.TimeoutException | java.util.concurrent.ExecutionException e) {
                timeoutCount++;
                log.warn("DLC状态查询超时，重试次数: {}/{}, 错误: {}", timeoutCount, maxTimeoutRetries, e.getMessage());

                if (timeoutCount >= maxTimeoutRetries) {
                    log.error("DLC状态查询连续超时{}次，任务终止", maxTimeoutRetries);
                    whileFlag = false;
                } else {
                    // 超时后等待更长时间再重试
                    try {
                        Thread.sleep(30000); // 30秒后重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        whileFlag = false;
                    }
                }
            } catch (Exception e) {
                log.error("监听DLC日志发生其他错误", e);
                whileFlag = false;
            }
        }

        return sb.toString().contains("finish");
    }

    /**
     * 生成唯一的DLC任务名
     *
     * @param prefix 前缀
     * @return 任务名
     */
    public static String generateTaskName(String prefix) {
        return prefix + "-" + IdUtil.fastSimpleUUID();
    }

    /**
     * 创建OSS挂载配置
     *
     * @param s3Path        S3路径
     * @param containerPath 容器内路径
     * @return DlcCreateOssDTO对象
     */
    public static DlcCreateOssDTO createOssMount(String s3Path, String containerPath) {
        return new DlcCreateOssDTO(s3Path, containerPath);
    }

    /**
     * 解析DLC日志中的JSON进度信息
     *
     * @param logLine     日志行
     * @param progressKey 进度字段名（如"progress"）
     * @return 进度值，解析失败返回-1
     */
    public static int parseProgressFromLog(String logLine, String progressKey) {
        try {
            if (logLine.contains(progressKey)) {
                JSONObject progressJson = JSONObject.parseObject(logLine);
                if (progressJson.containsKey(progressKey)) {
                    return progressJson.getIntValue(progressKey);
                }
            }
        } catch (Exception e) {
            log.warn("解析进度日志失败: {}", e.getMessage());
        }
        return -1;
    }

    /**
     * 解析自动标注任务的处理进度（临时方案）
     * 解析格式如: "[3/8] 处理: filename.jpg"
     *
     * @param logLine 日志行
     * @return 进度值(0-100)，解析失败返回-1
     */
    public static int parseAutoMarkProgress(String logLine) {
        try {
            if (logLine.contains("] 处理")) {
                // 查找 [x/y] 格式
                int startBracket = logLine.indexOf('[');
                int endBracket = logLine.indexOf(']');

                if (startBracket >= 0 && endBracket > startBracket) {
                    String progressPart = logLine.substring(startBracket + 1, endBracket);
                    if (progressPart.contains("/")) {
                        String[] parts = progressPart.split("/");
                        if (parts.length == 2) {
                            try {
                                int current = Integer.parseInt(parts[0].trim());
                                int total = Integer.parseInt(parts[1].trim());

                                if (total > 0) {
                                    int progress = (int) ((current * 100.0) / total);
                                    log.debug("解析自动标注进度: {}/{} = {}%", current, total, progress);
                                    return Math.min(progress, 100); // 确保不超过100%
                                }
                            } catch (NumberFormatException e) {
                                log.warn("解析进度数字失败: {}", progressPart);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析自动标注进度失败: {}", e.getMessage());
        }
        return -1;
    }

    /**
     * 解析DLC日志中的进度信息（综合方案）
     * 首先尝试JSON格式，失败则尝试自动标注格式
     *
     * @param logLine     日志行
     * @param progressKey JSON进度字段名（如"progress"）
     * @return 进度值(0-100)，解析失败返回-1
     */
    public static int parseAnyProgressFromLog(String logLine, String progressKey) {
        // 优先尝试JSON格式
        int jsonProgress = parseProgressFromLog(logLine, progressKey);
        if (jsonProgress >= 0) {
            return jsonProgress;
        }

        // 备用方案：解析自动标注格式
        int autoMarkProgress = parseAutoMarkProgress(logLine);
        if (autoMarkProgress >= 0) {
            return autoMarkProgress;
        }

        return -1;
    }

    /**
     * 创建标准的Canvas训练DLC任务
     *
     * @param jobName      任务名
     * @param imageUri     镜像URI
     * @param instanceType 实例类型
     * @param mountList    挂载列表
     * @return CreateJobResponseBody
     */
    public static CreateJobResponseBody createCanvasTrainTask(String jobName, String imageUri,
                                                              String instanceType, List<DlcCreateOssDTO> mountList) {
        return createDlcTask(jobName, imageUri, instanceType, mountList,
                "cd /opt/ml/code && python /opt/ml/code/train.py");
    }

    /**
     * 创建自动标注DLC任务
     *
     * @param jobName      任务名
     * @param imageUri     镜像URI (默认: registry-vpc.cn-hangzhou.aliyuncs.com/aidojo-registry/automark:0.2.0)
     * @param instanceType 实例类型
     * @param mountList    挂载列表
     * @return CreateJobResponseBody
     */
    public static CreateJobResponseBody createAutoMarkTask(String jobName, String imageUri,
                                                           String instanceType, List<DlcCreateOssDTO> mountList) {
        return createDlcTask(jobName, imageUri, instanceType, mountList,
                "cd /app && ls -la && ls -la images/ && python explore_cache.py && python fix_bert_path.py && python run_config.py");
    }

    /**
     * 创建自动标注DLC任务（使用默认镜像和GPU实例）
     *
     * @param jobName   任务名
     * @param mountList 挂载列表
     * @return CreateJobResponseBody
     */
    public static CreateJobResponseBody createAutoMarkTask(String jobName, List<DlcCreateOssDTO> mountList) {
        return createAutoMarkTask(jobName, "registry-vpc.cn-hangzhou.aliyuncs.com/aidojo-registry/automark:0.2.0",
                                 "ecs.gn6i-c4g1.xlarge", mountList);
    }

    /**
     * 创建自动标注DLC任务（使用默认镜像，自定义实例类型）
     *
     * @param jobName      任务名
     * @param instanceType 实例类型 (推荐使用GPU实例 ecs.gn6i-c4g1.xlarge 以获得更好的推理性能)
     * @param mountList    挂载列表
     * @return CreateJobResponseBody
     */
    public static CreateJobResponseBody createAutoMarkTask(String jobName, String instanceType, List<DlcCreateOssDTO> mountList) {
        return createAutoMarkTask(jobName, "registry-vpc.cn-hangzhou.aliyuncs.com/aidojo-registry/automark:0.2.0", instanceType, mountList);
    }
}