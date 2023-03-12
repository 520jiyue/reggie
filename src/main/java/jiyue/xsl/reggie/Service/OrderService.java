package jiyue.xsl.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Entity.Orders;


public interface OrderService extends IService<Orders> {

    /**
     * submit 用户下单
     */
    public void submit(Orders order) throws CustomException;


}
