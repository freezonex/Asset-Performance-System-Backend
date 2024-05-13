package com.freezonex.aps.modules.asset.service;

import com.freezonex.aps.modules.asset.dto.DepartmentListDTO;
import com.freezonex.aps.modules.asset.model.Department;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * department 服务类
 * </p>
 *
 * @author supos
 * @since 2024-05-13
 */
public interface DepartmentService extends IService<Department> {

    List<DepartmentListDTO> allList();
}
