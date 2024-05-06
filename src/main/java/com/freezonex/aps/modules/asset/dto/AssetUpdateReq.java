package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/06 15:55
 */
@Data
public class AssetUpdateReq extends AssetCreateReq{
    @NotNull
    private Long id;
}
