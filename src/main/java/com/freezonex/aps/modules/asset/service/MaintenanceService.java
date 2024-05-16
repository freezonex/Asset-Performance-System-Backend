package com.freezonex.aps.modules.asset.service;

import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.model.Maintenance;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * maintenance 服务类
 * </p>
 *
 * @author supos
 * @since 2024-05-15
 */
public interface MaintenanceService extends IService<Maintenance> {

    CommonPage<MaintenanceDTO> list(MaintenanceListReq req);

    Boolean create(MaintenanceCreateReq req);

    List<TopDataDTO> topData();

    Boolean completed(MaintenanceCompletedReq req);

    ValueModelDataDTO valueModelData(ValueModelDataReq req);

}
