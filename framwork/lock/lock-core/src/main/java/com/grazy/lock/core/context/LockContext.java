package com.grazy.lock.core.context;

import com.grazy.lock.core.annotation.LockAnnotation;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @Author: grazy
 * @Date: 2024-04-09 9:56
 * @Description: 锁实体的上下文信息  主要做切点的实体解析，为整体逻辑所公用
 */

@Data
public class LockContext {

    /**
     * 注解表述的方法所属类的名称
     */
    private String className;

    /**
     * 注解标注的方法名称
     */
    private String methodName;

    /**
     * 切点方法上标注的自定义锁注解
     */
    private LockAnnotation annotation;

    /**
     * 类的对象
     */
    private Class classType;

    /**
     * 当前调用的方法的实体
     */
    private Method method;

    /**
     * 参数列表实体
     */
    private Object[] args;

    /**
     * 参数列表类型
     */
    private Class[] parameterTypes;

    /**
     * 代理对象实体
     */
    private Object target;


    /**
     * 初始化实体对象
     *
     * @param proceedingJoinPoint
     * @return
     */
    public static LockContext init(ProceedingJoinPoint proceedingJoinPoint){
        LockContext lockContext = new LockContext();
        doInit(lockContext,proceedingJoinPoint);
        return lockContext;
    }


    /**
     * 执行初始化动作
     *
     * @param lockContext
     * @param proceedingJoinPoint
     */
    private static void doInit(LockContext lockContext, ProceedingJoinPoint proceedingJoinPoint) {
        Signature signature = proceedingJoinPoint.getSignature();
        Object[] args = proceedingJoinPoint.getArgs();
        Object target = proceedingJoinPoint.getTarget();
        Class classTypes = signature.getDeclaringType();
        String className = signature.getDeclaringTypeName();
        Method method = ((MethodSignature) signature).getMethod();
        String methodName = method.getName();
        LockAnnotation annotation = method.getAnnotation(LockAnnotation.class);
        Class<?>[] parameterTypes = method.getParameterTypes();

        lockContext.setAnnotation(annotation);
        lockContext.setArgs(args);
        lockContext.setClassName(className);
        lockContext.setClassType(classTypes);
        lockContext.setMethodName(methodName);
        lockContext.setParameterTypes(parameterTypes);
        lockContext.setTarget(target);
        lockContext.setMethod(method);
    }
}
