package com.freezonex.aps.modules.asset.service;

import com.freezonex.aps.modules.asset.dto.TotalAssetDTO;
import com.freezonex.aps.modules.asset.dto.TotalWorkOrderDTO;
import com.freezonex.aps.modules.asset.dto.WorkOrderListDTO;

import java.util.List;

public interface DashboardService {
    TotalAssetDTO totalAsset();

    TotalWorkOrderDTO totalWorkOrder();

    List<WorkOrderListDTO> workOrdersQueue();
}
