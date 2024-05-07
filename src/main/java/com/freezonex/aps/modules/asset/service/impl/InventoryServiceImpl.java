package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.convert.InventoryConvert;
import com.freezonex.aps.modules.asset.dto.InventoryListDTO;
import com.freezonex.aps.modules.asset.dto.InventoryListReq;
import com.freezonex.aps.modules.asset.dto.SafetyLevelAssetTypeListDTO;
import com.freezonex.aps.modules.asset.mapper.InventoryMapper;
import com.freezonex.aps.modules.asset.model.Inventory;
import com.freezonex.aps.modules.asset.service.InventoryService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * asset inventory 服务实现类
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    @Resource
    private InventoryConvert inventoryConvert;

    @Override
    public CommonPage<InventoryListDTO> list(InventoryListReq req) {
        Page<Inventory> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        Page<Inventory> assetPage = this.getBaseMapper().selectPage(page, query);
        return CommonPage.restPage(assetPage, inventoryConvert::toDTO);
    }

    @Override
    public List<SafetyLevelAssetTypeListDTO> safetyLevelAssetTypeList() {
        //TODO
        return Lists.newArrayList();
    }

}
