package com.freezonex.aps.modules.asset.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * work order
 * </p>
 *
 * @author supos
 * @since 2024-05-09
 */
@Getter
@Setter
@TableName(value = "work_order", autoResultMap = true)
@ApiModel(value = "WorkOrder Object", description = "work order")
public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
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

    @ApiModelProperty(
            value = "1：Open 2：In Progress 3：Pending Review 4：Dued 5：Closed"
    )
    private Integer status;

    private String assignedTo;

    private String createdBy;

    private String notes;

    private Date gmtCreate;

    private Date gmtModified;

    @TableLogic
    private Integer deleted;


}
