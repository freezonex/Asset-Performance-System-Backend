package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.Utils.DateUtils;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.model.Inventory;
import com.freezonex.aps.modules.asset.service.AssetService;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import com.freezonex.aps.modules.asset.service.InventoryService;
import com.freezonex.aps.modules.asset.service.ScheduleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author penglifr
 * @since 2024/05/10 15:50
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private AssetService assetService;
    @Resource
    private AssetTypeService assetTypeService;
    @Resource
    private InventoryService inventoryService;

    @Override
    public ScheduleHeadDataDTO queryHeadData() {
        //TODO
        ScheduleHeadDataDTO scheduleHeadDataDTO = new ScheduleHeadDataDTO();
        scheduleHeadDataDTO.setWorkRecords(0);
        scheduleHeadDataDTO.setIssuesWorkRecords(0);
        scheduleHeadDataDTO.setUsageRate(0);
        scheduleHeadDataDTO.setTotalAssets(0);
        return scheduleHeadDataDTO;
    }

    @Override
    public ScheduleFormDataDTO queryFormData(ScheduleFormDataReq req) {
        final String pattern = "MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date startDate = Date.from(req.getSelectDate().minusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(req.getSelectDate().plusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant());
        //查询日期
        List<Date> dates = DateUtils.findDates(startDate, endDate);

        AssetTypeListReq assetTypeListReq = new AssetTypeListReq();
        assetTypeListReq.setPageNum(req.getPageNum());
        assetTypeListReq.setPageSize(req.getPageSize());
        CommonPage<AssetTypeListDTO> assetTypePage = assetTypeService.list(assetTypeListReq);

        Map<Long, AssetTypeListDTO> assetTypeMap = assetTypePage.getList().stream().collect(Collectors.toMap(AssetTypeListDTO::getId, v -> v));
        Set<Long> assetTypeIds = assetTypeMap.keySet();

        LambdaQueryWrapper<Inventory> query = new LambdaQueryWrapper<>();
        query.in(Inventory::getAssetTypeId, assetTypeIds);
        query.orderByAsc(Inventory::getAssetTypeId, Inventory::getExpectedDate);
        List<Inventory> inventoryAllList = inventoryService.list(query);
        //查询 asset
        List<AssetListDTO> assetAllList = assetService.queryByAssetTypeId(assetTypeIds);

        Map<Long, List<Inventory>> inventoryListMap = inventoryAllList.stream().collect(Collectors.groupingBy(Inventory::getAssetTypeId));

        Map<Long, List<AssetListDTO>> assetListMap = assetAllList.stream().collect(Collectors.groupingBy(AssetListDTO::getAssetTypeId));

        List<ScheduleFormDataDTO.DetailData> detailDataList = new ArrayList<>();
        assetTypeMap.forEach((assetTypeId, assetTypeListDTO) -> {
            List<Inventory> inventoryList = inventoryListMap.get(assetTypeId);
            List<AssetListDTO> assetList = assetListMap.get(assetTypeId);
            //填充 inventory
            List<Inventory> fillInventoryList = inventoryService.fillInventory(assetTypeId, inventoryList);
            //计算每天的库存
            Map<LocalDate, Integer> dateQuantityMap = inventoryService.calculateDateQuantity(assetList, dates);
            //每日分组
            Map<LocalDate, Inventory> inventoryDateMap = fillInventoryList.stream().collect(Collectors.toMap(v -> LocalDateTime.ofInstant(v.getExpectedDate().toInstant(), ZoneId.systemDefault()).toLocalDate(), v -> v));

            List<ScheduleFormDataDTO.DateData> dataList = new ArrayList<>();
            for (Date date : dates) {
                LocalDate localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
                Integer quantity = dateQuantityMap.getOrDefault(localDate, 0);
                int expectedQuantity = 0;
                if (inventoryDateMap.containsKey(localDate)) {
                    expectedQuantity = inventoryDateMap.get(localDate).getExpectedQuantity();
                }
                ScheduleFormDataDTO.DateData dateData = new ScheduleFormDataDTO.DateData();
                dateData.setDate(sdf.format(date));
                dateData.setColorType(quantity >= expectedQuantity ? 1 : 0);
                dataList.add(dateData);
            }
            ScheduleFormDataDTO.DetailData detailData = new ScheduleFormDataDTO.DetailData();
            detailData.setAssetTypeId(assetTypeId);
            detailData.setAssetType(assetTypeMap.get(assetTypeId).getAssetType());
            detailData.setDataList(dataList);
            detailDataList.add(detailData);
        });

        CommonPage<ScheduleFormDataDTO.DetailData> detailDataPage = new CommonPage<>();
        detailDataPage.setPageNum(assetTypePage.getPageNum());
        detailDataPage.setPageSize(assetTypePage.getPageSize());
        detailDataPage.setTotalPage(assetTypePage.getTotalPage());
        detailDataPage.setTotal(assetTypePage.getTotal());
        detailDataPage.setList(detailDataList);

        ScheduleFormDataDTO scheduleFormDataDTO = new ScheduleFormDataDTO();
        scheduleFormDataDTO.setDates(dates.stream().map(sdf::format).collect(Collectors.toList()));
        scheduleFormDataDTO.setPageData(detailDataPage);
        return scheduleFormDataDTO;
    }
}
