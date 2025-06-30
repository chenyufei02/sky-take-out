package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行公共字段自动填充处理
 * @param
 * @return
 */
@Target(ElementType.METHOD)  // 指定这个注解只能加在方法上
@Retention(RetentionPolicy.RUNTIME)  // 指定注解的存活时间生命周期
public @interface AutoFill {
    // 指定数据库操作类型（OperationType里只有UPDATE INSERT）
    OperationType value(); // value()方法指定操作类型（例如UPDATE或INSERT）
}
