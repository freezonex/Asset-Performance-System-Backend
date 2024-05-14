package com.freezonex.aps.modules.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author penglifr
 * @since 2024/05/14 10:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalAssetDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer totalQuantity;

    private List<String> assetTypeList;

    private List<ChartData> dataList;

    @Data
    public static class ChartData{

        private Long assetTypeId;

        private String assetType;

        private Integer quantity;

    }

}
