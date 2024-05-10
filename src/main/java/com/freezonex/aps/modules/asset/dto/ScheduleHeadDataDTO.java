package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author penglifr
 * @since 2024/05/10 15:56
 */
@Data
public class ScheduleHeadDataDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer workRecords;
    private Integer issuesWorkRecords;
    private Integer usageRate;
    private Integer totalAssets;
}
