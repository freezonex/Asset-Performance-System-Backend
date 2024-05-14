package com.freezonex.aps.modules.asset.service;

import com.freezonex.aps.modules.asset.dto.*;

import java.util.List;

public interface DashboardService {
    TotalAssetDTO totalAsset();

    TotalWorkOrderDTO totalWorkOrder();

    List<WorkOrderListDTO> workOrdersQueue();

    List<EventListDTO> eventList(EventListReq req);
}
