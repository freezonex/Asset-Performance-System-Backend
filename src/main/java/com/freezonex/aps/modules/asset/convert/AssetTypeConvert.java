package com.freezonex.aps.modules.asset.convert;

import com.freezonex.aps.modules.asset.dto.AssetTypeListDTO;
import com.freezonex.aps.modules.asset.model.AssetType;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author penglifr
 * @since 2024/05/07 13:51
 */
@Mapper(componentModel = "spring")
public interface AssetTypeConvert {
    AssetTypeListDTO toDTO(AssetType assetType);

    List<AssetTypeListDTO> toDTOList(List<AssetType> assetTypeList);

}
