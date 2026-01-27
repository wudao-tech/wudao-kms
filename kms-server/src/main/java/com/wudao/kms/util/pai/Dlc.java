package com.wudao.kms.util.pai;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.sdk.service.pai_dlc20201203.AsyncClient;
import com.aliyun.sdk.service.pai_dlc20201203.models.CreateJobRequest;
import com.aliyun.sdk.service.pai_dlc20201203.models.CreateJobResponse;
import com.aliyun.sdk.service.pai_dlc20201203.models.CreateJobResponseBody;
import com.aliyun.sdk.service.pai_dlc20201203.models.GetJobRequest;
import com.aliyun.sdk.service.pai_dlc20201203.models.GetJobResponse;
import com.aliyun.sdk.service.pai_dlc20201203.models.GetJobResponseBody;
import com.aliyun.sdk.service.pai_dlc20201203.models.GetPodLogsRequest;
import com.aliyun.sdk.service.pai_dlc20201203.models.GetPodLogsResponse;
import com.aliyun.sdk.service.pai_dlc20201203.models.ImageConfig;
import com.aliyun.sdk.service.pai_dlc20201203.models.JobSpec;
import com.aliyun.sdk.service.pai_dlc20201203.models.StopJobRequest;
import com.aliyun.sdk.service.pai_dlc20201203.models.StopJobResponse;
import com.wudao.common.exception.ServiceException;
import com.wudao.common.utils.StringUtils;
import com.wudao.kms.util.pai.dto.DlcCreateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * pai平台的dlc运行类
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Dlc {
    private final StringRedisTemplate stringRedisTemplate;
    private final AsyncClient asyncClient;


    /**
     * 创建任务
     *
     * @param createDTO 任务dto
     * @return 返回创建任务的job
     */
    public CreateJobResponseBody createTask(DlcCreateDTO createDTO) {
        // 处理mount - 从DTO中的mountList动态生成DataSources
        List<CreateJobRequest.DataSources> dataSources = createDTO.getMountList().stream()
                .map(mount -> CreateJobRequest.DataSources.builder()
                        .uri(mount.getOssUri())
                        .mountPath(mount.getMountPath())
                        .build())
                .collect(Collectors.toList());


        // Parameter settings for API request
        ImageConfig jobSpec0ImageConfig = ImageConfig.builder()
                .dockerRegistry("wudao-registry.cn-hangzhou.cr.aliyuncs.com")
                .username("bill.huang@wudao-tech.com")
                .password("Pwdwudao123..")
                .build();
        JobSpec jobSpec0 = JobSpec.builder()
                .type("Worker")
                .image(createDTO.getImage())
                .imageConfig(jobSpec0ImageConfig)
                .podCount(createDTO.getPodCount())
                .ecsSpec(createDTO.getInstanceType())
                .build();
         CreateJobRequest createJobRequest = CreateJobRequest.builder()
                .jobType(createDTO.getJobType())
                .displayName(createDTO.getTaskName())
//                .userCommand("cd /opt/ml/code && python /opt/ml/code/train.py")
                .userCommand(createDTO.getCommand())
                .jobSpecs(List.of(jobSpec0))
                .dataSources(dataSources)
                .build();
        // Asynchronously get the return value of the API request
        CompletableFuture<CreateJobResponse> response = asyncClient.createJob(createJobRequest);
        // Asynchronous processing of return values
        try {
            // 设置60秒超时，创建任务通常需要更长时间
            CreateJobResponse resp = response.get(60, java.util.concurrent.TimeUnit.SECONDS);
            log.info("DLC任务创建成功，JobId: {}", resp.getBody().getJobId());
            return resp.getBody();
        } catch (java.util.concurrent.TimeoutException e) {
            log.error("创建DLC任务超时", e);
            throw new RuntimeException("创建DLC任务超时，请稍后重试", e);
        } catch (Exception e) {
            log.error("创建任务报错", e);
            throw new RuntimeException("创建DLC任务失败: " + e.getMessage(), e);
        }
//        return CreateJobResponseBody.builder().jobId("dlcmnzn60oly4zbu").build();
    }

    /**
     * 根据jobId查看pod信息
     *
     * @param jobId 任务id
     * @return 任务详情
     */
    private GetJobResponse getPod(String jobId) {
        GetJobRequest jobRequest = GetJobRequest.builder().jobId(jobId).build();
        CompletableFuture<GetJobResponse> jobDetail = asyncClient.getJob(jobRequest);
        AtomicReference<GetJobResponse> response = new AtomicReference<>();
//        jobDetail.thC(resp -> {
//            // 判断状态
//            if (resp.getStatusCode() != HttpStatus.SC_OK) {
//                throw new ServiceException("获取任务详情失败");
//            }
//            response.set(resp);
//        }).exceptionally(throwable -> {
//            throw new ServiceException(throwable.getMessage());
//        });
        try {
            GetJobResponse getJobResponse = jobDetail.get();
            return getJobResponse;
        } catch (Exception e) {
            throw new ServiceException("获取任务详情失败");
        }
    }

    /**
     * 停止任务
     *
     * @param jobId jobId
     * @return 停止任务结果
     */
    public StopJobResponse stopTask(String jobId) {
        StopJobRequest stopJobRequest = StopJobRequest.builder().jobId(jobId).build();
        CompletableFuture<StopJobResponse> response = asyncClient.stopJob(stopJobRequest);
        try {
            return response.get();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 查询日志
     *
     * @param jobId         任务ID
     * @param lastQueryTime 上一次查询时间
     * @return 上一次到当前的日志列表
     */
    public GetPodLogsResponse getLog(String jobId, String lastQueryTime) throws Exception {
        return getLog(jobId, lastQueryTime, null);
    }

    /**
     * 查询日志
     *
     * @param jobId     任务ID
     * @param startTime 开始查询时间
     * @param endTime   结束查询时间（可为null）
     * @return 指定时间范围内的日志列表
     */
    public GetPodLogsResponse getLog(String jobId, String startTime, String endTime) throws Exception {
        String podKey = "model_train:pod:%s";
        podKey = String.format(podKey, jobId);
        String podId = stringRedisTemplate.opsForValue().get(podKey);
        if (StringUtils.isEmpty(podId)) {
            List<GetJobResponseBody.Pods> podsList = getPod(jobId).getBody().getPods();
            if (podsList.isEmpty()) {
                log.warn("还没有创建完成");
                return null;
            }
            podId = podsList.get(0).getPodId();
            stringRedisTemplate.opsForValue().set(podKey, podId);
        }

        GetPodLogsRequest.Builder requestBuilder = GetPodLogsRequest.builder()
                .jobId(jobId)
                .podId(podId)
                .startTime(startTime);

        // 如果指定了结束时间，则添加endTime参数
        if (StringUtils.isNotEmpty(endTime)) {
            requestBuilder.endTime(endTime);
        }

        GetPodLogsRequest getPodLogsRequest = requestBuilder.build();

        CompletableFuture<GetPodLogsResponse> logs = asyncClient.getPodLogs(getPodLogsRequest);
        // 设置30秒超时
        GetPodLogsResponse getPodLogsResponse = logs.get(30, java.util.concurrent.TimeUnit.SECONDS);
        log.debug("查询DLC日志成功，返回数据: {}", JSONObject.toJSONString(getPodLogsResponse));
        return getPodLogsResponse;
    }

    public GetJobResponse getStatus(String jobId) throws Exception {
        GetJobRequest getJobRequest = GetJobRequest.builder().jobId(jobId).build();
        CompletableFuture<GetJobResponse> response = asyncClient.getJob(getJobRequest);
        // 设置30秒超时
        return response.get(30, java.util.concurrent.TimeUnit.SECONDS);
    }
}
