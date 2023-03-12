package jiyue.xsl.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jiyue.xsl.reggie.Common.BaseContext;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Common.R;
import jiyue.xsl.reggie.Dto.OrdersDto;
import jiyue.xsl.reggie.Entity.OrderDetail;
import jiyue.xsl.reggie.Entity.Orders;
import jiyue.xsl.reggie.Entity.ShoppingCart;
import jiyue.xsl.reggie.Service.OrderDetailService;
import jiyue.xsl.reggie.Service.OrderService;
import jiyue.xsl.reggie.Service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 请求 URL: http://localhost:8080/order/submit
     * 请求方法: POST
     * 支付提交
     */
    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders order) throws CustomException {
        log.info("order servic -----》   {}", order);
        orderService.submit(order);
        return R.success("下单 成功");
    }


    //抽离的一个方法，通过订单id查询订单明细，得到一个订单明细的集合
    //这里抽离出来是为了避免在stream中遍历的时候直接使用构造条件来查询导致eq叠加，从而导致后面查询的数据都是null
    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        return orderDetailList;
    }

    /**
     * 请求 URL: http://localhost:8080/order/userPage?page=1&pageSize=5
     * 请求方法: GET
     * 订单查询
     */

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        // 分页构造器对象
        Page<Orders> pageorder = new Page<>(page, pageSize);
        Page<OrdersDto> pagedto = new Page<>(page, pageSize);

        // 构造调节查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getShreadid());
        // 这里是直接把当前用户分页的全部结果查询出来，要添加用户id作为查询条件，否则会出现用户可以查询到其他用户的订单情况
        // 添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageorder, queryWrapper);

        // 通过OrderId查询对应的OrderDetail
        LambdaQueryWrapper<OrderDetail> queryWrapper2 = new LambdaQueryWrapper<>();

        // 对OrderDto进行需要的属性赋值
        List<Orders> records = pageorder.getRecords();
        List<OrdersDto> orderDtoList = records.stream().map((item) ->{
            OrdersDto orderDto = new OrdersDto();

            // 此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            Long orderId = item.getId();//获取订单id
            List<OrderDetail> orderDetailList = this.getOrderDetailListByOrderId(orderId);
            BeanUtils.copyProperties(item,orderDto);
            //对orderDto进行OrderDetails属性的赋值
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());

        //使用dto的分页有点难度.....需要重点掌握
        BeanUtils.copyProperties(pageorder,pagedto,"records");
        pagedto.setRecords(orderDtoList);
        return R.success(pagedto);
    }


    /**
     *请求 URL: http://localhost:8080/order/page?page=1&pageSize=10
     * 请求方法: GET
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime){


        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);


        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        //添加查询条件  动态sql  字符串使用StringUtils.isNotEmpty这个方法来判断
        //这里使用了范围查询的动态SQL，这里是重点！！！
        queryWrapper.like(number!=null,Orders::getNumber,number)
                .gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
                .lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);

        orderService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }


    /**
     * 订单派送，订单完成操作
     * 注：后端操作
     * @param orders
     * @return
     */
    @PutMapping
    public R<Orders> dispatch(@RequestBody Orders orders){
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(orders.getId()!=null,Orders::getId,orders.getId());
        Orders one = orderService.getOne(queryWrapper);

        one.setStatus(orders.getStatus());
        orderService.updateById(one);
        return R.success(one);
    }

}
