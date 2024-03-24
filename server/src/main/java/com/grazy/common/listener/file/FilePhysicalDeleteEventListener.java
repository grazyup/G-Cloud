package com.grazy.common.listener.file;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grazy.common.event.file.FilePhysicalDeleteEvent;
import com.grazy.common.event.log.ErrorLogEvent;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.modules.file.domain.GCloudFile;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.grazy.modules.file.enums.FolderFlagEnum;
import com.grazy.modules.file.service.GCloudFileService;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.storage.engine.core.StorageEngine;
import com.grazy.storage.engine.core.context.DeleteStorageFileContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2024-03-24 15:43
 * @Description: 文件物理删除监听器
 */

@Component
public class FilePhysicalDeleteEventListener implements ApplicationContextAware {

    @Resource
    private GCloudFileService gCloudFileService;

    @Resource
    private GCloudUserFileService gCloudUserFileService;

    @Resource
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    /**
     * 监听物理文件删除事件执行器
     *  该执行器是一个资源释放器，释放被物理删除的文件列表中关联的实体文件记录
     * 1.查询所有无引用的实体文件记录
     * 2.删除记录
     * 3.物理删除文件（委托文件存储引擎）
     *
     * @param event
     */
    @EventListener(classes = FilePhysicalDeleteEvent.class)
    public void physicalDeleteFile(FilePhysicalDeleteEvent event){
        List<GCloudUserFile> allRecords = event.getAllRecords();
        if(CollectionUtils.isEmpty(allRecords)){
            return;
        }
        List<Long> realFileIdList = findAllUnusedRealFileIdList(allRecords);
        if(CollectionUtils.isEmpty(realFileIdList)){
            return;
        }
        List<GCloudFile> realFileRecords = gCloudFileService.listByIds(realFileIdList);
        if(CollectionUtils.isEmpty(realFileRecords)){
            return;
        }
        if (!gCloudFileService.removeByIds(realFileIdList)) {
            applicationContext.publishEvent(new ErrorLogEvent(this,
                    "实体文件记录：" + JSON.toJSONString(realFileIdList) + "， 物理删除失败，请执行手动删除",
                    GCloudConstants.ZERO_LONG));
            return;
        }
        physicalDeleteFileByStorageEngine(realFileRecords);
    }



    /********************************************** private方法 **********************************************/

    /**
     * 物理删除文件（委托文件存储引擎）
     *
     * @param realFileRecords
     */
    private void physicalDeleteFileByStorageEngine(List<GCloudFile> realFileRecords) {
        List<String> realPathList = realFileRecords.stream().map(GCloudFile::getRealPath).collect(Collectors.toList());
        DeleteStorageFileContext deleteStorageFileContext = new DeleteStorageFileContext();
        deleteStorageFileContext.setFileRealPathList(realPathList);
        try {
            storageEngine.delete(deleteStorageFileContext);
        } catch (IOException e) {
            applicationContext.publishEvent(new ErrorLogEvent(this,
                    "实体文件：" + JSON.toJSONString(realPathList) + "， 物理删除失败，请执行手动删除",
                    GCloudConstants.ZERO_LONG));
        }
    }


    /**
     * 查找所有没有被引用的真实文件记录ID集合
     *
     * @param allRecords
     * @return
     */
    private List<Long> findAllUnusedRealFileIdList(List<GCloudUserFile> allRecords) {
        List<Long> realFileIdList = allRecords.stream()
                .filter(record -> Objects.equals(record.getFolderFlag(), FolderFlagEnum.NO.getCode()))
                .filter(this::isUnused)
                .map(GCloudUserFile::getRealFileId)
                .collect(Collectors.toList());
        return realFileIdList;
    }


    /**
     * 校验文件的真实文件ID是不是没有被引用了
     *
     * @param gCloudUserFile
     * @return
     */
    private boolean isUnused(GCloudUserFile gCloudUserFile) {
        LambdaQueryWrapper<GCloudUserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GCloudUserFile::getRealFileId,gCloudUserFile.getRealFileId());
        return gCloudUserFileService.count(lambdaQueryWrapper) != GCloudConstants.ZERO_INT.intValue();
    }
}
