package com.freezonex.aps.modules.asset.dto;

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

    private Integer usedStatus;

    private Integer status;

    private String department;

    private String location;

    private String value;

    private String responsiblePerson;

    private Date installationDate;

    private String maintenanceLog;

    private String spareParts;

    private String documentation;

}
