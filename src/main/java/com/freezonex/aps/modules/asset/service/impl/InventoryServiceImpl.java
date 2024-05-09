package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.convert.InventoryConvert;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.mapper.InventoryMapper;
import com.freezonex.aps.modules.asset.model.Inventory;
import com.freezonex.aps.modules.asset.service.AssetService;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import com.freezonex.aps.modules.asset.service.InventoryService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * asset inventory 服务实现类
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    @Resource
    private InventoryConvert inventoryConvert;
    @Resource
    private AssetService assetService;
    @Resource
    private AssetTypeService assetTypeService;

    @Override
    public CommonPage<InventoryListDTO> list(InventoryListReq req) {
        //1.分页查询已有记录品类
        CommonPage<AssetTypeListDTO> assetTypePage = assetTypeService.list(req);
        //2.获取已有品类的记录数据预期时间距离今天最近一条数据
        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        query.in(Inventory::getAssetTypeId, assetTypePage.getList().stream().map(AssetTypeListDTO::getId).collect(Collectors.toList()));
        query.orderByAsc(Inventory::getAssetTypeId, Inventory::getExpectedDate);
        List<Inventory> inventoryList = this.list(query);
        Map<Long, Inventory> map = getNearestInventory(inventoryList);
        //3.组装返回结果
        List<InventoryListDTO> resultList = Lists.newArrayList();
        for (AssetTypeListDTO assetTypeListDTO : assetTypePage.getList()) {
            Long assetTypeId = assetTypeListDTO.getId();
            if (map.containsKey(assetTypeId)) {
                resultList.add(inventoryConvert.toDTO(map.get(assetTypeId)));
            } else {
                //初始化一条数据
                resultList.add(inventoryConvert.toDTO(initInventory(assetTypeListDTO)));
            }
        }
        CommonPage<InventoryListDTO> inventoryPage = new CommonPage<>();
        inventoryPage.setPageNum(assetTypePage.getPageNum());
        inventoryPage.setPageSize(assetTypePage.getPageSize());
        inventoryPage.setTotalPage(assetTypePage.getTotalPage());
        inventoryPage.setTotal(assetTypePage.getTotal());
        inventoryPage.setList(resultList);
        return inventoryPage;
    }

    /**
     * 获取距离今天最近的一条记录
     */
    private Map<Long, Inventory> getNearestInventory(List<Inventory> inventoryList) {
        Map<Long, Inventory> map = new HashMap<>();
        Date today = new Date();
        inventoryList.forEach(v -> {
            if (v.getExpectedDate().before(today)) {
                map.put(v.getAssetTypeId(), v);
            } else {
                Inventory inventory = map.get(v.getAssetTypeId());
                if (inventory != null) {
                    if (inventory.getExpectedDate().before(today)) {
                        map.put(v.getAssetTypeId(), v);
                    }
                } else {
                    map.put(v.getAssetTypeId(), v);
                }

            }
        });
        return map;
    }

    private Inventory initInventory(AssetTypeListDTO assetTypeListDTO) {
        Inventory inventory = new Inventory();
        inventory.setId(0L);
        inventory.setAssetTypeId(assetTypeListDTO.getId());
        inventory.setAssetType(assetTypeListDTO.getAssetType());
        inventory.setQuantity(0);
        inventory.setUnit(assetTypeListDTO.getUnit());
        inventory.setUsageRate(0);
        inventory.setSupplierName(assetTypeListDTO.getSupplierName());
        inventory.setExpectedQuantity(0);
        inventory.setCreationTime(new Date());
        inventory.setExpectedDate(new Date());
        return inventory;
    }

    @Override
    public CommonPage<InventoryDetailListDTO> queryByAssetTypeList(InventoryByAssetTypeListReq req) {
        Page<Inventory> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        query.eq(Inventory::getAssetTypeId, req.getAssetTypeId());
        query.orderByAsc(Inventory::getExpectedDate);
        Page<Inventory> assetPage = this.getBaseMapper().selectPage(page, query);
        return CommonPage.restPage(assetPage, inventoryConvert::toDetailDTO);
    }

    @Override
    public CommonPage<SafetyLevelAssetTypeListDTO> safetyLevelAssetTypeList(InventorySafetyLevelAssetTypeReq req) {
        CommonPage<AssetTypeListDTO> data = assetTypeService.list(req);
        List<SafetyLevelAssetTypeListDTO> records = Lists.newArrayList();
        for (AssetTypeListDTO dto : data.getList()) {
            SafetyLevelAssetTypeListDTO safetyLevelAssetTypeListDTO = new SafetyLevelAssetTypeListDTO();
            safetyLevelAssetTypeListDTO.setId(dto.getId());
            safetyLevelAssetTypeListDTO.setAssetType(dto.getAssetType());
            safetyLevelAssetTypeListDTO.setIcon(dto.getIcon());
            records.add(safetyLevelAssetTypeListDTO);
        }
        CommonPage<SafetyLevelAssetTypeListDTO> pageResult = new CommonPage<>();
        pageResult.setPageNum(data.getPageNum());
        pageResult.setPageSize(data.getPageSize());
        pageResult.setTotalPage(data.getTotalPage());
        pageResult.setTotal(data.getTotal());
        pageResult.setList(records);
        return pageResult;
    }

    public CommonPage<SafetyLevelAssetTypeQuantityListDTO> queryAssetTypeQuantity(InventorySafetyLevelAssetTypeReq req) {
        CommonPage<SafetyLevelAssetTypeListDTO> assetTypePage = this.safetyLevelAssetTypeList(req);
        Map<Long, SafetyLevelAssetTypeListDTO> assetTypeMap = assetTypePage.getList().stream().collect(Collectors.toMap(SafetyLevelAssetTypeListDTO::getId, v -> v));
        Map<Long, Long> assetTypeQuantityMap = assetService.queryGroupByAssetType(assetTypeMap.keySet());
        List<SafetyLevelAssetTypeQuantityListDTO> records = new ArrayList<>();
        assetTypeMap.forEach((k, v) -> {
            Long quantity = assetTypeQuantityMap.getOrDefault(k, 0L);
            SafetyLevelAssetTypeQuantityListDTO dto = new SafetyLevelAssetTypeQuantityListDTO(v, quantity);
            records.add(dto);
        });
        CommonPage<SafetyLevelAssetTypeQuantityListDTO> result = new CommonPage<>();
        result.setPageNum(assetTypePage.getPageNum());
        result.setPageSize(assetTypePage.getPageSize());
        result.setTotalPage(assetTypePage.getTotalPage());
        result.setTotal(assetTypePage.getTotal());
        result.setList(records);
        return result;
    }

}
