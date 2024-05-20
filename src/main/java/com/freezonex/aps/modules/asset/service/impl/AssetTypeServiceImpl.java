package com.freezonex.aps.modules.asset.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.exception.Asserts;
import com.freezonex.aps.modules.asset.convert.AssetTypeConvert;
import com.freezonex.aps.modules.asset.dto.AssetTypeCreateReq;
import com.freezonex.aps.modules.asset.dto.AssetTypeListDTO;
import com.freezonex.aps.modules.asset.dto.AssetTypeListReq;
import com.freezonex.aps.modules.asset.mapper.AssetTypeMapper;
import com.freezonex.aps.modules.asset.model.AssetType;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * asset type 服务实现类
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
@Service
public class AssetTypeServiceImpl extends ServiceImpl<AssetTypeMapper, AssetType> implements AssetTypeService {

    @Resource
    private AssetTypeConvert assetTypeConvert;

    @Override
    public List<AssetTypeListDTO> allList() {
        List<AssetType> list = this.list();
        return assetTypeConvert.toDTOList(list);
    }

    @Override
    public CommonPage<AssetTypeListDTO> list(AssetTypeListReq req) {
        Page<AssetType> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<AssetType> query = new LambdaQueryWrapper<>();
        query.eq(StringUtils.isNotEmpty(req.getAssetType()), AssetType::getAssetType, req.getAssetType());
        query.in(CollectionUtil.isNotEmpty(req.getAssetTypeIds()), AssetType::getId, req.getAssetTypeIds());
        query.orderByAsc(AssetType::getId);
        Page<AssetType> assetPage = this.getBaseMapper().selectPage(page, query);
        return CommonPage.restPage(assetPage, assetTypeConvert::toDTO);
    }

    @Override
    public AssetTypeListDTO getByAssetTypeId(Long assetTypeId) {
        return assetTypeConvert.toDTO(this.getById(assetTypeId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(AssetTypeCreateReq req) {
        String assetTypeName = StringUtils.trim(req.getAssetType());
        LambdaQueryWrapper<AssetType> query = new LambdaQueryWrapper<>();
        query.eq(AssetType::getAssetType, assetTypeName);
        AssetType assetType = this.getOne(query);
        if (assetType != null) {
            Asserts.fail("asset type already exists");
        }
        req.setAssetType(assetTypeName);
        assetType = assetTypeConvert.toAssetType(req);
        assetType.setGmtCreate(new Date());//数据库时区不对 使用系统时间
        return this.save(assetType);
    }

}
