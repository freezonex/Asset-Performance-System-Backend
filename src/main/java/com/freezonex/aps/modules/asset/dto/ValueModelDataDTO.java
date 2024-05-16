package com.freezonex.aps.modules.asset.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author penglifr
 * @since 2024/05/15 18:19
 */
@Data
public class ValueModelDataDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Integer> dates;

    private List<DetailData>  dataList;

    @Data
    public static class DetailData{

        private Integer date;

        private Integer value;
    }

}
