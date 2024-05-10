package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/10 13:55
 */
@Data
public class InventoryChartDataReq {
    @NotNull
    private Long assetTypeId;
}
