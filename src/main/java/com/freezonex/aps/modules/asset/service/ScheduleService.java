package com.freezonex.aps.modules.asset.service;

import com.freezonex.aps.modules.asset.dto.ScheduleFormDataDTO;
import com.freezonex.aps.modules.asset.dto.ScheduleFormDataReq;
import com.freezonex.aps.modules.asset.dto.ScheduleHeadDataDTO;

public interface ScheduleService {
    ScheduleHeadDataDTO queryHeadData();

    ScheduleFormDataDTO queryFormData(ScheduleFormDataReq req);
}
