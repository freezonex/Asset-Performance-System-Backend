package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author penglifr
 * @since 2024/05/15 10:21
 */
@Data
public class DepartmentCreateReq {

    @NotEmpty
    private String departmentName;

}
