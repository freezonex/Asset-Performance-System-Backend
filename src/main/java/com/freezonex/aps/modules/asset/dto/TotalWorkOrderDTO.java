package com.freezonex.aps.modules.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author penglifr
 * @since 2024/05/14 10:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalWorkOrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer totalQuantity;

    private List<String> statusList;

    private List<ChartData> dataList;

    @Data
    public static class ChartData {

        private String status;

        private Integer quantity;

    }
}
