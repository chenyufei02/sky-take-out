package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    // 这里异常的名字是从控制台复制的MYSQL唯一键冲突时返回的异常信息。
    public Result exceptionHandler(SQLIntegrityConstraintViolationException e){
        // 如果用户名已存在的话数据库的报错信息e=Duplicate entry "xxx" for key "xxxx"
        String message = e.getMessage();
        if (message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];  // 提取第3个单词 也就是每次报错已存在的用户名
            String msg = username+ MessageConstant.ALREADY_EXISTS;
            return Result.error(msg); // 返回报错的Result
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }

    }

}
