package jiyue.xsl.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import jiyue.xsl.reggie.Dto.SetmealDto;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    // 保存 套餐数据
    public void setwithDishsave(SetmealDto setmealDto);

    // 查询修改套餐数据
    public SetmealDto setSetmeal(Long id);

    // 修改查询的套餐
    public void setmealupdate(SetmealDto setmealDto);

    // 删除选定 的一个或多个 套餐
    public void deletSetmeal(List<Long> ids) throws CustomException;

}
