package com.freezonex.aps.modules.asset.convert;

import com.freezonex.aps.modules.asset.dto.AssetCreateReq;
import com.freezonex.aps.modules.asset.dto.AssetListDTO;
import com.freezonex.aps.modules.asset.model.Asset;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssetConvert {
    AssetListDTO toDTO(Asset asset);
    List<AssetListDTO> toDTOList(List<Asset> assetList);
    Asset toAsset(AssetCreateReq req);
}
