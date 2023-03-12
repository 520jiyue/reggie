package jiyue.xsl.reggie.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jiyue.xsl.reggie.Common.BaseContext;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Entity.*;
import jiyue.xsl.reggie.Mapper.OrdersMapper;
import jiyue.xsl.reggie.Service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;


    /**
     * submit 用户下单
     */
    @Override
    @Transactional
    public void submit(Orders orders) throws CustomException {
        // 提取 当前用户id
        Long user_id= BaseContext.getShreadid();

        // 提取 当前用户 在 shopcart 的数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, user_id);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("No shopping cart");
        }

        // 查询 用户数据 和 用户地址
        User user = userService.getById(user_id);
        AddressBook adderssbook = addressBookService.getById(orders.getAddressBookId());
        if (adderssbook == null) {
            throw new CustomException("No AddressBook 404 ");
        }

        // 下单 向订单和明细表 插入 数据
        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(user_id);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(adderssbook.getConsignee());
        orders.setPhone(adderssbook.getPhone());
        orders.setAddress((adderssbook.getProvinceName() == null ? "" : adderssbook.getProvinceName())
                + (adderssbook.getCityName() == null ? "" : adderssbook.getCityName())
                + (adderssbook.getDistrictName() == null ? "" : adderssbook.getDistrictName())
                + (adderssbook.getDetail() == null ? "" : adderssbook.getDetail()));

        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}
