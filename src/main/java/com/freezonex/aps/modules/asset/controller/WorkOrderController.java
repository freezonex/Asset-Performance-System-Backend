package com.freezonex.aps.modules.asset.controller;


import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.service.WorkOrderService;
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
 * work order 前端控制器
 * </p>
 *
 * @author supos
 * @since 2024-05-09
 */
@RestController
@RequestMapping("/workOrder")
@Api(tags = "WorkOrderController")
@Tag(name = "WorkOrderController",description = "work order")
public class WorkOrderController {

    @Resource
    private WorkOrderService workOrderService;

    @ApiOperation("WorkOrder list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<WorkOrderListDTO>> list(@RequestBody WorkOrderListReq req) {
        return CommonResult.success(workOrderService.list(req));
    }

    @ApiOperation("WorkOrder create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Boolean> create(@RequestBody @Validated WorkOrderCreateReq req) {
        return CommonResult.success(workOrderService.create(req));
    }

    @ApiOperation("WorkOrder update")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<Boolean> update(@RequestBody @Validated WorkOrderUpdateReq req) {
        return CommonResult.success(workOrderService.update(req));
    }

    @ApiOperation("WorkOrder delete")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<Boolean> delete(@RequestBody @Validated WorkOrderDeleteReq req) {
        return CommonResult.success(workOrderService.delete(req));
    }

}

