package com.freezonex.aps.modules.asset.controller;


import cn.hutool.core.util.URLUtil;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.common.exception.Asserts;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.service.AssetService;
import com.freezonex.aps.modules.asset.service.MqttSender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
@RequestMapping("/apsbackend/asset")
@Api(tags = "AssetController")
@Tag(name = "AssetController", description = "asset")
public class AssetController {

    @Resource
    private AssetService assetService;

    @Autowired
    private MqttSender mqttSender;

    /**
     * 发送MQTT消息的控制器方法。
     *
     * @param topic 通过URL路径变量传递，代表消息要发送到的主题。
     * @return 根据消息发送结果返回不同的字符串信息。如果消息发送成功，返回"Message sent!"；如果发送失败，返回错误信息。
     */
    @RequestMapping(value = "/send/{topic}", method = RequestMethod.GET)
    public String sendMqttMessage(@PathVariable String topic) {
        try {
            // 尝试发送消息到指定的主题
            mqttSender.sendMessage(topic, "Hello MQTT "+topic);
            return "Message sent!";
        } catch (Exception e) {
            // 捕获并处理发送消息过程中可能发生的任何异常
            return "Error Sending Message: " + e.getMessage();
        }
    }

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

        String attachmentName;
        InputStream inputStream;
        if (StringUtils.isBlank(asset.getAttachmentDir()) ||!new File(asset.getAttachmentDir()).exists()) {
            //如果文件不存在则使用默认的pdf 文件
            attachmentName =  "asset.pdf";
            inputStream = MaintenanceController.class.getResourceAsStream("/files/" + attachmentName);
        }else{
            attachmentName = asset.getAttachmentName();
            inputStream = Files.newInputStream(Paths.get(asset.getAttachmentDir()));
        }
        if (inputStream ==null) {
            Asserts.fail("asset attachment not found");
        }
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
        response.setHeader(
                "Content-disposition",
                "attachment; filename=" + URLUtil.encode(attachmentName));
        try (
                BufferedInputStream bis = new BufferedInputStream(inputStream);
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

