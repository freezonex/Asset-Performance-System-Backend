package com.freezonex.aps.modules.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.model.Inventory;

/**
 * <p>
 * asset inventory 服务类
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
public interface InventoryService extends IService<Inventory> {

    CommonPage<InventoryListDTO> list(InventoryListReq req);

    CommonPage<InventoryListDTO> queryByAssetTypeList(InventoryByAssetTypeListReq req);

    CommonPage<SafetyLevelAssetTypeListDTO> safetyLevelAssetTypeList(InventorySafetyLevelAssetTypeReq req);

    CommonPage<SafetyLevelAssetTypeQuantityListDTO> queryAssetTypeQuantity(InventorySafetyLevelAssetTypeReq req);


}
