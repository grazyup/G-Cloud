package com.grazy.modules.user;

import cn.hutool.core.lang.Assert;
import com.grazy.GCloudServerLauncher;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.modules.user.context.UserRegisterContext;
import com.grazy.modules.user.service.GCloudUserService;
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
        userRegisterContext.setUserName("user");
        userRegisterContext.setPassword("123123");
        userRegisterContext.setQuestion("question");
        userRegisterContext.setAnswer("answer");
        return userRegisterContext;
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

}
