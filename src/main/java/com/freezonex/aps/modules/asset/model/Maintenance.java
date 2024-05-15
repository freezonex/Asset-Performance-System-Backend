package com.freezonex.aps.modules.asset.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * maintenance
 * </p>
 *
 * @author supos
 * @since 2024-05-15
 */
@Getter
@Setter
@ApiModel(value = "Maintenance object", description = "maintenance")
public class Maintenance implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long assetTypeId;

    private Date scheduledDate;

    private String content;

    @ApiModelProperty(
            value = "0：Planned 1：Completed"
    )
    private Integer status;

    private Date completedTime;

    private Date gmtCreate;

    private Date gmtModified;

    @TableLogic
    private Integer deleted;


}
