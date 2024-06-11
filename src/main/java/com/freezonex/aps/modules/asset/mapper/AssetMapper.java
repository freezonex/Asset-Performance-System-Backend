package com.freezonex.aps.modules.asset.mapper;

import com.freezonex.aps.modules.asset.model.Asset;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * asset Mapper 接口
 * </p>
 *
 * @author supos
 * @since 2024-05-06
 */
@Mapper
public interface AssetMapper extends BaseMapper<Asset> {

    @Select("select * from asset where asset_type_id = #{assetTypeId}")
    List<Asset> getAssetByAssetTypeId(Long assetTypeId);
}
