package com.freezonex.aps.modules.asset.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.exception.Asserts;
import com.freezonex.aps.modules.asset.convert.AssetConvert;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.mapper.AssetMapper;
import com.freezonex.aps.modules.asset.model.Asset;
import com.freezonex.aps.modules.asset.model.Department;
import com.freezonex.aps.modules.asset.service.AssetService;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import com.freezonex.aps.modules.asset.service.DepartmentService;
import com.freezonex.aps.modules.asset.service.MqttSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * asset 服务实现类
 * </p>
 *
 * @author supos
 * @since 2024-05-06
 */
@Service
@Slf4j
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {

    @Resource
    private AssetConvert assetConvert;

    @Resource
    private AssetTypeService assetTypeService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private MqttSender mqttSender;

    @Value("${asset.upload.dir}")
    private String uploadDir;

    @Value("${asset.download.website:http://47.236.10.165:30090}")
    private String website;

    @Override
    public CommonPage<AssetListDTO> list(AssetListReq req) {
        Page<Asset> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<Asset> query = new LambdaQueryWrapper<>();
        query.eq(StringUtils.isNotBlank(req.getAssetId()), Asset::getAssetId, req.getAssetId());
        query.eq(StringUtils.isNotBlank(req.getAssetName()), Asset::getAssetName, req.getAssetName());
        query.eq(StringUtils.isNotBlank(req.getAssetType()), Asset::getAssetType, req.getAssetType());
        query.eq(StringUtils.isNotBlank(req.getResponsiblePerson()), Asset::getResponsiblePerson, req.getResponsiblePerson());
        query.orderByDesc(Asset::getId);
        Page<Asset> assetPage = this.getBaseMapper().selectPage(page, query);

        // Set the URL for each asset if glbDir is not null or empty
        assetPage.getRecords().parallelStream().forEach(asset -> {
            if (StringUtils.isNotBlank(asset.getGblDir())) {
                asset.setGlbUrl(website + "/apsbackend/asset/download?type=2&id=" + asset.getId());
            }
        });

        return CommonPage.restPage(assetPage, assetConvert::toDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(AssetCreateReq req) {
        AssetTypeListDTO assetTypeDTO = assetTypeService.getByAssetTypeId(req.getAssetTypeId());
        if (assetTypeDTO == null) {
            Asserts.fail("asset type not found");
        }
        if (req.getDepartmentId() != null) {
            Department department = departmentService.getById(req.getDepartmentId());
            if (department == null) {
                Asserts.fail("Department not found");
            }
            req.setDepartment(department.getDepartmentName());
        } else {
            req.setDepartment(null);
        }
        req.setAssetType(assetTypeDTO.getAssetType());
        if (req.getUsedStatus() != null && req.getUsedStatus() == 1) {
            req.setUsedDate(new Date());
        }

        Asset asset = assetConvert.toAsset(req);
        asset.setGmtCreate(new Date());//数据库时区不对 使用系统时间
        asset.setGmtModified(new Date());
        boolean result = this.save(asset);
        if (result) {
            asset = this.getById(asset.getId());
            asset.setGlbUrl(website + "/apsbackend/asset/download?type=2&id=" + asset.getId());
            this.updateById(asset);
            sendMsg(asset);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(AssetUpdateReq req) {
        AssetTypeListDTO assetTypeDTO = assetTypeService.getByAssetTypeId(req.getAssetTypeId());
        if (assetTypeDTO == null) {
            Asserts.fail("asset type not found");
        }
        Asset oldAsset = this.getById(req.getId());
        if (req.getUsedStatus() != null && req.getUsedStatus() == 1 && (oldAsset.getUsedStatus() == null || oldAsset.getUsedStatus() == 0)) {
            //如果未使用变成使用状态 则修改状态 设置使用日期
            req.setUsedStatus(1);
            req.setUsedDate(new Date());
        } else {
            //否则维持之前的数据
            req.setUsedStatus(oldAsset.getUsedStatus());
            req.setUsedDate(oldAsset.getUsedDate());
        }
        if(StringUtils.isBlank(req.getGlbUrl())){
            //保证编辑 的时候也有url
            req.setGlbUrl(website + "/apsbackend/asset/download?type=2&id=" + req.getId());
        }
        Asset asset = assetConvert.toAsset(req);
        asset.setId(req.getId());
        asset.setGmtCreate(oldAsset.getGmtCreate());
        asset.setGmtModified(new Date());
        boolean update = this.updateById(asset);
        if (update) {
            sendMsg(this.getById(req.getId()));
        }
        return update;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(AssetDeleteReq req) {
        return this.removeById(req.getId());
    }

    @Override
    public Map<Long, Long> queryGroupByAssetType(Collection<Long> assetTypeIds, Integer usedStatus) {
        Map<Long, Long> assetTypeQuantityMap = new HashMap<>();
        QueryWrapper<Asset> query = new QueryWrapper<>();
        query.select("asset_type_id assetTypeId", "count(id) quantity");
        if (usedStatus != null) {
            query.eq("used_status", usedStatus);
        }
        query.eq("deleted", 0);
        query.in("asset_type_id", assetTypeIds);
        query.groupBy("asset_type_id");
        List<Map<String, Object>> maps = this.getBaseMapper().selectMaps(query);
        for (Map<String, Object> map : maps) {
            Long assetTypeId = (Long) map.get("assetTypeId");
            Long quantity = (Long) map.get("quantity");
            assetTypeQuantityMap.put(assetTypeId, quantity);
        }
        return assetTypeQuantityMap;
    }

    @Override
    public List<AssetListDTO> queryByAssetTypeId(Collection<Long> assetTypeId) {
        LambdaQueryWrapper<Asset> query = new LambdaQueryWrapper<>();
        query.in(Asset::getAssetTypeId, assetTypeId);
        List<Asset> list = this.list(query);
        return assetConvert.toDTOList(list);
    }

    @Override
    public AssetListDTO getAssetById(Long id) {
        return assetConvert.toDTO(this.getById(id));
    }

    @Override
    public AssetAttachmentUploadDTO attachmentUpload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            Asserts.fail("file is empty");
        }
        File filePath = new File(uploadDir);
        filePath.mkdirs();
        String uuid = UUID.randomUUID().toString(true);
        String fileExtension = org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (fileExtension != null) {
            uuid = uuid + "." + fileExtension;
        }
        File fileObj = new File(uploadDir + uuid);
        file.transferTo(fileObj);
        AssetAttachmentUploadDTO assetAttachmentUploadDTO = new AssetAttachmentUploadDTO();
        assetAttachmentUploadDTO.setAttachmentName(file.getOriginalFilename());
        assetAttachmentUploadDTO.setAttachmentDir(fileObj.getAbsolutePath());
        return assetAttachmentUploadDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean usedStatusUpdate(AssetUsedStatusReq req) {
        Asset asset = this.getById(req.getId());
        if (asset == null) {
            Asserts.fail("asset not found");
        }
        if (asset.getUsedStatus() != null && asset.getUsedStatus() == 1) {
            //已经是使用状态 无需修改
            return true;
        } else {
            asset.setUsedStatus(1);
            asset.setUsedDate(new Date());
        }
        LambdaUpdateWrapper<Asset> updateWrapper = new UpdateWrapper<Asset>().lambda();
        updateWrapper.eq(Asset::getId, req.getId());
        updateWrapper.eq(Asset::getUsedStatus, 0);
        return this.update(asset, updateWrapper);
    }

    @Override
    public Long getUsedCount() {
        LambdaQueryWrapper<Asset> query = new LambdaQueryWrapper<>();
        query.eq(Asset::getUsedStatus, 1);
        return this.count(query);
    }

    public void sendMsg(Asset asset) {
        for (int i = 0; i < 5; i++) {
            //发5次请求
            try {
                if(asset.getGmtCreate()==null){
                    asset.setGmtCreate(new Date());
                }
                if(asset.getGmtModified()==null){
                    asset.setGmtModified(new Date());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("assetName", asset.getAssetName());
                jsonObject.put("assetDescription", asset.getDescription());
                jsonObject.put("Status", asset.getStatus());
                jsonObject.put("Responsible Person", asset.getResponsiblePerson());
                jsonObject.put("Asset Type", asset.getAssetType());
                jsonObject.put("SN", asset.getSn());
                jsonObject.put("Department", asset.getDepartment());
                jsonObject.put("Location", asset.getLocation());
                jsonObject.put("Value", asset.getValue());
                jsonObject.put("iframeAddress", asset.getModelUrl());
                mqttSender.sendMessage("SIB/Singapore/Office/"+asset.getAssetName(), jsonObject.toJSONString());
            } catch (MqttException e) {
                log.error("send mqtt error", e);
            }
        }

    }

}
