package com.freezonex.aps.modules.asset.controller;


import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.service.InventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * asset inventory 前端控制器
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
@RestController
@RequestMapping("/apsbackend/inventory")
@Api(tags = "InventoryController")
@Tag(name = "InventoryController",description = "asset inventory")
public class InventoryController {

    @Resource
    private InventoryService inventoryService;

    @ApiOperation("Inventory list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<InventoryListDTO>> list(@RequestBody InventoryListReq req) {
        return CommonResult.success(inventoryService.list(req));
    }

    @ApiOperation("Inventory Detail list query by asset type")
    @RequestMapping(value = "/queryByAssetTypeList", method = RequestMethod.POST)
    public CommonResult<CommonPage<InventoryDetailListDTO>> queryByAssetTypeList(@Validated @RequestBody InventoryByAssetTypeListReq req) {
        return CommonResult.success(inventoryService.queryByAssetTypeList(req));
    }

    @ApiOperation("Safety level asset type list")
    @RequestMapping(value = "/assetType/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<SafetyLevelAssetTypeListDTO>> safetyLevelAssetTypeList(@RequestBody InventorySafetyLevelAssetTypeReq req) {
        return CommonResult.success(inventoryService.safetyLevelAssetTypeList(req));
    }

    @ApiOperation("Safety level asset type quantity list")
    @RequestMapping(value = "/assetTypeQuantity/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<SafetyLevelAssetTypeQuantityListDTO>> queryAssetTypeQuantity(@RequestBody InventorySafetyLevelAssetTypeReq req) {
        return CommonResult.success(inventoryService.queryAssetTypeQuantity(req));
    }

    @ApiOperation("Trend chart data")
    @RequestMapping(value = "/queryChartData", method = RequestMethod.POST)
    public CommonResult<InventoryChartDataDTO> queryChartData(@RequestBody @Validated InventoryChartDataReq req) {
        return CommonResult.success(inventoryService.queryChartData(req));
    }

    @ApiOperation("Asset type expected create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Boolean> create(@RequestBody @Validated InventoryCreateReq req) {
        return CommonResult.success(inventoryService.create(req));
    }

}

