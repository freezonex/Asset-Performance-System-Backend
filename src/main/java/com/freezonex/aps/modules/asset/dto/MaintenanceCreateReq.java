package com.freezonex.aps.modules.asset.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author penglifr
 * @since 2024/05/15 11:26
 */
@Data
public class MaintenanceCreateReq {
    @NotNull
    private Long assetTypeId;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(
            value = "date format: yyyy-MM-dd"
    )
    private LocalDate scheduledDate;

    @NotEmpty
    private String content;

}
