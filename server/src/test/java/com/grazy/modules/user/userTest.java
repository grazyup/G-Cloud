package com.grazy.modules.user;

import cn.hutool.core.lang.Assert;
import com.grazy.GCloudServerLauncher;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.JwtUtil;
import com.grazy.modules.user.constants.UserConstant;
import com.grazy.modules.user.context.UserLoginContext;
import com.grazy.modules.user.context.UserRegisterContext;
import com.grazy.modules.user.service.GCloudUserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-02-02 3:53
 * @Description: 用户模块单元测试
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GCloudServerLauncher.class)
@Transactional
public class userTest {

    @Resource
    private GCloudUserService gCloudUserService;


    /**
     * 创建用户实体信息
     * @return
     */
    private UserRegisterContext createUserRegisterContext(){
        UserRegisterContext userRegisterContext = new UserRegisterContext();
        userRegisterContext.setUsername("user");
        userRegisterContext.setPassword("123123123");
        userRegisterContext.setQuestion("question");
        userRegisterContext.setAnswer("answer");
        return userRegisterContext;
    }


    /**
     * 创建登录实体
     */
    private UserLoginContext createUserLoginContext(){
        UserLoginContext userLoginContext = new UserLoginContext();
        userLoginContext.setUsername("user");
        userLoginContext.setPassword("123123123");
        return userLoginContext;
    }


    /**
     * 测试成功注册用户
     */
    @Test
    public void testUserRegister(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = gCloudUserService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);
    }


    /**
     *  测试重复用户名称注册幂等
     */
    @Test(expected = GCloudBusinessException.class)  //期望接收到这个异常类
    public void testRegisterDuplicateUsername(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        //第一次注册
        Long register = gCloudUserService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);
        //第二次注册
        gCloudUserService.register(userRegisterContext);
    }


    /**
     * 测试用户登录成功
     */
    @Test
    public void loginSuccess(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = gCloudUserService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        String token = gCloudUserService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));
    }


    /**
     * 测试用户登录名错误
     */
    @Test(expected = GCloudBusinessException.class)
    public void errorUsername(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = gCloudUserService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setUsername("change_username");
        String token = gCloudUserService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));
    }


    /**
     * 测试用户密码错误
     */
    @Test(expected = GCloudBusinessException.class)
    public void errorPassword(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = gCloudUserService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setPassword("123456789");
        String token = gCloudUserService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));
    }


    /**
     * 测试用户成功退出登录
     */
    @Test
    public void exit(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = gCloudUserService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        String token = gCloudUserService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));

        //解析accessToken获取用户Id
        Long userId = (Long)JwtUtil.analyzeToken(token, UserConstant.LOGIN_USER_ID);

        gCloudUserService.exit(userId);
    }
}
