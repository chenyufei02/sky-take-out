package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     * @return void
     * @author yufei
     * @since 2025/6/29
     */
    void save(EmployeeDTO employeeDTO);
    
    /**
     * 分页查询
     * @param [employeePageQueryDTO]
     * @return com.sky.result.PageResult
     * @author yufei
     * @since 2025/6/30
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用禁用员工账号
     * @param [s, id]
     * @return void
     * @author yufei
     * @since 2025/6/30
     */
    void startOrstop(Integer status, Long id);
}
