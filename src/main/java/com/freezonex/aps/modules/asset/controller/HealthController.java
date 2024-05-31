package com.freezonex.aps.modules.asset.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = "HealthController")
@Tag(name = "HealthController",description = "health")
public class HealthController {

    private static final Logger logger = LogManager.getLogger(HealthController.class);
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        logger.info("health is ok!");
        return "aps-health-is-ok-!";
    }


    @ApiOperation("page")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<String>> list() {
        Page<String> menuList = new Page<>(1,10);
        return CommonResult.success(CommonPage.restPage(menuList));
    }

    @ApiOperation("failed1")
    @RequestMapping(value = "/failed1", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<String>> failed1() {
        throw new RuntimeException();
    }

    @ApiOperation("failed2")
    @RequestMapping(value = "/failed2", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<String>> failed2() {
        return CommonResult.failed("Custom error message!");
    }

}
