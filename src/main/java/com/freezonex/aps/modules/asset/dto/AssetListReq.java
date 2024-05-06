package com.freezonex.aps.modules.asset.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freezonex.aps.common.api.BasePage;
import lombok.Data;

/**
 * @author penglifr
 * @since 2024/05/06 14:15
 */
@Data
public class AssetListReq extends BasePage {

    private String assetId;

    private String assetName;

    private Integer assetType;

    private String responsiblePerson;

}
