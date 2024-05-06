package com.freezonex.aps.common.api;

import io.swagger.annotations.ApiModelProperty;

public abstract class BasePage {

    @ApiModelProperty(
            value = "page num default 1",
            required = true
    )
    private long pageNum = 1L;
    @ApiModelProperty(
            value = "page size default 20",
            required = true
    )
    private long pageSize = 20L;

    public BasePage() {
    }

    public long getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
}
