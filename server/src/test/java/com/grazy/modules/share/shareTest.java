package com.grazy.modules.share;

import cn.hutool.core.lang.Assert;
import com.grazy.GCloudServerLauncher;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.modules.file.context.CreateFolderContext;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.share.context.*;
import com.grazy.modules.share.enums.ShareDayTypeEnum;
import com.grazy.modules.share.enums.ShareTypeEnum;
import com.grazy.modules.share.service.GCloudShareService;
import com.grazy.modules.share.vo.GCloudShareUrlListVo;
import com.grazy.modules.share.vo.GCloudShareUrlVo;
import com.grazy.modules.share.vo.ShareDetailVo;
import com.grazy.modules.user.context.UserRegisterContext;
import com.grazy.modules.user.service.GCloudUserService;
import com.grazy.modules.user.vo.UserInfoVo;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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


    /**
     * 测试取消文件分享链接 -- 成功
     */
    @Test
    public void testCancelShareFileUrlSuccess(){
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
        //取消分享
        CancelShareContext cancelShareContext = new CancelShareContext();
        cancelShareContext.setUserId(userId);
        cancelShareContext.setShareIdList(Lists.newArrayList(gCloudShareUrlVo.getShareId()));
        gCloudShareService.cancelShare(cancelShareContext);

        //重新查询分享链接列表
        shares = gCloudShareService.getShares(queryShareListContext);
        Assert.isTrue(CollectionUtils.isEmpty(shares));
    }


    /**
     * 检验分享提取码 -- 成功
     */
    @Test
    public void testCheckShareCodeSuccess(){
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
        //校验分享提取码
        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareCode(gCloudShareUrlVo.getShareCode());
        checkShareCodeContext.setShareId(gCloudShareUrlVo.getShareId());
        String token = gCloudShareService.checkShareCode(checkShareCodeContext);
        Assert.notBlank(token);
    }


    /**
     * 检验分享提取码 -- 错误验证码
     */
    @Test(expected = GCloudBusinessException.class)
    public void testCheckShareCodeFailByErrorShareCode(){
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
        //校验分享提取码
        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareCode(gCloudShareUrlVo.getShareCode() + "_change");
        checkShareCodeContext.setShareId(gCloudShareUrlVo.getShareId());
        String token = gCloudShareService.checkShareCode(checkShareCodeContext);
        Assert.notBlank(token);
    }


    /**
     * 检验分享提取码 -- 分享链接被删除
     */
    @Test(expected = GCloudBusinessException.class)
    public void testCheckShareCodeFailByDelShare(){
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
        //取消分享
        CancelShareContext cancelShareContext = new CancelShareContext();
        cancelShareContext.setUserId(userId);
        cancelShareContext.setShareIdList(Lists.newArrayList(gCloudShareUrlVo.getShareId()));
        gCloudShareService.cancelShare(cancelShareContext);
        //校验分享提取码
        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareCode(gCloudShareUrlVo.getShareCode() + "_change");
        checkShareCodeContext.setShareId(gCloudShareUrlVo.getShareId());
        String token = gCloudShareService.checkShareCode(checkShareCodeContext);
        Assert.notBlank(token);
    }


    @Test
    public void testStr(){
        StringBuffer name = new StringBuffer("01234567");
        System.out.println(name.replace(2,name.length() - 2,"*"));  //左闭右开区间
    }


    /**
     * 测试获取分享详情
     */
    @Test
    public void testDetailSuccess(){
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
        //获取分享详情
        QueryShareDetailContext queryShareDetailContext = new QueryShareDetailContext();
        queryShareDetailContext.setShareId(gCloudShareUrlVo.getShareId());
        ShareDetailVo detail = gCloudShareService.detail(queryShareDetailContext);
        Assert.isTrue(Objects.nonNull(detail));

        System.out.println(detail);
    }
}
