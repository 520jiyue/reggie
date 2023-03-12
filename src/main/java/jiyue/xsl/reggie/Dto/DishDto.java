package jiyue.xsl.reggie.Dto;

import jiyue.xsl.reggie.Entity.Dish;
import jiyue.xsl.reggie.Entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
