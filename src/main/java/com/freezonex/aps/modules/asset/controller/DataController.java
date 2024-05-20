package com.freezonex.aps.modules.asset.controller;

import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.modules.asset.service.DataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/apsbackend/data")
@Api(tags = "DataController")
@Tag(name = "DataController", description = "data init")
public class DataController {

    @Resource
    private DataService dataService;

    @ApiOperation("Data generation")
    @RequestMapping(value = "/generation", method = RequestMethod.POST)
    public CommonResult<Boolean> generation() {
        return CommonResult.success(dataService.initData());
    }
}
