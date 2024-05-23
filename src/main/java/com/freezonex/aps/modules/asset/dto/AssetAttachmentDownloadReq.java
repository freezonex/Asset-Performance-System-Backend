package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/11 14:17
 */
@Data
public class AssetAttachmentDownloadReq {
    @NotNull
    private Long id;

    private Integer type;
}
