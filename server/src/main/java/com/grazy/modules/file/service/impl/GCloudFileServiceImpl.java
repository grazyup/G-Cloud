package com.grazy.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.common.event.log.ErrorLogEvent;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.FileUtils;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.file.context.FileSaveContext;
import com.grazy.modules.file.domain.GCloudFile;
import com.grazy.modules.file.mapper.GCloudFileMapper;
import com.grazy.modules.file.service.GCloudFileService;
import com.grazy.storage.engine.core.StorageEngine;
import com.grazy.storage.engine.core.context.DeleteStorageFileContext;
import com.grazy.storage.engine.core.context.StoreFileContext;
import org.assertj.core.util.Lists;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

/**
* @author gaofu
* @description 针对表【g_cloud_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2024-01-24 17:41:31
*/
@Service
public class GCloudFileServiceImpl extends ServiceImpl<GCloudFileMapper, GCloudFile> implements GCloudFileService, ApplicationContextAware {

    @Resource
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 上传文件并保存文件的实体记录
     * 1.上传文件
     * 2.保存文件实体记录
     *
     * @param context 文件保存上下文信息
     */
    @Override
    public void saveFile(FileSaveContext context) {
        storeMultipartFile(context);
        GCloudFile gCloudFile = doSaveFileRecord(context.getFilename(), context.getIdentifier(), context.getRealPath(),
                context.getTotalSize(), context.getUserId());
        context.setRecord(gCloudFile);
    }


    /**
     * 单文件上传
     * 该方法委托文件存储引擎实现
     *
     * @param context
     */
    private void storeMultipartFile(FileSaveContext context) {
        try {
            StoreFileContext storeFileContext = new StoreFileContext();
            storeFileContext.setFilename(context.getFilename());
            storeFileContext.setTotalSize(context.getTotalSize());
            storeFileContext.setInputStream(context.getFile().getInputStream());
            storageEngine.store(storeFileContext);
            context.setRealPath(storeFileContext.getRealPath());
        }catch (IOException e){
            e.printStackTrace();
            throw new GCloudBusinessException("文件上传失败！");
        }
    }


    /**
     * 保存文件实体记录
     *
     * @param filename   文件名
     * @param identifier 文件的唯一标识
     * @param realPath   文件路径
     * @param totalSize  文件大小
     * @param userId     用户ID
     * @return 文件实体对象
     */
    private GCloudFile doSaveFileRecord(String filename, String identifier, String realPath, Long totalSize, Long userId) {
        GCloudFile gCloudFile = assembleGCloudFile(filename, identifier, realPath, totalSize, userId);
        if(!save(gCloudFile)){
            //删除已上传的物理文件
            try {
                DeleteStorageFileContext deleteStorageFileContext = new DeleteStorageFileContext();
                deleteStorageFileContext.setFileRealPathList(Lists.newArrayList(realPath));
                storageEngine.delete(deleteStorageFileContext);
            }catch (IOException e){
                e.printStackTrace();
                //发布错误日志事件
                ErrorLogEvent errorLogEvent = new ErrorLogEvent(this,
                        "文件物理删除失败，请手动删除，文件路径为：" + realPath, userId);
                applicationContext.publishEvent(errorLogEvent);
            }
        }
        return gCloudFile;
    }


    /**
     * 组装实体文件信息
     *
     * @param filename   文件名
     * @param identifier 文件的唯一标识
     * @param realPath   文件路径
     * @param totalSize  文件大小
     * @param userId     用户ID
     * @return 文件实体对象
     */
    private GCloudFile assembleGCloudFile(String filename, String identifier, String realPath, Long totalSize, Long userId) {
        GCloudFile gCloudFile = new GCloudFile();
        gCloudFile.setFileId(IdUtil.get());
        gCloudFile.setFileSuffix(FileUtils.getFileSuffix(filename));
        gCloudFile.setFileSize(String.valueOf(totalSize));
        gCloudFile.setRealPath(realPath);
        gCloudFile.setIdentifier(identifier);
        gCloudFile.setCreateUser(userId);
        gCloudFile.setFilename(filename);
        gCloudFile.setFileSizeDesc(FileUtils.byteCountToDisplaySize(totalSize));
        gCloudFile.setCreateTime(new Date());
        return gCloudFile;
    }



}




