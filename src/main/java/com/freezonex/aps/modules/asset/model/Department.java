package com.freezonex.aps.modules.asset.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * department
 * </p>
 *
 * @author supos
 * @since 2024-05-13
 */
@Getter
@Setter
@TableName(value = "department", autoResultMap = true)
@ApiModel(value = "Department Object", description = "department")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String departmentName;

    private Date gmtCreate;

    private Date gmtModified;

    @TableLogic
    private Integer deleted;


}
