package com.grazy.modules.file;

import cn.hutool.core.lang.Assert;
import com.grazy.GCloudServerLauncher;
import com.grazy.modules.file.context.QueryFileListContext;
import com.grazy.modules.file.enums.DelFlagEnum;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.file.vo.GCloudUserFileVO;
import com.grazy.modules.user.context.UserLoginContext;
import com.grazy.modules.user.context.UserRegisterContext;
import com.grazy.modules.user.service.GCloudUserService;
import com.grazy.modules.user.vo.UserInfoVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-02-19 7:04
 * @Description: 文件模块单元测试
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GCloudServerLauncher.class)
@Transactional
public class fileTest {

    @Resource
    private GCloudUserService userService;

    @Resource
    private GCloudUserFileService gCloudUserFileService;


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
     * 测试获取用户的文件列表
     */
    @Test
    public void testGetUserFileList(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(userId);
        queryFileListContext.setParentId(info.getRootFileId());
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setFileTypeArray(null);
        List<GCloudUserFileVO> fileList = gCloudUserFileService.getFileList(queryFileListContext);

        Assert.isTrue(CollectionUtils.isEmpty(fileList));
    }
}
