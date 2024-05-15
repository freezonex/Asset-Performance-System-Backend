package com.freezonex.aps.modules.asset.convert;

import com.freezonex.aps.modules.asset.dto.AssetTypeListDTO;
import com.freezonex.aps.modules.asset.dto.MaintenanceDTO;
import com.freezonex.aps.modules.asset.model.AssetType;
import com.freezonex.aps.modules.asset.model.Maintenance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MaintenanceConvert {
    MaintenanceDTO toDTO(Maintenance maintenance);
}
