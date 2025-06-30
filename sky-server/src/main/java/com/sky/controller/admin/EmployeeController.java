package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@SuppressWarnings("ALL")
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return com.sky.result.Result
     * @author yufei
     * @since 2025/6/29
     */
    @PostMapping
    @ApiOperation("增加员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }


    /**
     * 分页查询
     * @param [employeePageQueryDTO]
     * @return com.sky.result.Result<com.sky.result.PageResult>
     * @author yufei
     * @since 2025/6/30
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    // 返回的是一个装了很多个页结果的result集合（每一页result的data是pageresult）
    // 每一页的pageresult包括当页的所有数据（总记录数total和数据集合records）
    // 没有用@RequestBody 注解的时候，方法的参数自动绑定query里的值
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询，参数为{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 启用禁用员工账号
     * @param [s, id]
     * @return com.sky.result.Result
     * @author yufei
     * @since 2025/6/30
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用员工账号")
    // 要返回data数据的时候才要加上泛型，这里只是提交操作 返回code状态码即可不用返回什么数据
    // status是放在请求路径里的参数 要声明@PathVariable("status")注解 并且括号里对应请求路径里{status}的变量名。
    public Result startOrstop(@PathVariable("status") Integer status, Long id){
        log.info("启用禁用员工账号:{},{}",id,status);
        employeeService.startOrstop(status,id);
        return  Result.success();
    }


    /**
     * 根据ID查询员工信息
     * @param [id]
     * @return com.sky.result.Result<com.sky.entity.Employee>
     * @author yufei
     * @since 2025/6/30
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询员工信息")
    public Result<Employee> getById(@PathVariable("id") Long id){
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }


    /**
     * 修改员工信息
     * @param [employeeDTO]
     * @return com.sky.result.Result
     * @author yufei
     * @since 2025/6/30
     */
    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息：{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }


}
