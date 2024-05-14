package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freezonex.aps.modules.asset.convert.WorkOrderConvert;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.model.Asset;
import com.freezonex.aps.modules.asset.model.WorkOrder;
import com.freezonex.aps.modules.asset.service.AssetService;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import com.freezonex.aps.modules.asset.service.DashboardService;
import com.freezonex.aps.modules.asset.service.WorkOrderService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author penglifr
 * @since 2024/05/14 10:23
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private AssetService assetService;
    @Resource
    private AssetTypeService assetTypeService;
    @Resource
    private WorkOrderService workOrderService;
    @Resource
    private WorkOrderConvert workOrderConvert;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public TotalAssetDTO totalAsset() {
        //查询所有 asset type
        List<AssetTypeListDTO> assetTypeAllList = assetTypeService.allList();
        List<TotalAssetDTO.ChartData> dataList = new ArrayList<>();
        List<String> assetTypeList = new ArrayList<>();
        List<Asset> list = assetService.list();
        Map<Long, Long> collect = list.stream().collect(Collectors.groupingBy(Asset::getAssetTypeId, Collectors.counting()));
        for (AssetTypeListDTO assetTypeListDTO : assetTypeAllList) {
            TotalAssetDTO.ChartData chartData = new TotalAssetDTO.ChartData();
            chartData.setAssetTypeId(assetTypeListDTO.getId());
            chartData.setAssetType(assetTypeListDTO.getAssetType());
            chartData.setQuantity(collect.getOrDefault(assetTypeListDTO.getId(), 0L).intValue());
            assetTypeList.add(assetTypeListDTO.getAssetType());
            dataList.add(chartData);
        }
        return TotalAssetDTO.builder().totalQuantity(list.size()).assetTypeList(assetTypeList).dataList(dataList).build();
    }

    public TotalWorkOrderDTO totalWorkOrder() {
        List<String> statusList = new ArrayList<>();
        List<TotalWorkOrderDTO.ChartData> dataList = new ArrayList<>();
        List<WorkOrder> list = workOrderService.list();
        //状态分组
        Map<Integer, Long> collect = list.stream().collect(Collectors.groupingBy(WorkOrder::getStatus,Collectors.counting()));
        for (WorkOrderStatusEnum statusEnum : WorkOrderStatusEnum.values()) {
            TotalWorkOrderDTO.ChartData chartData = new TotalWorkOrderDTO.ChartData();
            chartData.setStatus(statusEnum.getDesc());
            chartData.setQuantity(collect.getOrDefault(statusEnum.getCode(), 0L).intValue());
            statusList.add(statusEnum.getDesc());
            dataList.add(chartData);
        }
        return TotalWorkOrderDTO.builder().totalQuantity(list.size()).statusList(statusList).dataList(dataList).build();
    }

    @Override
    public List<WorkOrderListDTO> workOrdersQueue() {
        LambdaQueryWrapper<WorkOrder> query = new LambdaQueryWrapper<>();
        query.orderByDesc(WorkOrder::getCreationTime);
        query.orderByDesc(WorkOrder::getId);
        query.last("limit 5");
        List<WorkOrder> list = workOrderService.list(query);
        return workOrderConvert.toDTOList(list);
    }

    /**
     * select  e.* from (select 1 as type, gmt_create
     *                 from asset
     *                 union all
     *                 select 2 as type, gmt_create
     *                 from inventory
     *                 union all
     *                 select 3 as type, gmt_create
     *                 from work_order) e order by e.gmt_create desc limit 4
     */
    public List<EventListDTO> eventList() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy | hh:mm a", Locale.ENGLISH);
        List<EventListDTO> list = jdbcTemplate.query("select  e.* from (select 1 as type, gmt_create\n" +
                "                from asset\n" +
                "                union all\n" +
                "                select 2 as type, gmt_create\n" +
                "                from inventory\n" +
                "                union all\n" +
                "                select 3 as type, gmt_create\n" +
                "                from work_order) e order by e.gmt_create desc limit 4", (rs, rowNum) -> {
            EventListDTO eventListDTO = new EventListDTO();
            eventListDTO.setEventName(EventEnum.codeOf(rs.getInt("type")).getDesc());
            eventListDTO.setEventTime(sdf.format(rs.getTimestamp("gmt_create")));
            return eventListDTO;
        });
        return list;
    }
}
