package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.convert.WorkOrderConvert;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.mapper.WorkOrderMapper;
import com.freezonex.aps.modules.asset.model.WorkOrder;
import com.freezonex.aps.modules.asset.service.WorkOrderService;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * <p>
 * work order 服务实现类
 * </p>
 *
 * @author supos
 * @since 2024-05-09
 */
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {

    @Resource
    private WorkOrderConvert workOrderConvert;

    @Override
    public CommonPage<WorkOrderListDTO> list(WorkOrderListReq req) {
        Page<WorkOrder> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<WorkOrder> query = new LambdaQueryWrapper<>();
        query.eq(StringUtils.isNotBlank(req.getOrderId()), WorkOrder::getOrderId, req.getOrderId());
        query.eq(StringUtils.isNotBlank(req.getOrderName()), WorkOrder::getOrderName, req.getOrderName());
        query.eq(StringUtils.isNotBlank(req.getOrderType()), WorkOrder::getOrderType, req.getOrderType());
        query.eq(Objects.nonNull(req.getCreationTime()), WorkOrder::getCreationTime, req.getCreationTime());
        query.orderByDesc(WorkOrder::getId);
        Page<WorkOrder> workOrderPage = this.getBaseMapper().selectPage(page, query);
        return CommonPage.restPage(workOrderPage, workOrderConvert::toDTO);

    }

    @Override
    public Boolean create(WorkOrderCreateReq req) {
        req.setCreatedBy("admin");
        WorkOrder workOrder = workOrderConvert.toWorkOrder(req);
        workOrder.setGmtCreate(new Date());
        return this.save(workOrder);
    }

    @Override
    public Boolean update(WorkOrderUpdateReq req) {
        req.setCreatedBy("admin");
        WorkOrder workOrder = workOrderConvert.toWorkOrder(req);
        LambdaUpdateWrapper<WorkOrder> updateWrapper = new UpdateWrapper<WorkOrder>().lambda();
        updateWrapper.eq(WorkOrder::getId, req.getId());
        return this.update(workOrder, updateWrapper);
    }

    @Override
    public Boolean delete(WorkOrderDeleteReq req) {
        return this.removeById(req.getId());
    }

    public CommonPage<WorkOrderListDTO> groupList(WorkOrderListReq req) {
        Page<WorkOrder> page = new Page<>(req.getPageNum(), req.getPageSize());
        LambdaQueryWrapper<WorkOrder> query = new LambdaQueryWrapper<>();
        query.select(WorkOrder::getAssignedTo);
        query.groupBy(WorkOrder::getAssignedTo);
        Page<WorkOrder> workOrderPage = this.getBaseMapper().selectPage(page, query);
        return CommonPage.restPage(workOrderPage, workOrderConvert::toDTO);
    }

    public Table<Long, LocalDate, Long> queryGroupByAssignedTo(Collection<Long> assignedToList, Date startDate, Date endDate) {
        Table<Long, LocalDate, Long> quantityMap = HashBasedTable.create();
        QueryWrapper<WorkOrder> query = new QueryWrapper<>();
        query.select("creation_time creationTime,assigned_to assignedTo", "count(id) quantity");
        query.eq("deleted", 0);
        query.in("assigned_to", assignedToList);
        query.ge("creation_time", startDate);
        query.le("creation_time", endDate);
        query.groupBy("assigned_to,creation_time");
        List<Map<String, Object>> maps = this.getBaseMapper().selectMaps(query);
        for (Map<String, Object> map : maps) {
            LocalDateTime creationTime = (LocalDateTime) map.get("creationTime");
            Long assignedTo = (Long) map.get("assignedTo");
            Long quantity = (Long) map.get("quantity");
            quantityMap.put(assignedTo, creationTime.atZone(ZoneId.systemDefault()).toLocalDate(), quantity);
        }
        return quantityMap;
    }
}
