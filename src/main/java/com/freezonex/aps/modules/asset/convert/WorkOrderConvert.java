package com.freezonex.aps.modules.asset.convert;

import com.freezonex.aps.modules.asset.dto.WorkOrderCreateReq;
import com.freezonex.aps.modules.asset.dto.WorkOrderListDTO;
import com.freezonex.aps.modules.asset.model.WorkOrder;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author penglifr
 * @since 2024/05/09 11:27
 */
@Mapper(componentModel = "spring")
public interface WorkOrderConvert {
    WorkOrderListDTO toDTO(WorkOrder workOrder);
    List<WorkOrderListDTO> toDTOList(List<WorkOrder> workOrderList);

    WorkOrder toWorkOrder(WorkOrderCreateReq req);
}
