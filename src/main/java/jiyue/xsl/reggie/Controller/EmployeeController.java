package jiyue.xsl.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jiyue.xsl.reggie.Common.R;
import jiyue.xsl.reggie.Entity.Employee;
import jiyue.xsl.reggie.Service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/login")
    // 因为网页的响应用的是 json 所以加上 @RequestBody   员工登录
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         * 1. 将页面提交密码加密为md5
         *
         */
        //1. 将页面提交密码加密为md5
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2. 根据用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3. 如果没有查询到返回登录失败结果
        if(emp == null){
            return R.error("登录失败，没有这个用户");
        }

        //4. 密码的比对
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }

        //5. 查看员工状态
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6.登录成功将用户数据放入session 返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);

    }


    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        /**
         * 员工退出
         */
        //1. 清理session中保存的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> saveEmployee(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工信息:{}", employee.toString());
        //初始化  密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //Long empid  = (Long) request.getSession().getAttribute("employee");
        //.setCreateUser(empid);
        //employee.setUpdateUser(empid);

        //执行 mtbatisplus 方法 存储数据
        employeeService.save(employee);
        return R.success("用户创建成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> selectePageEmployees(int page, int pageSize, String name) {
        log.info("page={}, pagesize={}, name={}", page, pageSize, name);

        //构造分页条件
        Page page1 = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        // 添加一个过滤条件  StringUtils 来自  import org.apache.commons.lang.StringUtils;
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        // 添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(page1, queryWrapper);

        return R.success(page1);
    }


    /**
     * 根据接收的id 和status 来更新状态
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updataStatus(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        // 添加更新时间
        //employee.setUpdateTime(LocalDateTime.now());

        // 添加更新人id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateUser(empId);

        // 修改 status
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }


    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getId(@PathVariable Long id){
        log.info("根据id查询员工信息...");

        // 调用方法将 id查询
        Employee employee = employeeService.getById(id);

        if (employee != null){ return R.success(employee);}
        else {return R.error("查询失败");}

    }



}
