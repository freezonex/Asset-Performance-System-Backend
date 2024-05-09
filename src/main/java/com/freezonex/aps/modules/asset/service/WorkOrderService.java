package com.freezonex.aps.modules.asset.service;

import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.model.WorkOrder;
import com.baomidou.mybatisplus.extension.service.IService;

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

}
