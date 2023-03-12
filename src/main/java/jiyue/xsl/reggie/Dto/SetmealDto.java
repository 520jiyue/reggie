package jiyue.xsl.reggie.Dto;


import jiyue.xsl.reggie.Entity.Setmeal;
import jiyue.xsl.reggie.Entity.SetmealDish;
import lombok.Data;
import java.util.List;


@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
