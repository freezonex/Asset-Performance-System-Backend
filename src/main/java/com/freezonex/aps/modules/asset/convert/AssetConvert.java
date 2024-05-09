package com.freezonex.aps.modules.asset.convert;

import com.freezonex.aps.modules.asset.dto.AssetCreateReq;
import com.freezonex.aps.modules.asset.dto.AssetListDTO;
import com.freezonex.aps.modules.asset.model.Asset;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssetConvert {
    AssetListDTO toDTO(Asset asset);
    Asset toAsset(AssetCreateReq req);
}
