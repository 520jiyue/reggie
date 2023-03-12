package jiyue.xsl.reggie.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jiyue.xsl.reggie.Dto.SetmealDto;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Common.R;
import jiyue.xsl.reggie.Entity.Category;
import jiyue.xsl.reggie.Entity.Setmeal;
import jiyue.xsl.reggie.Service.CategoryService;
import jiyue.xsl.reggie.Service.SetmealDishService;
import jiyue.xsl.reggie.Service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;




/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 保存 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){

        // 自定义 方法 保存
        setmealService.setwithDishsave(setmealDto);

        return R.success("添加套餐成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> pagesetmeals(int page , int pageSize, String name){

        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);

    }


    /**
     * 通过id 回写数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> selUpdataMealDto(@PathVariable Long id){

        SetmealDto setmealDto = setmealService.setSetmeal(id);
        return R.success(setmealDto);
    }


    /**
     * 更新 数据
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){

        setmealService.setmealupdate(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 单个 和 批量删除
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) throws CustomException {

        setmealService.deletSetmeal(ids);
        return R.success("删除成功");
    }


    /**
     * 修改 单个或者多个 套餐 状态信息
     */
    @PostMapping("/status/{status}")
    public R<String> status(@RequestParam List<Long> ids,@PathVariable Integer status)  {
        // 修改状态
        LambdaQueryWrapper<Setmeal> query = new LambdaQueryWrapper<>();
        query.in(ids != null , Setmeal::getId, ids);
        List<Setmeal> list = setmealService.list(query);


        // 遍历 ids  对应的 套餐 id
        for (Setmeal setmeal : list) {
            if (setmeal != null ){
                setmeal.setStatus(status);
                setmealService.updateById(setmeal);
            }
        }

        return R.success("修改套餐状态成功");
    }

    /**
     * front 展示 套餐信息
     */

    @GetMapping("/list")
    public R<List<Setmeal>> listmeal(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> query = new LambdaQueryWrapper<>();
        query.eq(setmeal.getCategoryId() != null , Setmeal::getCategoryId ,setmeal.getCategoryId());
        query.eq(setmeal.getStatus() != null , Setmeal::getStatus, setmeal.getStatus());
        query.orderByDesc(Setmeal::getUpdateUser);

        List<Setmeal> list = setmealService.list(query);


        return R.success(list);
    }



}
