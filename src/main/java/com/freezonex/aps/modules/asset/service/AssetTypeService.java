package com.freezonex.aps.modules.asset.service;

import com.freezonex.aps.modules.asset.dto.AssetTypeListDTO;
import com.freezonex.aps.modules.asset.model.AssetType;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
