package com.grazy.modules.user;

import cn.hutool.core.lang.Assert;
import com.grazy.GCloudServerLauncher;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.JwtUtil;
import com.grazy.modules.user.constants.UserConstant;
import com.grazy.modules.user.context.*;
import com.grazy.modules.user.service.GCloudUserService;
import com.grazy.modules.user.vo.UserInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

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
    private GCloudUserService userService;


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
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);
    }


    /**
     *  测试重复用户名称注册幂等
     */
    @Test(expected = GCloudBusinessException.class)  //期望接收到这个异常类
    public void testRegisterDuplicateUsername(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        //第一次注册
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);
        //第二次注册
        userService.register(userRegisterContext);
    }


    /**
     * 测试用户登录成功
     */
    @Test
    public void loginSuccess(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        String token = userService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));
    }


    /**
     * 测试用户登录名错误
     */
    @Test(expected = GCloudBusinessException.class)
    public void errorUsername(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setUsername("change_username");
        String token = userService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));
    }


    /**
     * 测试用户密码错误
     */
    @Test(expected = GCloudBusinessException.class)
    public void errorPassword(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setPassword("123456789");
        String token = userService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));
    }


    /**
     * 测试用户成功退出登录
     */
    @Test
    public void exit(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        String token = userService.login(userLoginContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));

        //解析accessToken获取用户Id
        Long userId = (Long)JwtUtil.analyzeToken(token, UserConstant.LOGIN_USER_ID);

        userService.exit(userId);
    }


    /**
     * 测试 忘记密码-校验用户名 成功案例
     */
    @Test
    public void testSuccessCheckUsername(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername("user");
        String question = userService.checkUsername(checkUsernameContext);
        System.out.println(question);
        Assert.isTrue(StringUtils.isNoneBlank(question));
    }


    /**
     * 测试 忘记密码-校验用户名 失败案例
     */
    @Test(expected = GCloudBusinessException.class)
    public void testErrorCheckUsername(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername("user123123");
        String question = userService.checkUsername(checkUsernameContext);
    }

    
    /**
     * 测试 忘记密码-校验密保答案 成功案例
     */
    @Test()
    public void testSuccessCheckAnswer(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername("user");
        checkAnswerContext.setQuestion("question");
        checkAnswerContext.setAnswer("answer");
        String token = userService.checkAnswer(checkAnswerContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));
    }
    
    
    /**
     * 测试 忘记密码-校验密保答案 失败案例
     */
    @Test(expected = GCloudBusinessException.class)
    public void testErrorCheckAnswer(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername("user");
        checkAnswerContext.setQuestion("question");
        checkAnswerContext.setAnswer("answer_change");
        String token = userService.checkAnswer(checkAnswerContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));
    }


    /**
     * 测试 忘记密码-重置密码 成功案例
     */
    @Test()
    public void testSuccessResetPassword(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername("user");
        checkAnswerContext.setQuestion("question");
        checkAnswerContext.setAnswer("answer");
        String token = userService.checkAnswer(checkAnswerContext);

        PasswordResetContext passwordResetContext = new PasswordResetContext();
        passwordResetContext.setPassword("147258369");
        passwordResetContext.setUsername("user");
        passwordResetContext.setToken(token);
        userService.passwordReset(passwordResetContext);

    }

    /**
     * 测试 忘记密码-重置密码 失败案例
     */
    @Test(expected = GCloudBusinessException.class)
    public void testErrorResetPassword(){
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername("user");
        checkAnswerContext.setQuestion("question");
        checkAnswerContext.setAnswer("answer");
        String token = userService.checkAnswer(checkAnswerContext);

        PasswordResetContext passwordResetContext = new PasswordResetContext();
        passwordResetContext.setPassword("147258369");
        passwordResetContext.setUsername("user");
        passwordResetContext.setToken(token + "change");
        userService.passwordReset(passwordResetContext);
    }

    /**
     * 测试获取用户的基本信息
     */
    @Test
    public void info() {
        UserRegisterContext userRegisterContext = this.createUserRegisterContext();
        Long register = userService.register(userRegisterContext);
        Assert.isTrue(register.longValue() > 0L);

        UserInfoVo info = userService.info(register);
        Assert.isTrue(Objects.nonNull(info));

    }
}
