package com.grazy.common.aspect;

import com.grazy.common.annotation.LoginIgnore;
import com.grazy.common.utils.UserIdUtil;
import com.grazy.constants.CacheConstants;
import com.grazy.core.response.R;
import com.grazy.core.response.ResponseCode;
import com.grazy.core.utils.JwtUtil;
import com.grazy.modules.user.constants.UserConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-02-06 23:11
 * @Description: 统一的登录拦截校验切面逻辑实现类
 */

@Component
@Aspect
@Slf4j
public class CommonLoginAspect {

    @Resource
    private CacheManager cacheManager;


    /**
     * 登录认证的参数名称
     */
    private static final String LOGIN_AUTH_PARAM_NAME = "authorization";


    /**
     * 请求头携带的token的键值对key
     */
    private static final String LOGIN_AUTH_REQUEST_HEADER_NAME = "Authorization";

    /**
     * 切入点表达式
     */
    private static final String POINT_CUT = "execution(* com.grazy.modules.*.controller..*(..))";



    /**
     * 声明切入点
     */
    @Pointcut(value = POINT_CUT)
    public void LoginAuthPointcut() {

    }


    /**
     * 登录权限校验环绕通知
     * 1.判断是否需要校验登录权限信息
     * 2.校验登录信息
     *  a.从参数或请求头中获取token
     *  b.从缓存中读取token，两者检验对比是否一致
     *  c.解析token
     *  d.解析的userId存储到线程上下文，提供给下游使用
     *
     * @param proceedingJoinPoint
     * @throws Throwable
     */
    @Around("LoginAuthPointcut()")
    public Object LoginAuthAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if (checkNeedCheckLoginInfo(proceedingJoinPoint)) {
            //校验登录信息
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String requestURI = request.getRequestURI();
            log.info("成功拦截到请求，URI为{}",requestURI);
            if(!checkAndSaveUserId(request)){
                log.warn("成功拦截到请求，URI为{},检测到当前用户未登录，将跳转到登录页面",requestURI);
                return R.fail(ResponseCode.NEED_LOGIN);
            }
            log.info("成功拦截到请求，URI为{},请求通过",requestURI);
        }
        return proceedingJoinPoint.proceed();
    }


    /**
     * 校验token和保存userId
     * @param request 请求
     * @return true:校验通过; false:校验不通过
     */
    private boolean checkAndSaveUserId(HttpServletRequest request) {
        //从请求头中获取token
        String accessToken = request.getHeader(LOGIN_AUTH_REQUEST_HEADER_NAME);
        if(StringUtils.isBlank(accessToken)){
            //获取参数方式传递的token
            accessToken = request.getParameter(LOGIN_AUTH_PARAM_NAME);
        }
        Object userId = JwtUtil.analyzeToken(accessToken, UserConstant.LOGIN_USER_ID);
        if(Objects.isNull(userId)){
            log.info("请求中携带的登录认证token解析失败(错误token 或者 token过期)");
            return false;
        }
        //读取缓存中存储的token
        Cache cache = cacheManager.getCache(CacheConstants.G_CLOUD_CACHE_NAME);
        String cacheToken = cache.get(UserConstant.USER_LOGIN_PREFIX + userId, String.class);
        if(Objects.isNull(cacheToken)){
            log.info("缓存数据中不存在该用户token（可能原因: 缓存中存储token已过期或者该用户未登录）");
            return false;
        }
        if(Objects.equals(cacheToken,accessToken)){
            //保存userId到ThreadLocal中
            UserIdUtil.set(Long.valueOf(String.valueOf(userId)));
            log.info("token校验成功！");
            return true;
        }
        return false;
    }


    /**
     * 判断是否需要校验登录信息
     *
     * @param proceedingJoinPoint
     * @return true:需要校验; false:不需要校验
     */
    private boolean checkNeedCheckLoginInfo(ProceedingJoinPoint proceedingJoinPoint) {
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //是否存在注解，存在返回true，不存在返回false
        return !method.isAnnotationPresent(LoginIgnore.class);
    }
}
