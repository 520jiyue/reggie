package jiyue.xsl.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import jiyue.xsl.reggie.Common.CustomException;
import jiyue.xsl.reggie.Entity.Category;

public interface CategoryService extends IService<Category> {

    // 自定义方法
    public void removeCategory(Long ids) throws CustomException;
}
