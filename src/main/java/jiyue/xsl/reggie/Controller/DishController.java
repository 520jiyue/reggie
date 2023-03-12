package jiyue.xsl.reggie.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jiyue.xsl.reggie.Dto.DishDto;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Common.R;
import jiyue.xsl.reggie.Entity.Category;
import jiyue.xsl.reggie.Entity.Dish;
import jiyue.xsl.reggie.Entity.DishFlavor;
import jiyue.xsl.reggie.Service.CategoryService;
import jiyue.xsl.reggie.Service.DishFlavorService;
import jiyue.xsl.reggie.Service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto){

        log.info("dishDto  Saving ------------->   " + dishDto);
        // dishService.save(dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }


    /**
     * 页面响应数据
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> pagedish(int page,int pageSize, String name){

        //打印log
        log.info("page_dish ------>  " + "page={}, pagesize={}, name={}", page, pageSize, name);

        //构造分页条件
        Page<Dish> page1 = new Page<>(page,pageSize);
        Page<DishDto> page2 = new Page<>(page,pageSize);

        // 构造条件查询 构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加一个过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        // 添加一个排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(page1, lambdaQueryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(page1, page2, "records");

        List<Dish> records = page1.getRecords();

        List<DishDto> listdto = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();

            // 根据id查询 对象
            Category byId = categoryService.getById(categoryId);

            if (byId != null) {
                String category = byId.getName();
                dishDto.setCategoryName(category);
            }
            return dishDto;
        }).collect(Collectors.toList());

        page2.setRecords(listdto);

        return R.success(page2);
    }


    /**
     * 修改数据
     * PathVariable  用于请求url
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishId(@PathVariable Long id) {
        DishDto dishDto = dishService.getDishWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改数据
     * PathVariable  用于请求url
     * @return
     */
    @PutMapping
    public R<String> updatedish(@RequestBody DishDto dishDto) {
        dishService.updateDishWithFlavor(dishDto);
        return R.success("修改成功");
    }


    /**
     * dish 状态修改
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        LambdaQueryWrapper<Dish> query = new LambdaQueryWrapper<>();
        query.in(ids != null, Dish::getId, ids);
        List<Dish> list = dishService.list(query);

        for(Dish dish : list) {
            if (dish != null) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }
        return R.success("售卖状态修改成功");
    }


    /**
     * 删除 包括单个和多个 菜品
     * @param ids
     * @return
     * @throws CustomException
     */
    @DeleteMapping
    public R<String> deletedish(@RequestParam List<Long> ids) throws CustomException {
        log.info("ids   ---------->  {}", ids);
        dishService.removeDishWithFlavor(ids);
        return R.success("删除成功");
    }



//    /**
//     * 根据条件查询 菜品数据 给 新建套餐
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> listWithSetmeal(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }



    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}
