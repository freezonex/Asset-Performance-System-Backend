package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author penglifr
 * @since 2024/05/07 13:49
 */
@Data
public class AssetTypeListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String assetType;

    private String unit;

    private String supplierName;

    private String icon;

    private Integer priceValue;

}
