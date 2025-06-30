package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);


    /**
     * 插入员工数据
     * @param employee
     * @return void
     * @author yufei
     * @since 2025/6/29
     */
    @Select("insert into employee (name, username, password, phone, sex, id_number, " +
            "create_time, update_time, create_user, update_user,status) values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber}," +
            "#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    @AutoFill(value = OperationType.INSERT)  // 定义自定义的拦截类且说明修饰的方法为插入方法 需要被拦截
    void insert(Employee employee);


    /**
     * 分页查询 动态SQL选择不用select注解
     * @param [employeePageQueryDTO]
     * @return com.github.pagehelper.Page<com.sky.entity.Employee>
     * @author yufei
     * @since 2025/6/30
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);


    /**
     * 根据ID动态修改SQL语句以update更新数据
     * @param [employee]
     * @return void
     * @author yufei
     * @since 2025/6/30
     */
    @AutoFill(value = OperationType.UPDATE)  // 定义自定义的拦截类且说明修饰的方法为插入方法 需要被拦截
    void update(Employee employee);


    /**
     * 根据ID查询员工信息
     * @param [id]
     * @return com.sky.entity.Employee
     * @author yufei
     * @since 2025/6/30
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
