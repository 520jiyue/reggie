package jiyue.xsl.reggie.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jiyue.xsl.reggie.Common.BaseContext;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Entity.ShoppingCart;
import jiyue.xsl.reggie.Mapper.ShoppingCartMapper;
import jiyue.xsl.reggie.Service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {


    /**
     * 减少菜品套餐到购物车
     */
    @Transactional
    public ShoppingCart sub(ShoppingCart shoppingCart) throws CustomException {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        Long dishId = shoppingCart.getDishId();
        //代表数量减少的是菜品数量
        if (dishId != null) {
            //通过dishId查出购物车对象
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            //这里必须要加两个条件，否则会出现用户互相修改对方与自己购物车中相同套餐或者是菜品的数量
            queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getShreadid());
            ShoppingCart cart1 = this.getOne(queryWrapper);
            cart1.setNumber(cart1.getNumber() - 1);
            Integer LatestNumber = cart1.getNumber();
            if (LatestNumber > 0) {
                //对数据进行更新操作
                this.updateById(cart1);
            } else if (LatestNumber == 0) {
                //如果购物车的菜品数量减为0，那么就把菜品从购物车删除
                this.removeById(cart1.getId());
            } else if (LatestNumber < 0) {
                throw new CustomException("操作异常");

            }
            return cart1;

        }

        Long setmealId = shoppingCart.getSetmealId();
        //代表是套餐数量减少
        if (setmealId != null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId).eq(ShoppingCart::getUserId, BaseContext.getShreadid());
            ShoppingCart cart2 = this.getOne(queryWrapper);
            cart2.setNumber(cart2.getNumber() - 1);
            Integer LatestNumber = cart2.getNumber();
            if (LatestNumber > 0) {
                //对数据进行更新操作
                this.updateById(cart2);
            } else if (LatestNumber == 0) {
                //如果购物车的套餐数量减为0，那么就把套餐从购物车删除
                this.removeById(cart2.getId());
            } else if (LatestNumber < 0) {
                throw new CustomException("操作异常");
            }
            return cart2;
        }
        //如果两个if判断都进不去
        throw new CustomException("操作异常");
    }

    @Override
    public void clean() {
        Long user_id = BaseContext.getShreadid();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, user_id);
        this.remove(queryWrapper);
    }

}
