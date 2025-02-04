package com.freezonex.aps.modules.asset.service;

import com.freezonex.aps.common.api.CommonPage;
import com.freezonex.aps.modules.asset.dto.*;
import com.freezonex.aps.modules.asset.model.Asset;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * asset 服务类
 * </p>
 *
 * @author supos
 * @since 2024-05-06
 */
public interface AssetService extends IService<Asset> {

    /**
     * query asset list
     */
    CommonPage<AssetListDTO> list(AssetListReq req);

    /**
     * create a new asset
     */
    Boolean create(AssetCreateReq req);

    /**
     * update a asset
     */
    Boolean update(AssetUpdateReq req);

    Boolean delete(AssetDeleteReq req);

    Map<Long,Long> queryGroupByAssetType(Collection<Long> assetTypeIds, Integer usedStatus);

    List<AssetListDTO> queryByAssetTypeId(Collection<Long> assetTypeId);

    AssetListDTO getAssetById(Long id);

    AssetAttachmentUploadDTO attachmentUpload(MultipartFile file) throws IOException;

    Boolean usedStatusUpdate(AssetUsedStatusReq req);

    Long getUsedCount();

    void sendMsg(Asset asset);

    List<Asset> getAssetByAssetTypeId(Long assetTypeId);
}
