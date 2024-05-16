package com.freezonex.aps.modules.asset.service.impl;

import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.Utils.DateUtils;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.service.*;
import com.google.common.collect.Table;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Resource
    private WorkOrderService workOrderService;
    @Resource
    private DepartmentService departmentService;

    @Override
    public ScheduleHeadDataDTO queryHeadData() {
        Long workRecords = workOrderService.count();
        long totalAssets = assetService.count();
        Long usedCount = assetService.getUsedCount();
        ScheduleHeadDataDTO scheduleHeadDataDTO = new ScheduleHeadDataDTO();
        scheduleHeadDataDTO.setWorkRecords(workRecords);
        scheduleHeadDataDTO.setIssuesWorkRecords(new Random().nextInt(workRecords.intValue() / 2));
        scheduleHeadDataDTO.setUsageRate(new BigDecimal(usedCount).multiply(new BigDecimal(100)).divide(new BigDecimal(totalAssets), 2, RoundingMode.HALF_UP).toPlainString() + "%");
        scheduleHeadDataDTO.setTotalAssets(totalAssets);
        return scheduleHeadDataDTO;
    }

    @Override
    public ScheduleFormDataDTO queryFormData(ScheduleFormDataReq req) {
        Date startDate = Date.from(req.getSelectDate().minusDays(4).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(req.getSelectDate().plusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant());

        WorkOrderListReq workOrderListReq = new WorkOrderListReq();
        workOrderListReq.setPageSize(req.getPageSize());
        workOrderListReq.setPageNum(req.getPageNum());
        CommonPage<WorkOrderListDTO> workOrderPage = workOrderService.groupList(workOrderListReq);
        List<Long> assignedToList = workOrderPage.getList().stream().map(WorkOrderListDTO::getAssignedTo).collect(Collectors.toList());

        Table<Long, LocalDate, Long> quantityMap = workOrderService.queryGroupByAssignedTo(assignedToList, startDate, endDate);

        Map<Long, String> departmentMap = departmentService.allList().stream().collect(Collectors.toMap(DepartmentListDTO::getId, DepartmentListDTO::getDepartmentName));

        SimpleDateFormat sdf = new SimpleDateFormat("EEE,MMM d", Locale.ENGLISH);
        //查询日期
        List<Date> dateList = DateUtils.findDates(startDate, endDate);
        //获取表单的头部时间列表
        List<String> dates = new ArrayList<>();
        dateList.forEach(date -> {
            dates.add(sdf.format(date));
        });
        List<ScheduleFormDataDTO.DetailData> detailDataList = new ArrayList<>();

        for (Long assignedTo : assignedToList) {
            ScheduleFormDataDTO.DetailData detailData = new ScheduleFormDataDTO.DetailData();
            detailData.setGroupName(departmentMap.get(assignedTo));
            List<ScheduleFormDataDTO.DateData> dataList = new ArrayList<>();
            for (Date date : dateList) {
                LocalDate localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
                Long quantity = quantityMap.get(assignedTo, localDate);
                if (quantity == null) {
                    quantity = 0L;
                }
                ScheduleFormDataDTO.DateData dateData = new ScheduleFormDataDTO.DateData();
                dateData.setDate(sdf.format(date));
                dateData.setColorType(getColorType(quantity));
                dataList.add(dateData);
            }
            detailData.setDataList(dataList);
            detailDataList.add(detailData);
        }
        CommonPage<ScheduleFormDataDTO.DetailData> detailDataPage = new CommonPage<>();
        detailDataPage.setPageNum(workOrderPage.getPageNum());
        detailDataPage.setPageSize(workOrderPage.getPageSize());
        detailDataPage.setTotalPage(workOrderPage.getTotalPage());
        detailDataPage.setTotal(workOrderPage.getTotal());
        detailDataPage.setList(detailDataList);

        ScheduleFormDataDTO scheduleFormDataDTO = new ScheduleFormDataDTO();
        scheduleFormDataDTO.setDates(dates);
        scheduleFormDataDTO.setPageData(detailDataPage);
        return scheduleFormDataDTO;
    }

    private int getColorType(Long quantity) {
        return quantity >= 3 ? 3 : quantity.intValue();
    }

}
