package com.freezonex.aps.modules.asset.controller;


import com.freezonex.aps.common.api.CommonResult;
import com.freezonex.aps.modules.asset.dto.AssetTypeCreateReq;
import com.freezonex.aps.modules.asset.dto.DepartmentCreateReq;
import com.freezonex.aps.modules.asset.dto.DepartmentListDTO;
import com.freezonex.aps.modules.asset.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * department 前端控制器
 * </p>
 *
 * @author supos
 * @since 2024-05-13
 */
@RestController
@RequestMapping("/apsbackend/department")
@Api(tags = "DepartmentController")
@Tag(name = "DepartmentController",description = "department")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @ApiOperation("department all list")
    @RequestMapping(value = "/allList", method = RequestMethod.POST)
    public CommonResult<List<DepartmentListDTO>> allList() {
        return CommonResult.success(departmentService.allList());
    }

    @ApiOperation("department create")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<Boolean> create(@RequestBody @Validated DepartmentCreateReq req) {
        return CommonResult.success(departmentService.create(req));
    }
}

