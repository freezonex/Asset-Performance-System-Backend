package com.freezonex.aps.modules.asset.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/15 13:52
 */
@Data
public class MaintenanceDownloadReq {
    @NotNull
    @Max(3)
    @Min(1)
    @ApiModelProperty(
            value = "1:Historical Maintenance Log 2:Maintenance Check Interval 3:Asset VaIue depreciation model"
    )
    private Integer type;
}
