package jiyue.xsl.reggie.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {


    /**
     * 减少菜品套餐到购物车
     */
    public ShoppingCart sub(ShoppingCart shoppingCart) throws CustomException;

    public void clean();
}
