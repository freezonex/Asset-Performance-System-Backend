package com.freezonex.aps.modules.asset.dto;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(
            value = "1：Open 2：In Progress 3：Pending Review 4：Dued 5：Closed"
    )
    private Integer status;

    private String assignedTo;

    private String createdBy;

    private String notes;

}
