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
@RequestMapping("/inventory")
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
    @RequestMapping(value = "/queryByAssetTypeList", method = RequestMethod.GET)
    public CommonResult<CommonPage<InventoryDetailListDTO>> queryByAssetTypeList(@Validated @RequestBody InventoryByAssetTypeListReq req) {
        return CommonResult.success(inventoryService.queryByAssetTypeList(req));
    }

    @ApiOperation("Safety level asset type list")
    @RequestMapping(value = "/assetType/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SafetyLevelAssetTypeListDTO>> safetyLevelAssetTypeList(InventorySafetyLevelAssetTypeReq req) {
        return CommonResult.success(inventoryService.safetyLevelAssetTypeList(req));
    }

    @ApiOperation("Safety level asset type quantity list")
    @RequestMapping(value = "/assetTypeQuantity/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SafetyLevelAssetTypeQuantityListDTO>> queryAssetTypeQuantity(InventorySafetyLevelAssetTypeReq req) {
        return CommonResult.success(inventoryService.queryAssetTypeQuantity(req));
    }

}

