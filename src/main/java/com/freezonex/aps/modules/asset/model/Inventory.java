package com.freezonex.aps.modules.asset.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * asset inventory
 * </p>
 *
 * @author supos
 * @since 2024-05-07
 */
@Getter
@Setter
@TableName(value = "inventory", autoResultMap = true)
@ApiModel(value = "Inventory Object", description = "asset inventory")
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long assetTypeId;

    private String assetType;

    private Integer quantity;

    private String unit;

    private Integer usageRate;

    private String supplierName;

    private Integer expectedQuantity;

    private Date creationTime;

    private Date expectedDate;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer ai;

    @TableLogic
    private Integer deleted;


}
