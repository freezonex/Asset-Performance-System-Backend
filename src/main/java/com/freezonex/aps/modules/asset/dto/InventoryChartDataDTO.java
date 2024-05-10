package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author penglifr
 * @since 2024/05/10 13:47
 */
@Data
public class InventoryChartDataDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> dates;

    private List<ChartData> dataList;

    @Data
    public static class ChartData{
        private Integer quantity;

        private Integer expectedQuantity;
        private String date;
    }
}
