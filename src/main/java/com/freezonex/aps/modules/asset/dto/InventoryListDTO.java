package com.freezonex.aps.modules.asset.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author penglifr
 * @since 2024/05/07 13:39
 */
@Data
public class InventoryListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long assetTypeId;

    private String assetType;

    private Integer quantity;

    private String unit;

    private String usageRate;

    private String supplierName;

    private Integer expectedQuantity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date creationTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expectedDate;

    private Integer ai;
}
