package com.grazy.modules.file;

import cn.hutool.core.lang.Assert;
import com.grazy.GCloudServerLauncher;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.modules.file.context.CreateFolderContext;
import com.grazy.modules.file.context.QueryFileListContext;
import com.grazy.modules.file.context.UpdateFilenameContext;
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
import java.util.List;

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


    /**
     * 测试重命名 -- 错误的用户ID
     */
    @Test(expected = GCloudBusinessException.class)
    public void testUpdateFilenameErrorUserId(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);

        //创建修改文件名称对象
        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setNewFilename("change_TestCreateFolderName");
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId + 2L);
        gCloudUserFileService.updateFilename(updateFilenameContext);
    }


    /**
     * 测试重命名 -- 错误的文件ID
     */
    @Test(expected = GCloudBusinessException.class)
    public void testUpdateFilenameErrorFileId(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);

        //创建修改文件名称对象
        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setNewFilename("change_TestCreateFolderName");
        updateFilenameContext.setFileId(fileId + 22L);
        updateFilenameContext.setUserId(userId);
        gCloudUserFileService.updateFilename(updateFilenameContext);
    }


    /**
     * 测试重命名 -- 同一目录下重复的新文件名
     */
    @Test(expected = GCloudBusinessException.class)
    public void testUpdateFilenameDoubleFilename(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹01
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName1");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);

        //创建文件夹02
        CreateFolderContext createFolderContext2 = new CreateFolderContext();
        createFolderContext2.setFolderName("TestCreateFolderName2");
        createFolderContext2.setUserId(userId);
        createFolderContext2.setParentId(info.getRootFileId());
        gCloudUserFileService.createFolder(createFolderContext2);

        //创建修改文件名称对象
        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setNewFilename("TestCreateFolderName2");
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId);
        gCloudUserFileService.updateFilename(updateFilenameContext);
    }


    /**
     * 测试重命名 -- 新旧文件名重复
     */
    @Test(expected = GCloudBusinessException.class)
    public void testUpdateFilenameOldAndNewFileId(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);

        //创建修改文件名称对象
        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setNewFilename("TestCreateFolderName");
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId);
        gCloudUserFileService.updateFilename(updateFilenameContext);
    }


    /**
     * 测试重命名 -- 全部通过
     */
    @Test()
    public void testUpdateFilenameSuccess(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);

        //创建修改文件名称对象
        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setNewFilename("TestCreateFolderName_change");
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId);
        gCloudUserFileService.updateFilename(updateFilenameContext);

        //查询根目录下的文件列表
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(userId);
        queryFileListContext.setParentId(info.getRootFileId());
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setFileTypeArray(null);
        List<GCloudUserFileVO> fileList = gCloudUserFileService.getFileList(queryFileListContext);
    }

}
