package com.freezonex.aps.modules.asset.controller;


import cn.hutool.core.util.URLUtil;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.common.exception.Asserts;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.service.AssetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
@Api(tags = "AssetController")
@Tag(name = "AssetController", description = "asset")
public class AssetController {

    @Resource
    private AssetService assetService;

    @ApiOperation("asset list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
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

    @ApiOperation("asset used status update")
    @RequestMapping(value = "/usedStatusUpdate", method = RequestMethod.POST)
    public CommonResult<Boolean> usedStatusUpdate(@RequestBody @Validated AssetUsedStatusReq req) {
        return CommonResult.success(assetService.usedStatusUpdate(req));
    }

    @ApiOperation("asset delete")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<Boolean> delete(@RequestBody @Validated AssetDeleteReq req) {
        return CommonResult.success(assetService.delete(req));
    }

    @ApiOperation("asset attachments upload")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public CommonResult<AssetAttachmentUploadDTO> upload(@RequestParam MultipartFile file) throws IOException {
        return CommonResult.success(assetService.attachmentUpload(file));
    }

    @ApiOperation("asset attachments download")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(HttpServletResponse response, @Validated AssetAttachmentDownloadReq req) throws Exception {
        AssetListDTO asset = assetService.getAssetById(req.getId());
        if (asset == null) {
            Asserts.fail("asset not found");
        }
        if (StringUtils.isBlank(asset.getAttachmentDir())) {
            Asserts.fail("asset attachment not found");
        }
        if (!new File(asset.getAttachmentDir()).exists()) {
            Asserts.fail("asset attachment not found");
        }
        response.reset();
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader(
                "Content-disposition",
                "attachment; filename=" + URLUtil.encode(asset.getAttachmentName()));
        try (
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(Paths.get(asset.getAttachmentDir())));
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

