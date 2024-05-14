package com.freezonex.aps.modules.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author penglifr
 * @since 2024/05/14 17:11
 */
@AllArgsConstructor
@Getter
public enum EventEnum {
    ASSET(1, "Create a asset"),
    INVENTORY(2, "Add an inventory"),
    WORD_ORDER(3, "Add a work-order"),
    MAINTENANCE(4, "Add a maintenance record");

    private final Integer code;
    private final String desc;

    public static EventEnum codeOf(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
