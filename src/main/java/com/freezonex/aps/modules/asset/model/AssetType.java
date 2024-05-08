package com.freezonex.aps.modules.asset.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * asset type
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
@Getter
@Setter
@TableName("asset_type")
@ApiModel(value = "AssetType Object", description = "asset type")
public class AssetType implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String assetType;

    private Integer safetyStockQuantity;

    private String unit;

    private String supplierName;

    private String icon;

    private Date gmtCreate;

    private Date gmtModified;

    @TableLogic
    private Integer deleted;


}
