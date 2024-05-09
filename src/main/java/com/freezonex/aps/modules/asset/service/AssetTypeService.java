package com.freezonex.aps.modules.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freezonex.aps.common.api.BasePage;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.dto.AssetListReq;
import com.freezonex.aps.modules.asset.dto.AssetTypeListDTO;
import com.freezonex.aps.modules.asset.model.AssetType;

import java.util.List;

/**
 * <p>
 * asset type 服务类
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
public interface AssetTypeService extends IService<AssetType> {

    List<AssetTypeListDTO> allList();

    CommonPage<AssetTypeListDTO> list(BasePage page);

}
