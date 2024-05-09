package com.freezonex.aps.modules.asset.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author penglifr
 * @since 2024/05/09 14:23
 */
@Data
public class InventoryDetailListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer expectedQuantity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expectedDate;

    @ApiModelProperty(
            value = "color type 0:Gray 1:Blue 2:Black"
    )
    private Integer colorType;
}
