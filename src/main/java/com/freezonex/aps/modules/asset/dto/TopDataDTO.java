package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author penglifr
 * @since 2024/05/15 14:46
 */
@Data
public class TopDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long assetTypeId;

    private String assetType;

    private Integer priceValue;
}
