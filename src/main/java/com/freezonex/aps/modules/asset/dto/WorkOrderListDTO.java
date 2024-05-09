package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author penglifr
 * @since 2024/05/09 10:52
 */
@Data
public class WorkOrderListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

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
