package com.freezonex.aps.modules.asset.dto;

import com.freezonex.aps.common.api.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author penglifr
 * @since 2024/05/06 14:15
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AssetListReq extends BasePage {

    private String assetId;

    private String assetName;

    private String assetType;

    private String responsiblePerson;

}
