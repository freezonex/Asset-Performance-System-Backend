package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author penglifr
 * @since 2024/05/13 17:38
 */
@Data
public class DepartmentListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String departmentName;
}
