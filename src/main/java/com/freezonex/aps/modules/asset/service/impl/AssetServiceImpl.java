package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.convert.AssetConvert;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.mapper.AssetMapper;
import com.freezonex.aps.modules.asset.model.Asset;
import com.freezonex.aps.modules.asset.service.AssetService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        Asset asset = assetConvert.toAsset(req);
        return this.save(asset);
    }

    @Override
    public Boolean update(AssetUpdateReq req) {
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
    public Map<Long, Long> queryGroupByAssetType(Collection<Long> assetTypeIds) {
        Map<Long, Long> assetTypeQuantityMap = new HashMap<>();
        QueryWrapper<Asset> query = new QueryWrapper<>();
        query.select("asset_type_id assetTypeId", "count(id) quantity");
        query.eq("used_status", 0);
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
        query.in( Asset::getAssetTypeId, assetTypeId);
        List<Asset> list = this.list(query);
        return assetConvert.toDTOList(list);
    }

}
