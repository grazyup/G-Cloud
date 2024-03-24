package com.grazy.modules.share;

import cn.hutool.core.lang.Assert;
import com.grazy.GCloudServerLauncher;
import com.grazy.modules.file.context.CreateFolderContext;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.share.context.CreateShareUrlContext;
import com.grazy.modules.share.context.QueryShareListContext;
import com.grazy.modules.share.enums.ShareDayTypeEnum;
import com.grazy.modules.share.enums.ShareTypeEnum;
import com.grazy.modules.share.service.GCloudShareService;
import com.grazy.modules.share.vo.GCloudShareUrlListVo;
import com.grazy.modules.share.vo.GCloudShareUrlVo;
import com.grazy.modules.user.context.UserRegisterContext;
import com.grazy.modules.user.service.GCloudUserService;
import com.grazy.modules.user.vo.UserInfoVo;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-03-24 23:16
 * @Description: 文件分享模块测试
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GCloudServerLauncher.class)
@Transactional
public class shareTest {

    @Resource
    private GCloudUserService userService;

    @Resource
    private GCloudUserFileService gCloudUserFileService;

    @Resource
    private GCloudShareService gCloudShareService;

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
     * 测试分享文件
     */
    @Test
    public void testShareFile(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);
        //分享
        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("测试分享");
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setUserId(userId);
        GCloudShareUrlVo gCloudShareUrlVo = gCloudShareService.create(createShareUrlContext);
        Assert.isTrue(Objects.nonNull(gCloudShareUrlVo));
    }


    /**
     * 测试查询分享文件链接列表 -- 成功
     */
    @Test
    public void testGetShareFileUrlList(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);
        //分享
        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("测试分享");
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setUserId(userId);
        GCloudShareUrlVo gCloudShareUrlVo = gCloudShareService.create(createShareUrlContext);
        Assert.isTrue(Objects.nonNull(gCloudShareUrlVo));
        //获取分享列表
        QueryShareListContext queryShareListContext = new QueryShareListContext();
        queryShareListContext.setUserId(userId);
        List<GCloudShareUrlListVo> shares = gCloudShareService.getShares(queryShareListContext);
        Assert.notEmpty(shares);
    }

}
