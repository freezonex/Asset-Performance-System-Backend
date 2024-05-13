package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author penglifr
 * @since 2024/05/13 13:41
 */
@Data
public class AssetTypeCreateReq {

    @NotEmpty
    private String assetType;

    private String unit;

    private String supplierName;
}
