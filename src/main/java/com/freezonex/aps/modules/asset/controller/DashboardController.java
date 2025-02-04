package com.freezonex.aps.modules.asset.controller;

import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author penglifr
 * @since 2024/05/14 10:18
 */
@RestController
@RequestMapping("/apsbackend/dashboard")
@Api(tags = "DashboardController")
@Tag(name = "DashboardController", description = "dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @ApiOperation("total assets")
    @RequestMapping(value = "/totalAssets", method = RequestMethod.POST)
    public CommonResult<TotalAssetDTO> totalAsset() {
        return CommonResult.success(dashboardService.totalAsset());
    }

    @ApiOperation("work orders")
    @RequestMapping(value = "/totalWorkOrders", method = RequestMethod.POST)
    public CommonResult<TotalWorkOrderDTO> totalWorkOrder() {
        return CommonResult.success(dashboardService.totalWorkOrder());
    }

    @ApiOperation("work orders queue")
    @RequestMapping(value = "/workOrdersQueue", method = RequestMethod.POST)
    public CommonResult<List<WorkOrderListDTO>> workOrdersQueue() {
        return CommonResult.success(dashboardService.workOrdersQueue());
    }

    @ApiOperation("event list")
    @RequestMapping(value = "/eventList", method = RequestMethod.POST)
    public CommonResult<List<EventListDTO>> eventList(@RequestBody @Validated EventListReq req) {
        return CommonResult.success(dashboardService.eventList(req));
    }

}
