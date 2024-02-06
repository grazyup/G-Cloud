package com.grazy.modules.user.constants;

/**
 * @Author: grazy
 * @Date: 2024-02-04 8:41
 * @Description: 用户模块常量类
 */

public interface UserConstant {

    /**
     * 登录用户的用户ID的key值
     */
    String LOGIN_USER_ID = "LOGIN_USER_ID";


    /**
     * 用户登录缓存前缀
     */
    String USER_LOGIN_PREFIX = "USER_LOGIN_";


    /**
     * 用户忘记密码-重置密码临时token的key
     */
    String FORGET_USERNAME = "FORGET_USERNAME";


    /**
     * 一天时间的毫秒数
     */
    Long ONE_DAY_LONG = 24L * 60L * 60L * 1000L;


    /**
     * 五分钟的毫秒值
     */
    Long FIVE_MINUTES_LONG = 5L * 60L * 1000L;

}
