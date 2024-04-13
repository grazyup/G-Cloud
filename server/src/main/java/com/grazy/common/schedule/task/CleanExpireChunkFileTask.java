package com.grazy.common.schedule.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grazy.common.stream.channel.GCloudChannels;
import com.grazy.common.stream.event.log.ErrorLogEvent;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.modules.file.domain.GCloudFileChunk;
import com.grazy.modules.file.service.GCloudFileChunkService;
import com.grazy.storage.engine.core.StorageEngine;
import com.grazy.storage.engine.core.context.DeleteStorageFileContext;
import com.grazy.stream.core.StreamProducer;
import com.grazy.taskInterface.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2024-03-19 22:51
 * @Description: 清除过期分片文件任务
 */

@Component
@Slf4j
public class CleanExpireChunkFileTask implements ScheduleTask{

    private static final Long BATCH_SIZE = 500L;

    @Resource
    private GCloudFileChunkService gCloudFileChunkService;

    @Resource
    private StorageEngine storageEngine;

    @Resource
    @Qualifier(value = "defaultStreamProducer")
    private StreamProducer producer;

    @Override
    public String getScheduleTaskName() {
        return "CleanExpireChunkFileTask";
    }


    /**
     * 执行清除任务
     * 1.滚动查询过期的文件分片
     * 2.删除物理文件（委托文件存储引擎实现）
     * 3.删除过期分片文件记录
     * 4.重置上次查询的最大文件分片记录ID，继续滚动查询
     */
    @Override
    public void run() {
        log.info("{} start clean expire chunk file...", getScheduleTaskName());

        List<GCloudFileChunk> expireFileChunkRecords;
        Long scrollPointer = 1L;

        do{
            expireFileChunkRecords = scrollQueryExpireFileChunkRecord(scrollPointer);
            if(CollectionUtils.isNotEmpty(expireFileChunkRecords)){
                deleteRealChunkFile(expireFileChunkRecords);
                List<Long> idList = deleteChunkFileRecord(expireFileChunkRecords);
                //更新滚动查询的最大分片记录ID
                scrollPointer = Collections.max(idList);
            }
        }while (CollectionUtils.isNotEmpty(expireFileChunkRecords));
        log.info("{} finish clean expire chunk file...", getScheduleTaskName());
    }


    /**
     * 滚动查询过期文件记录
     *
     * @param scrollPointer
     * @return
     */
    private List<GCloudFileChunk> scrollQueryExpireFileChunkRecord(Long scrollPointer) {
        LambdaQueryWrapper<GCloudFileChunk> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.le(GCloudFileChunk::getExpirationTime, new Date())
                .ge(GCloudFileChunk::getId, scrollPointer)
                .last("limit " + BATCH_SIZE);
        return gCloudFileChunkService.list(lambdaQueryWrapper);
    }


    /**
     *  删除物理文件
     *
     * @param expireFileChunkRecords
     */
    private void deleteRealChunkFile(List<GCloudFileChunk> expireFileChunkRecords) {
        DeleteStorageFileContext deleteStorageFileContext = new DeleteStorageFileContext();
        List<String> realPathList = expireFileChunkRecords.stream().map(GCloudFileChunk::getRealPath).collect(Collectors.toList());
        deleteStorageFileContext.setFileRealPathList(realPathList);
        try {
            storageEngine.delete(deleteStorageFileContext);
        } catch (IOException e) {
            saveErrorLog(realPathList);
        }
    }

    /**
     * 保存错误日志
     * @param realPathList
     */
    private void saveErrorLog(List<String> realPathList) {
        ErrorLogEvent errorLogEvent = new ErrorLogEvent("文件物理删除失败，请手动执行文件删除，文件路径为: "
                + JSON.toJSONString(realPathList), GCloudConstants.ZERO_LONG);
        producer.sendMessage(GCloudChannels.ERROR_LOG_OUTPUT, errorLogEvent);
    }


    /**
     * 删除分片文件记录
     *
     * @param expireFileChunkRecords
     * @return
     */
    private List<Long> deleteChunkFileRecord(List<GCloudFileChunk> expireFileChunkRecords) {
        List<Long> idList = expireFileChunkRecords.stream().map(GCloudFileChunk::getId).collect(Collectors.toList());
        gCloudFileChunkService.removeByIds(idList);
        return idList;
    }


}
