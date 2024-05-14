package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author penglifr
 * @since 2024/05/14 17:03
 */
@Data
public class EventListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventName;

    private String eventTime;

}
