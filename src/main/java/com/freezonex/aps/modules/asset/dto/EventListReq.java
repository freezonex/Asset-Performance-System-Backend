package com.freezonex.aps.modules.asset.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author penglifr
 * @since 2024/05/14 17:46
 */
@Data
public class EventListReq {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(
            value = "date format: yyyy-MM-dd"
    )
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(
            value = "date format: yyyy-MM-dd"
    )
    private LocalDate endDate;
}
