package com.freezonex.aps.modules.asset.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author penglifr
 * @since 2024/05/09 11:32
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WorkOrderUpdateReq extends WorkOrderCreateReq{
    @NotNull
    private Long id;
}
