package com.wudao.kms.util.pai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DlcCreateOssDTO {
    private String ossUri;
    private String mountPath;
}
