package com.freezonex.aps.modules.asset.dto;

import com.freezonex.aps.common.api.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author penglifr
 * @since 2024/05/10 16:16
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ScheduleFormDataReq extends BasePage {
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(
            value = "date format: yyyy-MM-dd"
    )
    LocalDate selectDate;
}
