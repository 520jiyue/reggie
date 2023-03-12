package jiyue.xsl.reggie.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jiyue.xsl.reggie.Entity.Employee;
import jiyue.xsl.reggie.Mapper.EmployeeMapper;
import jiyue.xsl.reggie.Service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
