package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

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
        query.eq(Objects.nonNull(req.getAssetId()), Asset::getAssetId, req.getAssetId());
        query.eq(Objects.nonNull(req.getAssetName()), Asset::getAssetName, req.getAssetName());
        query.eq(Objects.nonNull(req.getAssetType()), Asset::getAssetType, req.getAssetType());
        query.eq(Objects.nonNull(req.getResponsiblePerson()), Asset::getResponsiblePerson, req.getResponsiblePerson());
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

}
