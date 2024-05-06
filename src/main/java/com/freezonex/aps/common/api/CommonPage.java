package com.freezonex.aps.common.api;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 分页数据封装类
 */
public class CommonPage<T> {
    private Integer pageNum;
    private Integer pageSize;
    private Integer totalPage;
    private Long total;
    private List<T> list;

    /**
     * 将MyBatis Plus 分页结果转化为通用结果
     */
    public static <T> CommonPage<T> restPage(Page<T> pageResult) {
        CommonPage<T> result = new CommonPage<>();
        result.setPageNum(Convert.toInt(pageResult.getCurrent()));
        result.setPageSize(Convert.toInt(pageResult.getSize()));
        result.setTotal(pageResult.getTotal());
        result.setTotalPage(Convert.toInt(pageResult.getTotal()/pageResult.getSize()+1));
        result.setList(pageResult.getRecords());
        return result;
    }

    public static <T,R> CommonPage<R> restPage(Page<T> pageResult, Function<T, R> convertFunction) {
        CommonPage<R> result = new CommonPage<>();
        result.setPageNum(Convert.toInt(pageResult.getCurrent()));
        result.setPageSize(Convert.toInt(pageResult.getSize()));
        result.setTotal(pageResult.getTotal());
        result.setTotalPage(Convert.toInt(pageResult.getTotal()/pageResult.getSize()+1));
        List<R> list = converts(pageResult.getRecords(),convertFunction);
        result.setList(list);
        return result;
    }

    public static <T, R> List<R> converts(List<T> inputs, Function<T, R> convertFunction) {
        if (inputs == null) {
            return null;
        }
        if (inputs.size() == 0) {
            return new ArrayList<R>();
        }
        List<R> result = new ArrayList<R>();
        for (T input : inputs) {
            R r = convertFunction.apply(input);
            result.add(r);
        }
        return result;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
