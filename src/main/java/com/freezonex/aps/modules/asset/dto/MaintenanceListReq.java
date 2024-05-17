package com.freezonex.aps.modules.asset.dto;

import com.freezonex.aps.common.api.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/15 11:34
 */
@Data
public class MaintenanceListReq extends BasePage {
    @NotNull
    private Long assetTypeId;
    @NotNull
    @ApiModelProperty(
            value = "0：Planned 1：Completed"
    )
    private Integer status;

    private String searchContent;
}
