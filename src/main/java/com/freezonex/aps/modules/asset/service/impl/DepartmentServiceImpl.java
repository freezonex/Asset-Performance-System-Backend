package com.freezonex.aps.modules.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freezonex.aps.modules.asset.convert.DepartmentConvert;
import com.freezonex.aps.modules.asset.dto.DepartmentListDTO;
import com.freezonex.aps.modules.asset.mapper.DepartmentMapper;
import com.freezonex.aps.modules.asset.model.Department;
import com.freezonex.aps.modules.asset.service.DepartmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

}
