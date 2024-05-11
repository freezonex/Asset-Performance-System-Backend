package com.freezonex.aps.modules.asset.dto;

import com.freezonex.aps.common.api.CommonPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author penglifr
 * @since 2024/05/10 16:05
 */
@Data
public class ScheduleFormDataDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> dates;

    private CommonPage<DetailData> pageData;

    @Data
    public static class DetailData{

        private String groupName;

        private List<ScheduleFormDataDTO.DateData> dataList;
    }

    @Data
    public static class DateData{
        private Integer colorType;
        private String date;
    }
}
