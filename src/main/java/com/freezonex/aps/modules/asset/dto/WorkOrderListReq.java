package com.freezonex.aps.modules.asset.dto;

import com.freezonex.aps.common.api.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @author penglifr
 * @since 2024/05/09 10:54
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WorkOrderListReq extends BasePage {

    private String orderId;

    private String orderName;

    private String orderType;

    private Date creationTime;
}
