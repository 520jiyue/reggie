package jiyue.xsl.reggie.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jiyue.xsl.reggie.Entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
