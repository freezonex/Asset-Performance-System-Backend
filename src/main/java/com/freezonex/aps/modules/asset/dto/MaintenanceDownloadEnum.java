package com.freezonex.aps.modules.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author penglifr
 * @since 2024/05/15 13:39
 */
@AllArgsConstructor
@Getter
public enum MaintenanceDownloadEnum {
    FILE_1(1, "Historical Maintenance Log.txt"),
    FILE_2(2, "Maintenance Check Interval.txt"),
    FILE_3(3, "Asset VaIue depreciation model.txt");

    private final Integer code;
    private final String desc;

    public static MaintenanceDownloadEnum codeOf(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
