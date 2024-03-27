package com.grazy.modules.file.converter;

import com.grazy.modules.file.context.*;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.grazy.modules.file.po.*;
import com.grazy.modules.file.vo.FolderTreeNodeVo;
import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.storage.engine.core.context.StoreChunkFileContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @Author: grazy
 * @Date: 2024-02-19 8:15
 * @Description: 文件参数对象类型转换
 */

@Mapper(componentModel = "spring")
public interface FileConverter {

    /**
     * 控制层创建文件夹类 转换为 业务层创建文件夹类
     *
     * @param createFolderPo 控制层创建文件夹类
     * @return 业务层创建文件夹类
     */
    @Mapping(target = "parentId",expression = "java(com.grazy.core.utils.IdUtil.decrypt(createFolderPo.getParentId()))")
    @Mapping(target = "userId",expression = "java(com.grazy.common.utils.UserIdUtil.get())")
    CreateFolderContext CreateFolderPoToCreateFolderContext(CreateFolderPo createFolderPo);


    /**
     * 控制层文件重命名类 转换为 业务层文件重命名类
     *
     * @param updateFilenamePo 控制层文件重命名类
     * @return 业务层文件重命名类
     */
    @Mapping(target = "fileId",expression = "java(com.grazy.core.utils.IdUtil.decrypt(updateFilenamePo.getFileId()))")
    @Mapping(target = "userId",expression = "java(com.grazy.common.utils.UserIdUtil.get())")
    UpdateFilenameContext UpdateFilenamePoToUpdateFilenameContext(UpdateFilenamePo updateFilenamePo);


    /**
     * 控制层批量删除文件类 转换为 业务层批量删除文件
     *
     * @param deleteFilePo 控制层批量删除文件类
     * @return 业务层批量删除文件
     */
    @Mapping(target = "userId",expression = "java(com.grazy.common.utils.UserIdUtil.get())")
    DeleteFileContext DeleteFilePoToDeleteFileContext(DeleteFilePo deleteFilePo);


    /**
     * 控制层文件秒传类 转换为 业务层文件秒传类
     *
     * @param secUploadFilePo 控制层文件秒传类
     * @return 业务层文件秒传类
     */
    @Mapping(target = "parentId",expression = "java(com.grazy.core.utils.IdUtil.decrypt(secUploadFilePo.getParentId()))")
    @Mapping(target = "userId",expression = "java(com.grazy.common.utils.UserIdUtil.get())")
    SecUploadFileContext SecUploadFilePoToSecUploadFileContext(SecUploadFilePo secUploadFilePo);


    /**
     * 控制层单文件上传类 转换为 业务层单文件上传类
     *
     * @param fileUploadPo 控制层单文件上传类
     * @return 业务层单文件上传类
     */
    @Mapping(target = "parentId", expression = "java(com.grazy.core.utils.IdUtil.decrypt(fileUploadPo.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.grazy.common.utils.UserIdUtil.get())")
    FileUploadContext FileUploadPoToFileUploadContext(FileUploadPo fileUploadPo);


    /**
     * 业务层单文件上传类 转换为 业务层单文件保存类
     * @param context 业务层单文件上传类
     * @return 业务层单文件保存类
     */
    @Mapping(target = "record",ignore = true)
    FileSaveContext FileUpLoadContextToFileSaveContext(FileUploadContext context);


    @Mapping(target = "userId", expression = "java(com.grazy.common.utils.UserIdUtil.get())")
    FileChunkUploadContext FileChunkUploadPoToFileChunkUploadContext(FileChunkUploadPo fileChunkUploadPo);


    FileChunkSaveContext FileChunkUploadContextToFileChunkSaveContext(FileChunkUploadContext context);


    @Mapping(target = "realPath", ignore = true)
    StoreChunkFileContext FileChunkSaveContextToStoreChunkFileContext(FileChunkSaveContext context);


    @Mapping(target = "userId", expression = "java(com.grazy.common.utils.UserIdUtil.get())")
    QueryUploadChunkContext QueryUploadChunkPoToQueryUploadChunkContext(QueryUploadChunkPo queryUploadChunkPo);


    @Mapping(target = "userId", expression = "java(com.grazy.common.utils.UserIdUtil.get())")
    FileChunkMergeContext FileChunkMergePoToFileChunkMergeContext(FileChunkMergePo fileChunkMergePo);


    FileChunkMergeAndSaveContext FileChunkMergeContextToFileChunkMergeAndSaveContext(FileChunkMergeContext context);


    @Mapping(target = "label",source = "filename")
    @Mapping(target = "id",source = "fileId")
    @Mapping(target = "children",expression = "java(com.google.common.collect.Lists.newArrayList())")
    FolderTreeNodeVo GCloudUserFileToFolderTreeNodeVO(GCloudUserFile gCloudUserFile);


    UserFileVO GcloudUserFileToUserFileVO(GCloudUserFile gCloudUserFile);
}