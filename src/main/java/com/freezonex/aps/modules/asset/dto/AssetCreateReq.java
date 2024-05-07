package com.freezonex.aps.modules.asset.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author penglifr
 * @since 2024/05/06 15:46
 */
@Data
public class AssetCreateReq {

    private String assetId;

    private String assetName;

    private Long assetTypeId;

    private String assetType;

    private String vendorModel;

    private String description;

    private String sn;

    private Integer usedStatus;

    private Integer status;

    private String department;

    private String location;

    private String value;

    private String responsiblePerson;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date installationDate;

    private String maintenanceLog;

    private String spareParts;

    private String documentation;

}
