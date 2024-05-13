package com.freezonex.aps.modules.asset.convert;

import com.freezonex.aps.modules.asset.dto.DepartmentListDTO;
import com.freezonex.aps.modules.asset.model.Department;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentConvert {
    DepartmentListDTO toDTO(Department department);
    List<DepartmentListDTO> toDTOList(List<Department> department);

}
