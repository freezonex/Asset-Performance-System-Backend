package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/09 11:33
 */
@Data
public class WorkOrderDeleteReq {
    @NotNull
    private Long id;
}
