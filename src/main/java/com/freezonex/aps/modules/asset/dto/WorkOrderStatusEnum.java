package com.freezonex.aps.modules.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 1：Open 2：In Progress 3：Pending Review 4：Dued 5：Closed
 */
@AllArgsConstructor
@Getter
public enum WorkOrderStatusEnum {
    OPEN(1, "Open"),
    IN_PROGRESS(2, "In Progress"),
    PENDING_REVIEW(3, "Pending Review"),
    DUED(4, "Dued"),
    CLOSED(5, "Closed");

    private final Integer code;
    private final String desc;
}
