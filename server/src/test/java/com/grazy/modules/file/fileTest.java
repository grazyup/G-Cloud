package com.grazy.modules.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.grazy.GCloudServerLauncher;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.file.context.*;
import com.grazy.modules.file.domain.GCloudFile;
import com.grazy.modules.file.domain.GCloudFileChunk;
import com.grazy.modules.file.enums.DelFlagEnum;
import com.grazy.modules.file.enums.MergeFlagEnum;
import com.grazy.modules.file.service.GCloudFileChunkService;
import com.grazy.modules.file.service.GCloudFileService;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.file.vo.FileChunkUploadVO;
import com.grazy.modules.file.vo.FolderTreeNodeVo;
import com.grazy.modules.file.vo.UploadChunksVo;
import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.modules.user.context.UserLoginContext;
import com.grazy.modules.user.context.UserRegisterContext;
import com.grazy.modules.user.service.GCloudUserService;
import com.grazy.modules.user.vo.UserInfoVo;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

    @Resource
    private GCloudFileService gCloudFileService;

    @Resource
    private GCloudFileChunkService gCloudFileChunkService;


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
        List<UserFileVO> fileList = gCloudUserFileService.getFileList(queryFileListContext);

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
        List<UserFileVO> fileList = gCloudUserFileService.getFileList(queryFileListContext);
    }


    /**
     * 测试文件删除 -- 错误的文件Id
     */
    @Test(expected = GCloudBusinessException.class)
    public void testDeleteFileByErrorFileId(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);

        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId + 1);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        gCloudUserFileService.deleteFile(deleteFileContext);
    }

    /**
     * 测试文件删除 -- 错误的用户Id
     */
    @Test(expected = GCloudBusinessException.class)
    public void testDeleteFileByErrorUserId(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);

        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId + 1);
        gCloudUserFileService.deleteFile(deleteFileContext);
    }

    /**
     * 测试文件删除 -- 成功
     */
    @Test
    public void testDeleteFileSuccess(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);
        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("TestCreateFolderName");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);

        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = new ArrayList<>();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        gCloudUserFileService.deleteFile(deleteFileContext);
    }


    /**
     * 测试文件秒传功能 -- 文件存在
     */
    @Test
    public void testSecUploadByFileExit(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);

        //创建文件
        GCloudFile gCloudFile = new GCloudFile();
        gCloudFile.setFileId(IdUtil.get());
        gCloudFile.setFilename("filename");
        gCloudFile.setFileSize("fileSize");
        gCloudFile.setFileSizeDesc("fileSizeDesc");
        gCloudFile.setCreateUser(userId);
        gCloudFile.setFileSuffix("suffix");
        gCloudFile.setFilePreviewContentType("");
        gCloudFile.setIdentifier("Identifier");
        gCloudFile.setRealPath("realPath");
        gCloudFile.setCreateTime(new Date());
        boolean save = gCloudFileService.save(gCloudFile);
        Assert.isTrue(save);

        SecUploadFileContext secUploadFileContext = new SecUploadFileContext();
        secUploadFileContext.setFilename("filename.suffix");
        secUploadFileContext.setIdentifier("Identifier");
        secUploadFileContext.setUserId(userId);
        secUploadFileContext.setParentId(info.getRootFileId());
        boolean b = gCloudUserFileService.secUpload(secUploadFileContext);
        Assert.isTrue(b);
    }


    /**
     * 测试文件秒传功能 -- 文件不存在
     */
    @Test
    public void testSecUploadByFileNoExit(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);

        SecUploadFileContext secUploadFileContext = new SecUploadFileContext();
        secUploadFileContext.setFilename("filename.suffix");
        secUploadFileContext.setIdentifier("Identifier");
        secUploadFileContext.setUserId(userId);
        secUploadFileContext.setParentId(info.getRootFileId());
        boolean b = gCloudUserFileService.secUpload(secUploadFileContext);
        Assert.isFalse(b);
    }


    /**
     * 测试单文件上传 -- 成功
     */
    @Test
    public void testUploadSuccess(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);

        FileUploadContext context = new FileUploadContext();
        MultipartFile file = generateMultipartFile();
        context.setFile(file);
        context.setIdentifier("123123");
        context.setTotalSize(file.getSize());
        context.setParentId(info.getRootFileId());
        context.setUserId(userId);
        context.setFilename(file.getOriginalFilename());
        gCloudUserFileService.upload(context);

        //查询文件是否存在
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setParentId(info.getRootFileId());
        queryFileListContext.setUserId(userId);
        List<UserFileVO> fileList = gCloudUserFileService.getFileList(queryFileListContext);
        Assert.notEmpty(fileList);
        Assert.isTrue(fileList.size() == 1);
    }


    /**
     * 生成模拟的网络文件实体
     *
     * @return
     */
    private static MultipartFile generateMultipartFile() {
        MultipartFile file = null;
        try {
            file = new MockMultipartFile("file", "test.txt",
                    "multipart/form-data", "this is test upload".getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 测试查询用户已上传的分片信息列表 -- 成功
     */
    @Test
    public void testQueryUploadedChunksSuccess(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());

        //生成并保存分片记录
        GCloudFileChunk record = new GCloudFileChunk();
        record.setId(IdUtil.get());
        record.setIdentifier("identifier");
        record.setRealPath("realPath");
        record.setChunkNumber(1);
        record.setExpirationTime(DateUtil.offsetDay(new Date(), 1));
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        boolean save = gCloudFileChunkService.save(record);
        Assert.isTrue(save);

        //查询已上传分片信息列表
        QueryUploadChunkContext queryUploadChunkContext = new QueryUploadChunkContext();
        queryUploadChunkContext.setIdentifier("identifier");
        queryUploadChunkContext.setUserId(userId);
        UploadChunksVo uploadedChunks = gCloudUserFileService.getUploadedChunks(queryUploadChunkContext);
        Assert.notNull(uploadedChunks);
        Assert.notEmpty(uploadedChunks.getUploadedChunks());
    }


    /**
     * 测试分片文件上传成功和合并(多线程上传分片)
     */
    @Test
    public void testUploadChunkAndMergeFile() throws InterruptedException{
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);

        //创建一个线程的计数器（主线程等待）
        // --> 统计10个线程，每个线程中run方法执行完调用 countDown()方法，计数器减 1,当计算器减到0时,主线程可以继续执行
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for(int i = 0; i < 10; i++){
            new ChunkUploader(countDownLatch, i + 1, 10, gCloudUserFileService, userId, info.getRootFileId()).start();
        }
        //线程等待（主线程）
        countDownLatch.await();
    }


    /**
     * 分片上传器
     */
    @AllArgsConstructor
    private static class ChunkUploader extends Thread{

        private CountDownLatch countDownLatch;

        /**
         * 分片下标
         */
        private Integer chunk;

        /**
         * 分片总数
         */
        private Integer chunks;

        private GCloudUserFileService userFileService;

        private Long userId;

        private Long parentId;


        /**
         * 1.分片文件的上传
         * 2.分片的合并
         */
        @Override
        public void run() {
            super.run();
            //获取文件
            MultipartFile file = generateMultipartFile();
            String filename = "test.txt";
            String identifier = "123123123";
            Long totalSize = file.getSize() * chunks;

            FileChunkUploadContext fileChunkUploadContext = new FileChunkUploadContext();
            fileChunkUploadContext.setChunkNumber(chunk);
            fileChunkUploadContext.setFile(file);
            fileChunkUploadContext.setFilename(filename);
            fileChunkUploadContext.setIdentifier(identifier);
            fileChunkUploadContext.setTotalSize(totalSize);
            fileChunkUploadContext.setCurrentChunkSize(file.getSize());
            fileChunkUploadContext.setUserId(userId);
            fileChunkUploadContext.setTotalChunks(chunks);
            //分片上传
            FileChunkUploadVO fileChunkUploadVO = userFileService.chunkUpload(fileChunkUploadContext);

            //判断是否达到合并分片
            if(fileChunkUploadVO.getMergeFlag().equals(MergeFlagEnum.READY.getCode())){
                System.out.println("分片 " + chunk + " 检测到可以合并分片");
                //分片合并
                FileChunkMergeContext context = new FileChunkMergeContext();
                context.setFilename(filename);
                context.setIdentifier(identifier);
                context.setParentId(parentId);
                context.setTotalSize(totalSize);
                context.setUserId(userId);
                userFileService.mergeFile(context);
                //线程计数器-1
                countDownLatch.countDown();
            }else {
                countDownLatch.countDown();
            }
        }
    }


    /**
     * 测试获取文件夹树列表
     */
    @Test
    public void testGetFolderTree(){
        //注册用户
        Long userId = userService.register(createUserRegisterContext());
        UserInfoVo info = userService.info(userId);

        //创建文件夹
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName("folder-1");
        createFolderContext.setUserId(userId);
        createFolderContext.setParentId(info.getRootFileId());
        Long fileId = gCloudUserFileService.createFolder(createFolderContext);


        createFolderContext.setFolderName("folder-2");
        fileId = gCloudUserFileService.createFolder(createFolderContext);

        createFolderContext.setFolderName("folder-2-1");
        createFolderContext.setParentId(fileId);
        gCloudUserFileService.createFolder(createFolderContext);

        createFolderContext.setFolderName("folder-2-2");
        createFolderContext.setParentId(fileId);
        fileId = gCloudUserFileService.createFolder(createFolderContext);

        createFolderContext.setFolderName("folder-2-2-1");
        createFolderContext.setParentId(fileId);
        fileId = gCloudUserFileService.createFolder(createFolderContext);

        QueryFolderTreeContext queryFolderTreeContext = new QueryFolderTreeContext();
        queryFolderTreeContext.setUserId(userId);
        List<FolderTreeNodeVo> folderTree = gCloudUserFileService.getFolderTree(queryFolderTreeContext);
        for(FolderTreeNodeVo el: folderTree){
            el.print();
        }
    }
}
