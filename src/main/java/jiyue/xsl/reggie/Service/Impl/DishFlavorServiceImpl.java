package jiyue.xsl.reggie.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jiyue.xsl.reggie.Entity.DishFlavor;
import jiyue.xsl.reggie.Mapper.DishFlavorMapper;
import jiyue.xsl.reggie.Service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
