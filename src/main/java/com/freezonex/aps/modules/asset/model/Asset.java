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
 * asset
 * </p>
 *
 * @author supos
 * @since 2024-05-06
 */
@Getter
@Setter
@ApiModel(value = "Asset Object", description = "asset")
@TableName(value = "asset", autoResultMap = true)
public class Asset implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String assetId;

    private String assetName;

    private Long assetTypeId;

    private String assetType;

    private String vendorModel;

    private String description;

    private String sn;

    @ApiModelProperty(
            value = "0: Not Used 1: Used"
    )
    private Integer usedStatus;

    private Date usedDate;

    private Integer status;

    private Long departmentId;

    private String department;

    private String location;

    private String value;

    private String responsiblePerson;

    private Date installationDate;

    private String maintenanceLog;

    private String spareParts;

    private String documentation;

    private String attachmentName;

    private String attachmentDir;

    private Date gmtCreate;

    private Date gmtModified;

    @TableLogic
    private Integer deleted;

    private String modelUrl;

    private String glbUrl;

    private String gblDir;

    private String gblFileName;
}
