package com.freezonex.aps.modules.asset.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author penglifr
 * @since 2024/05/06 14:26
 */
@Data
public class AssetListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(
            value = "1：Running 2：Maintaining 3：Halt 4：Scheduled Stop"
    )
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

    @ApiModelProperty(
            value = "3d-url"
    )
    private String modelUrl;

    @ApiModelProperty(
            value = "3d-gbl"
    )
    private String glbUrl;

    private String gblDir;

    private String gblFileName;

}
