package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.common.exception.Asserts;
import com.freezonex.aps.modules.asset.convert.MaintenanceConvert;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.mapper.MaintenanceMapper;
import com.freezonex.aps.modules.asset.model.AssetType;
import com.freezonex.aps.modules.asset.model.Maintenance;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import com.freezonex.aps.modules.asset.service.MaintenanceService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.*;
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
        query.like(StringUtils.isNotEmpty(req.getSearchContent()), Maintenance::getContent, req.getSearchContent());
        query.in(Maintenance::getStatus, req.getStatus());
        if (req.getStatus().equals(1)) {
            query.orderByDesc(Maintenance::getCompletedTime);
        } else {
            query.orderByDesc(Maintenance::getId);
        }
        Page<Maintenance> assetPage = this.getBaseMapper().selectPage(page, query);
        return CommonPage.restPage(assetPage, maintenanceConvert::toDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(MaintenanceCreateReq req) {
        Maintenance maintenance = new Maintenance();
        maintenance.setAssetTypeId(req.getAssetTypeId());
        maintenance.setScheduledDate(Date.from(req.getScheduledDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
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
    @Transactional(rollbackFor = Exception.class)
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

    public ValueModelDataDTO valueModelData(ValueModelDataReq req) {
        Long assetTypeId = req.getAssetTypeId();
        AssetType assetType = assetTypeService.getById(assetTypeId);

        //获取gmtCreate 的年份
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(assetType.getGmtCreate());
        Integer year = calendar.get(Calendar.YEAR);
        Integer value = assetType.getPriceValue();
        long seed = assetTypeId + Long.valueOf(value) + req.getModelType();
        int minValue = 1; // 设置最小值
        int maxValue = value; // 设置最大值

        Random rand = new Random(seed);
        //随机一个x轴数量
        int x = rand.nextInt(6) + 5;

        int currentValue = maxValue;
        List<Integer> values1 = new ArrayList<>();//存储逐渐下降的数据
        List<Integer> values2 = new ArrayList<>();//存储随机数
        List<Integer> dates = Lists.newArrayList(year);//第一年
        for (int i = 0; i < x; i++) {
            currentValue = Math.max(minValue, currentValue - (maxValue - minValue) / x); // 逐渐接近最小值
            dates.add(++year);
            values1.add(currentValue);
            values2.add(rand.nextInt((maxValue - minValue) + 1) + minValue);
        }
        values2 = values2.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        ValueModelDataDTO.DetailData initData = new ValueModelDataDTO.DetailData();
        initData.setDate(dates.get(0));
        initData.setValue(assetType.getPriceValue());
        List<ValueModelDataDTO.DetailData> dataList = Lists.newArrayList(initData);//第一年的value
        for (int i = 0; i < values1.size(); i++) {
            ValueModelDataDTO.DetailData detailData = new ValueModelDataDTO.DetailData();
            if (i == values1.size() - 1) {
                detailData.setValue(0); //最后一个数设置0
            } else if (i == (x / 2) || i == (x / 4)) {//用随机数替换部分逐渐下降的数据，
                detailData.setValue(values2.get(i));
            } else {
                detailData.setValue(values1.get(i));
            }
            detailData.setDate(dates.get(i + 1));
            dataList.add(detailData);
        }
        ValueModelDataDTO valueModelDataDTO = new ValueModelDataDTO();
        valueModelDataDTO.setDates(dates);
        valueModelDataDTO.setDataList(dataList);
        return valueModelDataDTO;
    }

}
