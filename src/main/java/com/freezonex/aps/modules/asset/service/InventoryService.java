package com.freezonex.aps.modules.asset.service;

import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.dto.InventoryListDTO;
import com.freezonex.aps.modules.asset.dto.InventoryListReq;
import com.freezonex.aps.modules.asset.dto.SafetyLevelAssetTypeListDTO;
import com.freezonex.aps.modules.asset.model.Inventory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    List<SafetyLevelAssetTypeListDTO> safetyLevelAssetTypeList();
}
