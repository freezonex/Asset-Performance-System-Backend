package com.freezonex.aps.modules.asset.controller;


import cn.hutool.core.util.URLUtil;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.service.MaintenanceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * <p>
 * maintenance 前端控制器
 * </p>
 *
 * @author supos
 * @since 2024-05-15
 */
@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Resource
    private MaintenanceService maintenanceService;

    @ApiOperation("top 5 data")
    @RequestMapping(value = "/topData", method = RequestMethod.POST)
    public CommonResult<List<TopDataDTO>> topData() {
        return CommonResult.success(maintenanceService.topData());
    }

    @ApiOperation("maintenance list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<CommonPage<MaintenanceDTO>> list(@RequestBody MaintenanceListReq req) {
        return CommonResult.success(maintenanceService.list(req));
    }

    @ApiOperation("maintenance create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Boolean> create(@RequestBody @Validated MaintenanceCreateReq req) {
        return CommonResult.success(maintenanceService.create(req));
    }

    @ApiOperation("maintenance completed")
    @RequestMapping(value = "/completed", method = RequestMethod.POST)
    public CommonResult<Boolean> completed(@RequestBody @Validated MaintenanceCompletedReq req) {
        return CommonResult.success(maintenanceService.completed(req));
    }

    @ApiOperation("maintenance download")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(HttpServletResponse response, @Validated MaintenanceDownloadReq req) throws Exception {
        String fileName = MaintenanceDownloadEnum.codeOf(req.getType()).getDesc();
        URI uri = this.getClass().getClassLoader().getResource("files/" + fileName).toURI();
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader(
                "Content-disposition",
                "attachment; filename=" + URLUtil.encode(fileName));
        try (
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(Paths.get(uri)));
                BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())
        ) {
            byte[] buff = new byte[1024];
            int len;
            while ((len = bis.read(buff)) > 0) {
                bos.write(buff, 0, len);
            }
        }
    }

}

