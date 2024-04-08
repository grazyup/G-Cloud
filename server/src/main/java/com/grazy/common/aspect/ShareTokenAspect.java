package com.grazy.common.aspect;

import com.grazy.common.utils.ShareIdUtil;
import com.grazy.core.response.R;
import com.grazy.core.response.ResponseCode;
import com.grazy.core.utils.JwtUtil;
import com.grazy.modules.share.constants.ShareConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-03-26 00:02
 * @Description: 统一的分享提取码校验通过后需要shareToken切面逻辑实现类
 */

@Component
@Aspect
@Slf4j
public class ShareTokenAspect {

    /**
     * 校验分享提取码通过后的token认证的参数名称
     */
    private static final String SHARE_CODE_AUTH_PARAM_NAME = "shareToken";


    /**
     * 请求头携带的shareToken的键值对key
     */
    private static final String SHARE_CODE_AUTH_REQUEST_HEADER_NAME = "Share-Token";


    /**
     * 切入点表达式(annotation -- 仅扫描标注该注解的接口)
     */
    private static final String POINT_CUT = "@annotation(com.grazy.common.annotation.NeedShareToken)";



    /**
     * 声明切入点
     */
    @Pointcut(value = POINT_CUT)
    public void ShareAuthPointcut() {

    }


    /**
     * 权限校验环绕通知
     *  1.校验shareToken信息
     *    a.从参数或请求头中获取token
     *    b.解析token
     *    c.解析的ShareId存储到线程上下文，提供给下游使用
     *
     * @param proceedingJoinPoint
     * @throws Throwable
     */
    @Around("ShareAuthPointcut()")
    public Object ShareAuthAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            //校验信息
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String requestURI = request.getRequestURI();
            log.info("成功拦截到请求，URI为{}",requestURI);
            if(!checkAndSaveShareId(request)){
                log.warn("成功拦截到请求，URI为{},检测到用户的分享提取码失效，将跳转至分享提取码校验页面",requestURI);
                return R.fail(ResponseCode.ACCESS_DENIED);
            }
            log.info("成功拦截到请求，URI为{},请求通过",requestURI);
        return proceedingJoinPoint.proceed();
    }


    /**
     * 校验token和保存ShareId
     * @param request 请求
     * @return true:校验通过; false:校验不通过
     */
    private boolean checkAndSaveShareId(HttpServletRequest request) {
        //从请求头中获取token
        String shareToken = request.getHeader(SHARE_CODE_AUTH_REQUEST_HEADER_NAME);
        if(StringUtils.isBlank(shareToken)){
            //获取参数方式传递的token
            shareToken = request.getParameter(SHARE_CODE_AUTH_PARAM_NAME);
        }
        if(StringUtils.isBlank(shareToken)){
            return false;
        }
        Object shareId = JwtUtil.analyzeToken(shareToken, ShareConstant.SHARE_ID);
        if(Objects.isNull(shareId)){
            log.info("请求中携带的认证token解析失败(错误token 或者 token过期)");
            return false;
        }
        ShareIdUtil.set(Long.valueOf(String.valueOf(shareId)));
        log.info("token校验成功！");
        return true;
    }
}
