package com.freezonex.aps.modules.asset.controller;


import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.service.AssetService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * asset 前端控制器
 * </p>
 *
 * @author supos
 * @since 2024-05-06
 */
@RestController
@RequestMapping("/asset")
public class AssetController {

    @Resource
    private AssetService assetService;

    @ApiOperation("asset list")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<AssetListDTO>> list(@RequestBody AssetListReq req) {
        return CommonResult.success(assetService.list(req));
    }

    @ApiOperation("asset create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Boolean> create(@RequestBody @Validated AssetCreateReq req) {
        return CommonResult.success(assetService.create(req));
    }

    @ApiOperation("asset update")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Boolean> update(@RequestBody @Validated AssetUpdateReq req) {
        return CommonResult.success(assetService.update(req));
    }

    @ApiOperation("asset delete")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<Boolean> delete(@RequestBody @Validated AssetDeleteReq req) {
        return CommonResult.success(assetService.delete(req));
    }

}

