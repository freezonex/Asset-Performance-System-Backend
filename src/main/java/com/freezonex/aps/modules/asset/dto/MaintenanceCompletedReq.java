package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/15 15:15
 */
@Data
public class MaintenanceCompletedReq {
    @NotNull
    private Long id;
}
