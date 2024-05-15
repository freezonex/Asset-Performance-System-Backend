package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.exception.Asserts;
import com.freezonex.aps.modules.asset.convert.MaintenanceConvert;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.mapper.MaintenanceMapper;
import com.freezonex.aps.modules.asset.model.Maintenance;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import com.freezonex.aps.modules.asset.service.MaintenanceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * maintenance 服务实现类
 * </p>
 *
 * @author supos
 * @since 2024-05-15
 */
@Service
public class MaintenanceServiceImpl extends ServiceImpl<MaintenanceMapper, Maintenance> implements MaintenanceService {

    @Resource
    private MaintenanceConvert maintenanceConvert;
    @Resource
    private AssetTypeService assetTypeService;

    @Override
    public CommonPage<MaintenanceDTO> list(MaintenanceListReq req) {
        Page<Maintenance> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<Maintenance> query = new LambdaQueryWrapper<>();
        query.eq(Maintenance::getAssetTypeId, req.getAssetTypeId());
        query.in(Maintenance::getStatus, req.getStatus());
        if (req.getStatus().equals(1)) {
            query.orderByAsc(Maintenance::getCompletedTime);
        } else {
            query.orderByAsc(Maintenance::getId);
        }
        Page<Maintenance> assetPage = this.getBaseMapper().selectPage(page, query);
        return CommonPage.restPage(assetPage, maintenanceConvert::toDTO);
    }

    @Override
    public Boolean create(MaintenanceCreateReq req) {
        Maintenance maintenance = new Maintenance();
        maintenance.setAssetTypeId(req.getAssetTypeId());
        maintenance.setScheduledDate(req.getScheduledDate());
        maintenance.setContent(req.getContent());
        maintenance.setStatus(0);
        maintenance.setGmtCreate(new Date());
        return this.save(maintenance);
    }

    @Override
    public List<TopDataDTO> topData() {
        List<AssetTypeListDTO> assetTypeListDTOS = assetTypeService.allList();
        return assetTypeListDTOS.stream()
                .sorted(Comparator.comparing(AssetTypeListDTO::getPriceValue).reversed().thenComparing(AssetTypeListDTO::getId))
                .limit(5)
                .map(dto -> {
                    TopDataDTO topDataDTO = new TopDataDTO();
                    topDataDTO.setAssetTypeId(dto.getId());
                    topDataDTO.setAssetType(dto.getAssetType());
                    topDataDTO.setPriceValue(dto.getPriceValue());
                    return topDataDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Boolean completed(MaintenanceCompletedReq req) {
        Maintenance maintenance = this.getById(req.getId());
        if (maintenance == null) {
            Asserts.fail("maintenance record not found");
        }
        if (maintenance.getStatus() == 1) {
            return true;
        }
        maintenance.setStatus(1);
        maintenance.setCompletedTime(new Date());
        return this.saveOrUpdate(maintenance);
    }
}
