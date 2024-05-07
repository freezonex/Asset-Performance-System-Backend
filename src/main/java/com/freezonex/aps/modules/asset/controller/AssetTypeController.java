package com.freezonex.aps.modules.asset.controller;


import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.modules.asset.dto.AssetTypeListDTO;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * asset type 前端控制器
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
@RestController
@RequestMapping("/assetType")
@Api(tags = "AssetTypeController")
@Tag(name = "AssetTypeController",description = "asset type")
public class AssetTypeController {

    @Resource
    private AssetTypeService assetTypeService;

    @ApiOperation("Asset type all list")
    @RequestMapping(value = "/allList", method = RequestMethod.GET)
    public CommonResult<List<AssetTypeListDTO>> allList() {
        return CommonResult.success(assetTypeService.allList());
    }

}

