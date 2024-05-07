package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author penglifr
 * @since 2024/05/07 15:52
 */
@Data
public class SafetyLevelAssetTypeListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String assetType;

    private String icon;
}
