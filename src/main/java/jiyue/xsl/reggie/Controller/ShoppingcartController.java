package jiyue.xsl.reggie.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jiyue.xsl.reggie.Common.BaseContext;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Common.R;
import jiyue.xsl.reggie.Entity.ShoppingCart;
import jiyue.xsl.reggie.Service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 *http://localhost:8080/shoppingCart/add
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingcartController {


    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 移动端 购物车添加
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据:{}",shoppingCart);

        Long currentId = BaseContext.getShreadid();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        log.info("ShoppingCart  ---->   {}" , shoppingCartService.getOne(queryWrapper));

        //查询当前菜品或者套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if(cartServiceOne != null){
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            //注意这个不能使用自动填充，因为这个实体类只有createTime，没有updateTime
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
            return R.success(cartServiceOne);

    }

    /**
     * 请求 URL: http://localhost:8080/shoppingCart/list
     * 请求方法: GET
     * 查看购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getShreadid());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }


    /**
     * 请求 URL: http://localhost:8080/shoppingCart/sub
     * 请求方法: POST
     * 减少购物车
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) throws CustomException {
        log.info("购物车数据:{}",shoppingCart);
        ShoppingCart cart = shoppingCartService.sub(shoppingCart);
        return R.success(cart);
    }



    /**
     * 请求 URL: http://localhost:8080/shoppingCart/clean
     * 请求方法: DELETE
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean(){

        shoppingCartService.clean();
        return R.success("已清空购物车");
    }
}

