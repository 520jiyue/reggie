package jiyue.xsl.reggie.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jiyue.xsl.reggie.Entity.User;
import jiyue.xsl.reggie.Mapper.UserMapper;
import jiyue.xsl.reggie.Service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
