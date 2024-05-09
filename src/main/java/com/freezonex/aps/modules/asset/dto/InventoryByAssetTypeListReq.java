package com.freezonex.aps.modules.asset.dto;

import com.freezonex.aps.common.api.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/08 15:58
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class InventoryByAssetTypeListReq extends BasePage {
    @NotNull
    private Long assetTypeId;
}
