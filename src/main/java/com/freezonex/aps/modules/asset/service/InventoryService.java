package com.freezonex.aps.modules.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.model.Inventory;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    CommonPage<InventoryDetailListDTO> queryByAssetTypeList(InventoryByAssetTypeListReq req);

    CommonPage<SafetyLevelAssetTypeListDTO> safetyLevelAssetTypeList(InventorySafetyLevelAssetTypeReq req);

    CommonPage<SafetyLevelAssetTypeQuantityListDTO> queryAssetTypeQuantity(InventorySafetyLevelAssetTypeReq req);

    InventoryChartDataDTO queryChartData(InventoryChartDataReq req);

    /**
     * 计算每天库存数量
     *
     * @param assetList 资产列表
     * @param dates     日期区间
     * @return 每日库存数量
     */
    Map<LocalDate, Integer> calculateDateQuantity(List<AssetListDTO> assetList, List<Date> dates);

    /**
     * 填充预期数据 保证预期间隔每天都有数据
     */
    List<Inventory> fillInventory(Long assetTypeId, List<Inventory> list);

    Boolean create(InventoryCreateReq req);
}
