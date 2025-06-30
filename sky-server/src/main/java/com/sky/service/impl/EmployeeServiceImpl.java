package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.LocaleResolver;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private LocaleResolver localeResolver;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对密码进行MD5加密后再进行比对和使用
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return void
     * @author yufei
     * @since 2025/6/29
     */
    public void save(EmployeeDTO employeeDTO) {
        // 因为Mapper层需要的是emploee对象 所以需要把DTO里的信息转到employee对象里来
        Employee employee = new Employee();
        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置账号的状态
        employee.setStatus(StatusConstant.ENABLE);
        // 设置默认密码 并且用工具类加密后存入
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

//        // 设置记录创建时间和更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());  这部分已经在mapper层调用新增方法时用自定义注解实现了公共字段自动填充
//        // 设置当前记录的修改人和创建人ID
//        // 用BaseContext里的静态变量ThreadLocal记录的当前登录的用户的ID
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        // 调用mapper创建并执行新增员工的方法
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询
     * @param [employeePageQueryDTO]
     * @return com.sky.result.PageResult
     * @author yufei
     * @since 2025/6/30
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // select * from employee limit 0,10

        // 采用MYBATIS框架下的PageHelper 动态拼接页码 分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize(),"create_time desc");

        // Page是框架指定的，page对象本身只是一个能装很多实体类的列表。
        // Page里存好返回的一页的所有Employee以后，可以自动用page.getTotal()获取一页的总记录数total
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        /* 由于上面mapper层返回的是装了一些Employee 的page对象，但page对象本身只是一个能装很多实体类的列表，无
           法按接口格式同时返回total和当页的数据集合records   */
        // 故实际应当返回pageresult对象封装两个属性：total和records（返回数据的集合）.
        // 因此此时需要先从page中获取total和records出来 存到pageresult返回
        long total = page.getTotal();
        List<Employee> records = page.getResult();

        return new PageResult(total,records);  //返回pageresult对象
    }

    /**
     * 启用禁用员工账号
     * @param [s, id]
     * @return void
     * @author yufei
     * @since 2025/6/30
     */
    public void startOrstop(Integer status, Long id) {
        // update employee set status = ? where id = ?
        // 但希望用一个更通用的动态SQL的update语句可通用的用于所有修改情况
        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);
        employeeMapper.update(employee);
    }

    /**
     * 根据ID查询员工
     * @param [id]
     * @return com.sky.entity.Employee
     * @author yufei
     * @since 2025/6/30
     */
    public Employee getById(Long id) {
        Employee employee =employeeMapper.getById(id);
        employee.setPassword("****"); //把密码手动设置为****返回（没有真改数据库！只是在Java里设置了数据库里查回的对象的属性）
        return employee;
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        // 想直接复用修改员工状态信息时在employeemapper里已经定义好的update方法。但那个方法要求传入employee对象。因此建新对象进行属性拷贝
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);

//        // 并手动设置修改时间
//        employee.setUpdateTime(LocalDateTime.now());   这部分已经在mapper层调用修改方法时用自定义注解实现了公共字段自动填充
//        employee.setUpdateUser(BaseContext.getCurrentId());

        // 调用mapper里已经定义好的更新方法
        employeeMapper.update(employee);
    }

}
