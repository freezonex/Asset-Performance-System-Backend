package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author penglifr
 * @since 2024/05/09 11:31
 */
@Data
public class WorkOrderCreateReq {

    private String orderId;

    private String orderName;

    private String orderType;

    private String description;

    private String assetId;

    private Integer priority;

    private Date creationTime;

    private Date dueTime;

    private Date completionTime;

    private String status;

    private String assignedTo;

    private String createdBy;

    private String notes;

}
