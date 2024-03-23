package com.grazy.modules.recycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grazy.common.event.file.FileRestoreEvent;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.modules.file.context.QueryFileListContext;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.grazy.modules.file.enums.DelFlagEnum;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.modules.recycle.context.QueryRecycleFileListContext;
import com.grazy.modules.recycle.context.RestoreContext;
import com.grazy.modules.recycle.service.GCloudRecycleService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2024-03-21 14:28
 * @Description:
 */

@Service
public class GCloudRecycleServiceImpl implements GCloudRecycleService, ApplicationContextAware {

    @Resource
    private GCloudUserFileService userFileService;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取回收站文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<UserFileVO> recycle(QueryRecycleFileListContext context) {
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(context.getUserId());
        queryFileListContext.setDelFlag(DelFlagEnum.YES.getCode());
        return userFileService.getFileList(queryFileListContext);
    }


    /**
     * 文件批量还原
     * 1.检查操作权限
     * 2.检查文件是否满足还原条件
     * 3.执行文件还原操作
     * 4.执行文件还原的后置操作
     *
     * @param context
     */
    @Override
    public void restore(RestoreContext context) {
        checkRestorePermission(context);
        checkRestoreFilename(context);
        doRestore(context);
        afterRestore(context);
    }



    /********************************************** private方法 **********************************************/

    /**
     * 发布文件还原事件
     *
     * @param context
     */
    private void afterRestore(RestoreContext context) {
        FileRestoreEvent fileRestoreEvent = new FileRestoreEvent(this, context.getFileIdList());
        applicationContext.publishEvent(fileRestoreEvent);
    }


    /**
     * 执行文件还原
     *
     * @param context
     */
    private void doRestore(RestoreContext context) {
        List<GCloudUserFile> records = context.getRecords();
        records.stream().forEach(record -> {
            record.setDelFlag(DelFlagEnum.NO.getCode());
            record.setUpdateUser(context.getUserId());
            record.setUpdateTime(new Date());
        });
        if(!userFileService.updateBatchById(records)){
            throw new GCloudBusinessException("文件还原失败");
        }
    }


    /**
     * 检查要还原的文件是否满足还原条件
     *  a.回收站中不能还原同一个父文件夹下面的相同文件名文件（两者都找回收站）
     *  b.要还原的文件的文件名不能与同一父文件下存在相同名的文件共同存在(后者没有被删除)
     *
     * @param context
     */
    private void checkRestoreFilename(RestoreContext context) {
        List<GCloudUserFile> records = context.getRecords();
        //检查要还原的文件列表中是否存在同名且同一个父文件夹的文件
        Set<String> filenameSet = records.stream()
                .map(record -> record.getFilename() + GCloudConstants.COMMON_SEPARATOR + record.getParentId())
                .collect(Collectors.toSet());
        if(filenameSet.size() != records.size()){
            throw new GCloudBusinessException("文件还原失败，该还原文件列表中存在同名文件，请逐个还原并重命名");
        }
        records.stream().forEach(record -> {
            //检查要还原的文件中的还原后的同一父文件夹下是否存在相同名称文件
            LambdaQueryWrapper<GCloudUserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(GCloudUserFile::getUserId,context.getUserId())
                    .eq(GCloudUserFile::getParentId,record.getParentId())
                    .eq(GCloudUserFile::getFilename,record.getFilename())
                    .eq(GCloudUserFile::getDelFlag,DelFlagEnum.NO);
            if(userFileService.count(lambdaQueryWrapper) > 0){
                throw new GCloudBusinessException("文件: " + record.getFilename() + " 还原失败，该文件夹下面已经存在了相同名称的文件或者文件夹，请重命名之后再执行文件还原操作");
            }
        });
    }


    /**
     * 检查操作权限
     *
     * @param context
     */
    private void checkRestorePermission(RestoreContext context) {
        List<Long> fileIdList = context.getFileIdList();
        List<GCloudUserFile> records = userFileService.listByIds(fileIdList);
        if(CollectionUtils.isEmpty(records)){
            throw new GCloudBusinessException("文件还原失败");
        }
        Set<Long> userIdSet = records.stream().map(GCloudUserFile::getUserId).collect(Collectors.toSet());
        if(userIdSet.size() > 1){
            throw new GCloudBusinessException("您无权执行文件还原");
        }
        if(!userIdSet.contains(context.getUserId())){
            throw new GCloudBusinessException("您无权执行文件还原");
        }
        context.setRecords(records);
    }
}
