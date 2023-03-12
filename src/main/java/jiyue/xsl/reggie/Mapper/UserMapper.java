package jiyue.xsl.reggie.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jiyue.xsl.reggie.Entity.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper extends BaseMapper<User> {
}
