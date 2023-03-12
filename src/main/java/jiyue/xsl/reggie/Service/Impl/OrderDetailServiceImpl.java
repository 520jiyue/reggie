package jiyue.xsl.reggie.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jiyue.xsl.reggie.Entity.OrderDetail;
import jiyue.xsl.reggie.Mapper.OrderDetailMapper;
import jiyue.xsl.reggie.Service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
