package com.freezonex.aps.modules.asset.dto;

import lombok.*;

/**
 * @author penglifr
 * @since 2024/05/08 13:36
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SafetyLevelAssetTypeQuantityListDTO extends SafetyLevelAssetTypeListDTO{
    private Long quantity;
    public SafetyLevelAssetTypeQuantityListDTO(){

    }

    public SafetyLevelAssetTypeQuantityListDTO(SafetyLevelAssetTypeListDTO dto, Long quantity) {
        super(dto.getId(), dto.getAssetType(), dto.getIcon());
        this.quantity = quantity;
    }
}
