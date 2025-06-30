package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MemberSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面 实现公共字段自动填充处理逻辑
 * @param
 * @return
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    // 指定切入点（对哪些类的哪些方法进行拦截）
    // 拦截mapper包下的 所有接口的 所有方法(参数不限) 且 标注了Autofill的方法
    // 符合拦截条件的方法都会执行下面的autoFillPointCut方法
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()") // 表示：在autoFillPointCut()定义的切入点匹配的方法执行之前先执行下面的 autoFill() 方法。
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException { // joinPoint 提供了当前方法调用的元数据，可通过它获取当前方法的参数、方法签名等信息。
        log.info("开始进行公共字段自动填充");

        // 获取当前被拦截到的方法的数据操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); // 获得方法上的注解对象
        OperationType operationType = autoFill.value(); // 获得操作类型 判断是不是自己定义的OperationType里的INSERT/U种类

        // 获取当前方法的参数实体
        Object[] args = joinPoint.getArgs();          // 获取了方法的所有参数
        if (args == null || args.length==0){return;}  // 参数非空检查
        Object entity = args[0];                      // 默认实体对象每次都为所有参数里的第一个对象

        // 准备当前操作的相关公共数据（updatetime updateuser createtime createuser）
        LocalDateTime now = LocalDateTime.now();
        Long currentID = BaseContext.getCurrentId();

        // 根据数据操作类型为实体对象的公共属性赋值（updatetime updateuser createtime createuser）
        if (operationType==OperationType.INSERT){
            // 插入字段需要为create和update的四个字段都赋值, 参数1为获取的entity的赋值方法的方法名(已在常量类里定义好) 参数2为方法的参数类型
            Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            // 通过反射为对象属性赋值
            setCreateTime.invoke(entity,now);
            setCreateUser.invoke(entity,currentID);
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentID);

        }else if (operationType==OperationType.UPDATE){
            // 更新字段只需要为update字段赋值
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            // 通过反射为对象属性赋值
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentID);
        }
    }
}
