package jiyue.xsl.reggie.Common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 一个由mybatis plus 提供的一个策略  用于自动填充公共字段  类似于 触发器
 */

@Slf4j
@Component
public class MyMetaObjecthandler implements MetaObjectHandler {

    /**
     * 在insert 执行
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info(" 公共字段 Insert fill " + metaObject.toString());
        //  加入信息填充
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getShreadid());
        metaObject.setValue("updateUser", BaseContext.getShreadid());

    }

    /**
     * 更新自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info(" 公共字段 Update fill " + metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getShreadid());
    }
}
