package com.freezonex.aps.modules.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.dto.AssetTypeCreateReq;
import com.freezonex.aps.modules.asset.dto.AssetTypeListDTO;
import com.freezonex.aps.modules.asset.dto.AssetTypeListReq;
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

    CommonPage<AssetTypeListDTO> list(AssetTypeListReq req);

    AssetTypeListDTO getByAssetTypeId(Long assetTypeId);

    Boolean create(AssetTypeCreateReq req);
}
