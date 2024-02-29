package com.grazy.modules.file.converter;

import com.grazy.modules.file.context.CreateFolderContext;
import com.grazy.modules.file.context.DeleteFileContext;
import com.grazy.modules.file.context.UpdateFilenameContext;
import com.grazy.modules.file.po.CreateFolderPo;
import com.grazy.modules.file.po.DeleteFilePo;
import com.grazy.modules.file.po.UpdateFilenamePo;
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
}