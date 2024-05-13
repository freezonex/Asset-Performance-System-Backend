package com.freezonex.aps.modules.asset.service.impl;

import cn.hutool.core.lang.UUID;
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
import com.freezonex.aps.modules.asset.service.AssetService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {

    @Resource
    private AssetConvert assetConvert;

    @Value("${asset.upload.dir}")
    private String uploadDir;

    @Override
    public CommonPage<AssetListDTO> list(AssetListReq req) {
        Page<Asset> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<Asset> query = new LambdaQueryWrapper<>();
        query.eq(StringUtils.isNotBlank(req.getAssetId()), Asset::getAssetId, req.getAssetId());
        query.eq(StringUtils.isNotBlank(req.getAssetName()), Asset::getAssetName, req.getAssetName());
        query.eq(StringUtils.isNotBlank(req.getAssetType()), Asset::getAssetType, req.getAssetType());
        query.eq(StringUtils.isNotBlank(req.getResponsiblePerson()), Asset::getResponsiblePerson, req.getResponsiblePerson());
        Page<Asset> assetPage = this.getBaseMapper().selectPage(page, query);
        return CommonPage.restPage(assetPage, assetConvert::toDTO);
    }

    @Override
    public Boolean create(AssetCreateReq req) {
        if (req.getUsedStatus() != null && req.getUsedStatus() == 1) {
            req.setUsedDate(new Date());
        }
        Asset asset = assetConvert.toAsset(req);
        return this.save(asset);
    }

    @Override
    public Boolean update(AssetUpdateReq req) {
        Asset oldAsset = this.getById(req.getId());
        if (req.getUsedStatus() != null && req.getUsedStatus() == 1 && (oldAsset.getUsedStatus() == null || oldAsset.getUsedStatus() == 0)) {
            //如果未使用变成使用状态 则设置使用日期
            req.setUsedDate(new Date());
        }
        Asset asset = assetConvert.toAsset(req);
        LambdaUpdateWrapper<Asset> updateWrapper = new UpdateWrapper<Asset>().lambda();
        updateWrapper.eq(Asset::getId, req.getId());
        return this.update(asset, updateWrapper);
    }

    @Override
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

}
