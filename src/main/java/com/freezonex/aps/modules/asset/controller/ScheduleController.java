package com.freezonex.aps.modules.asset.controller;

import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.modules.asset.dto.ScheduleFormDataDTO;
import com.freezonex.aps.modules.asset.dto.ScheduleFormDataReq;
import com.freezonex.aps.modules.asset.dto.ScheduleHeadDataDTO;
import com.freezonex.aps.modules.asset.service.ScheduleService;
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
 * @author penglifr
 * @since 2024/05/10 15:36
 */
@RestController
@RequestMapping("/schedule")
@Api(tags = "ScheduleController")
@Tag(name = "ScheduleController", description = "scheduler")
public class ScheduleController {

    @Resource
    private ScheduleService scheduleService;

    @ApiOperation("Schedule head data")
    @RequestMapping(value = "/queryHeadData", method = RequestMethod.POST)
    public CommonResult<ScheduleHeadDataDTO> queryHeadData() {
        return CommonResult.success(scheduleService.queryHeadData());
    }

    @ApiOperation("Schedule form data")
    @RequestMapping(value = "/queryFormData", method = RequestMethod.POST)
    public CommonResult<ScheduleFormDataDTO> queryFormData(@RequestBody @Validated ScheduleFormDataReq req) {
        return CommonResult.success(scheduleService.queryFormData(req));
    }
}
