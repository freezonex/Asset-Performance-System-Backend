package com.freezonex.aps.modules.asset.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author penglifr
 * @since 2024/05/15 11:24
 */
@Data
public class MaintenanceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long assetTypeId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date scheduledDate;

    private String content;

    private Integer status;

    private Date completedTime;

}
