package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/06 16:00
 */
@Data
public class AssetDeleteReq {
    @NotNull
    private Long id;
}
