package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.common.exception.Asserts;
import com.freezonex.aps.modules.asset.convert.DepartmentConvert;
import com.freezonex.aps.modules.asset.dto.DepartmentCreateReq;
import com.freezonex.aps.modules.asset.dto.DepartmentListDTO;
import com.freezonex.aps.modules.asset.mapper.DepartmentMapper;
import com.freezonex.aps.modules.asset.model.Department;
import com.freezonex.aps.modules.asset.service.DepartmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * department 服务实现类
 * </p>
 *
 * @author supos
 * @since 2024-05-13
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Resource
    private DepartmentConvert departmentConvert;

    @Override
    public List<DepartmentListDTO> allList() {
        List<Department> list = this.list();
        return departmentConvert.toDTOList(list);
    }

    @Override
    public Boolean create(DepartmentCreateReq req) {
        String departmentName = StringUtils.trim(req.getDepartmentName());
        LambdaQueryWrapper<Department> query = new LambdaQueryWrapper<>();
        query.eq(Department::getDepartmentName, departmentName);
        Department department = this.getOne(query);
        if (department != null) {
            Asserts.fail("Department name already exists");
        }
        department = new Department();
        department.setDepartmentName(departmentName);
        department.setGmtCreate(new Date());
        return this.save(department);
    }

}
