package com.freezonex.aps.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(tags = "HealthController")
@Tag(name = "HealthController",description = "心跳检测")
public class HealthController {
    @GetMapping("/health")
    public String health() {
        log.info("App health check.");
        return "health";
    }


    @ApiOperation("分页")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<String>> list() {
        Page<String> menuList = new Page<>(1,10);
        return CommonResult.success(CommonPage.restPage(menuList));
    }

}
