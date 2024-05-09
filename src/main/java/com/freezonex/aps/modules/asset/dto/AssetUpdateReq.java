package com.freezonex.aps.modules.asset.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/06 15:55
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AssetUpdateReq extends AssetCreateReq{
    @NotNull
    private Long id;
}
