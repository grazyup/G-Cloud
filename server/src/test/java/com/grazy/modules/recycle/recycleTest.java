package com.grazy.modules.recycle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.grazy.GCloudServerLauncher;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.modules.file.context.CreateFolderContext;
import com.grazy.modules.file.context.DeleteFileContext;
import com.grazy.modules.file.service.GCloudFileService;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.modules.recycle.context.QueryRecycleFileListContext;
import com.grazy.modules.recycle.context.RestoreContext;
import com.grazy.modules.recycle.service.GCloudRecycleService;
import com.grazy.modules.user.context.UserRegisterContext;
import com.grazy.modules.user.service.GCloudUserService;
import com.grazy.modules.user.vo.UserInfoVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-21 14:44
 * @Description: 文件回收站测试模块
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GCloudServerLauncher.class)
@Transactional
public class recycleTest {

    @Resource
    private GCloudUserService userService;

    @Resource
    private GCloudFileService gCloudFileService;

    @Resource
    private GCloudUserFileService gCloudUserFileService;

    @Resource
    private GCloudRecycleService recycleService;

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
     * 测试获取回收站文件列表
     */
    @Test
    public void getRecycleFileListSuccess(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);
        //删除文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        gCloudUserFileService.deleteFile(deleteFileContext);
        //查询回收站文件列表
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        List<UserFileVO> recycle = recycleService.recycle(queryRecycleFileListContext);
        Assert.isTrue(CollectionUtil.isNotEmpty(recycle));
    }


    /**
     * 测试还原文件 -- 成功
     */
    @Test
    public void testRestoreSuccess(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);

        //删除文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        gCloudUserFileService.deleteFile(deleteFileContext);

        //还原文件
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setFileIdList(fileIdList);
        restoreContext.setUserId(userId);
        recycleService.restore(restoreContext);
    }


    /**
     * 测试还原文件 -- 无权限（文件与当前用户不一致）
     */
    @Test(expected = GCloudBusinessException.class)
    public void testRestoreFailByErrorUserId(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);

        //删除文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        gCloudUserFileService.deleteFile(deleteFileContext);

        //还原文件
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setFileIdList(fileIdList);
        restoreContext.setUserId(userId + 1);
        recycleService.restore(restoreContext);
    }


    /**
     * 测试还原文件 -- 同名文件（两者均在还原列表中且同一个父文件夹下）
     */
    @Test(expected = GCloudBusinessException.class)
    public void testRestoreFailByErrorFilename1(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId1 = gCloudUserFileService.createFolder(createFolderContext);

        //删除文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId1);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        gCloudUserFileService.deleteFile(deleteFileContext);

        createFolderContext.setFolderName("TestCreateFolderName");
        Long fileId2 = gCloudUserFileService.createFolder(createFolderContext);
        fileIdList.add(fileId2);
        gCloudUserFileService.deleteFile(deleteFileContext);

        //还原文件
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setFileIdList(fileIdList);
        restoreContext.setUserId(userId);
        recycleService.restore(restoreContext);
    }


    /**
     * 测试还原文件 -- 同名文件（同名且同一父文件夹下,一个在回收站，一个在文件目录）
     */
    @Test(expected = GCloudBusinessException.class)
    public void testRestoreFailByErrorFilename2(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId1 = gCloudUserFileService.createFolder(createFolderContext);

        //删除文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId1);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        gCloudUserFileService.deleteFile(deleteFileContext);

        createFolderContext.setFolderName("TestCreateFolderName");
        gCloudUserFileService.createFolder(createFolderContext);

        //还原文件
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setFileIdList(fileIdList);
        restoreContext.setUserId(userId);
        recycleService.restore(restoreContext);
    }
}
