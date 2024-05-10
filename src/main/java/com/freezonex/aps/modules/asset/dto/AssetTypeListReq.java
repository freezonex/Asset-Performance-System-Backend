package com.freezonex.aps.modules.asset.dto;

import com.freezonex.aps.common.api.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

/**
 * @author penglifr
 * @since 2024/05/10 15:18
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AssetTypeListReq extends BasePage {

    private String assetType;

    private Collection<Long> assetTypeIds;
}
