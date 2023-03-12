package jiyue.xsl.reggie.Common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常捕获
 */

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobaExpceptionHandler {

    //sql exception deal
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionhandler(SQLIntegrityConstraintViolationException exception){
        log.error(exception.getMessage());

        //判断错误类型 是否为 用户名冲突
        if (exception.getMessage().contains("Duplicate entry")){
            String[] split_message = exception.getMessage().split(" ");
            String message = split_message[2] + "已存在";
            return R.error("添加失败" + message);
        }
        return R.error("未知错误");
    }


    //sql exception deal
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionhandler(CustomException exception){
        log.error("CustomException    --------->    "  + exception.getMessage());
        return R.error(exception.getMessage());
    }



}
