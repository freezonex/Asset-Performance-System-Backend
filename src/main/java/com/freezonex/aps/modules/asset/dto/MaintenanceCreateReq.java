package com.freezonex.aps.modules.asset.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author penglifr
 * @since 2024/05/15 11:26
 */
@Data
public class MaintenanceCreateReq {
    @NotNull
    private Long assetTypeId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull
    private Date scheduledDate;

    @NotEmpty
    private String content;

}
