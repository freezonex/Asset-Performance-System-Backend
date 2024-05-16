package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/15 18:20
 */
@Data
public class ValueModelDataReq {
    @NotNull
    private Long assetTypeId;
    @NotNull
    @Min(0)
    private Integer modelType;
}
