package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.modules.asset.convert.AssetTypeConvert;
import com.freezonex.aps.modules.asset.dto.AssetTypeListDTO;
import com.freezonex.aps.modules.asset.mapper.AssetTypeMapper;
import com.freezonex.aps.modules.asset.model.AssetType;
import com.freezonex.aps.modules.asset.service.AssetTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * asset type 服务实现类
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
@Service
public class AssetTypeServiceImpl extends ServiceImpl<AssetTypeMapper, AssetType> implements AssetTypeService {

    @Resource
    private AssetTypeConvert assetTypeConvert;

    @Override
    public List<AssetTypeListDTO> allList() {
        List<AssetType> list = this.list();
        return assetTypeConvert.toDTOList(list);
    }

}
