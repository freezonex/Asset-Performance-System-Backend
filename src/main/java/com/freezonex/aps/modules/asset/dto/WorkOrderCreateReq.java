package com.freezonex.aps.modules.asset.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate creationTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate dueTime;

    private Date completionTime;

    @ApiModelProperty(
            value = "1：Open 2：In Progress 3：Pending Review 4：Dued 5：Closed"
    )
    private Integer status;

    private Long assignedTo;

    private String createdBy;

    private String notes;

}
