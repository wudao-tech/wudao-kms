package com.wudao.kms.util.pai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DlcCreateDTO {
    @Schema(description = "挂载路径")
    private List<DlcCreateOssDTO> mountList;
    @Schema(description = "运行镜像")
    private String image;
    @Schema(description = "节点数量，默认1")
    private Long podCount = 1L;
    @Schema(description = "实例")
    private String instanceType;
    @Schema(description = "执行命令")
    private String command;
    @Schema(description = "任务名称")
    private String taskName;
    @Schema(description = "任务类型，默认PyTorchJob")
    private String jobType = "PyTorchJob";
}
