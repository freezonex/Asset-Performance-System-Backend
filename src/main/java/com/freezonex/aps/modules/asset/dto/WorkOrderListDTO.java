package com.freezonex.aps.modules.asset.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date creationTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueTime;

    private Date completionTime;

    @ApiModelProperty(
            value = "1：Open 2：In Progress 3：Pending Review 4：Dued 5：Closed"
    )
    private Integer status;

    private Long assignedTo;

    private String createdBy;

    private String notes;

}
