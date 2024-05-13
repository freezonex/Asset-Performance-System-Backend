package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/13 9:58
 */
@Data
public class AssetUsedStatusReq {
    @NotNull
    private Long id;
}
