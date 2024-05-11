package com.freezonex.aps.modules.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.model.WorkOrder;
import com.google.common.collect.Table;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

/**
 * <p>
 * work order 服务类
 * </p>
 *
 * @author supos
 * @since 2024-05-09
 */
public interface WorkOrderService extends IService<WorkOrder> {

    CommonPage<WorkOrderListDTO> list(WorkOrderListReq req);

    Boolean create(WorkOrderCreateReq req);

    Boolean update(WorkOrderUpdateReq req);

    Boolean delete(WorkOrderDeleteReq req);

    CommonPage<WorkOrderListDTO> groupList(WorkOrderListReq req);

    Table<String, LocalDate, Long> queryGroupByAssignedTo(Collection<String> assignedToList, Date startDate, Date endDate);

}
