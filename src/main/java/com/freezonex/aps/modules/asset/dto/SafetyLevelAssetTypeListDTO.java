package com.freezonex.aps.modules.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author penglifr
 * @since 2024/05/07 15:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SafetyLevelAssetTypeListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String assetType;

    private String icon;
}
