package jiyue.xsl.reggie.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Common.R;
import jiyue.xsl.reggie.Entity.Category;
import jiyue.xsl.reggie.Service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */

@Slf4j
@RequestMapping("/category")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新添加 分类
     */
    @PostMapping
    public R<String> saveCategory(@RequestBody Category category){
        //通过 mybatis plus 提交添加
        categoryService.save(category);
        log.info("Category saved {}.......",category);
        return R.success("添加成功");
    }

    /**
     * 分页展示
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> getselectedCategory(int page, int pageSize){

        //构造分页条件
        Page<Category> page1 = new Page<>(page,pageSize);

        log.info("Category   -------》   page={}, pagesize={}", page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加一个排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(page1, queryWrapper);

        return R.success(page1);
    }

    /**
     * 菜品分类信息修改
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){
        //打印日志
        log.info("修改分类信息    ---------》  {}", category);
        categoryService.updateById(category);
        return R.success("菜品分类信息修改成功");

    }

    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> removeCategory(Long ids) throws CustomException {
        log.info("删除分类 ------》 id: {}",ids);
        // categoryService.removeById(ids);
        categoryService.removeCategory(ids);
        return R.success("删除成功");
    }


    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType , category.getType());

        // 添加一个排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);


        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }



}
