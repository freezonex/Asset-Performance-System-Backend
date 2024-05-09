package com.freezonex.aps.modules.asset.convert;

import com.freezonex.aps.modules.asset.dto.InventoryDetailListDTO;
import com.freezonex.aps.modules.asset.dto.InventoryListDTO;
import com.freezonex.aps.modules.asset.model.Inventory;
import org.mapstruct.Mapper;

/**
 * @author penglifr
 * @since 2024/05/07 13:42
 */
@Mapper(componentModel = "spring")
public interface InventoryConvert {
    InventoryListDTO toDTO(Inventory inventory);
    InventoryDetailListDTO toDetailDTO(Inventory inventory);

}
