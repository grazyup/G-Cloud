//package com.grazy.common.listener.share;
//
//import com.grazy.common.event.file.DeleteFileEvent;
//import com.grazy.common.event.file.FileRestoreEvent;
//import com.grazy.modules.file.domain.GCloudUserFile;
//import com.grazy.modules.file.enums.DelFlagEnum;
//import com.grazy.modules.file.service.GCloudUserFileService;
//import com.grazy.modules.share.service.GCloudShareService;
//import org.apache.commons.collections.CollectionUtils;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * @Author: grazy
// * @Date: 2024-03-28 23:04
// * @Description: 监听文件状态变更导致分享状态变更的处理器
// */
//
//@Component
//public class ShareStatusChangeListener {
//
//    @Resource
//    private GCloudUserFileService userFileService;
//
//    @Resource
//    private GCloudShareService gCloudShareService;
//
//
//    /**
//     * 监听文件进入回收站之后，刷新所有受影响的分享状态
//     *  分享连接中的文件夹下面的子文件被删除不会修改当前文件所在的分享状态
//     *  当前链接的文件以及其父文件被删除将执行修改分享状态
//     *
//     * @param event
//     */
//    @Async(value = "eventListenerTaskExecutor")
//    @EventListener(DeleteFileEvent.class)
//    public void changeShareStatusToFileDelete(DeleteFileEvent event){
//        List<Long> fileIdList = event.getFileIdList();
//        if(CollectionUtils.isEmpty(fileIdList)){
//            return;
//        }
//        //查询所有的子文件
//        List<GCloudUserFile> allRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList);
//        //过滤掉已删除的文件是因为已放入回收站的文件已经执行过这个步骤了，就不需要重复执行，只需要校验当前链接中的全部文件及子文件放入回收站所影响的分享链接即可
//        List<Long> allAvailableFileIdList = allRecords.stream()
//                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
//                .map(GCloudUserFile::getFileId)
//                .collect(Collectors.toList());
//        allAvailableFileIdList.addAll(fileIdList);
//        gCloudShareService.refreshShareStatus(allAvailableFileIdList);
//    }
//
//
//    /**
//     * 监听文件回收站还原之后，刷新所有受影响的分享状态
//     *
//     * @param event
//     */
//    @Async(value = "eventListenerTaskExecutor")
//    @EventListener(FileRestoreEvent.class)
//    public void changeShareStatusToFileNormal(FileRestoreEvent event){
//        List<Long> fileIdList = event.getFileIdList();
//        if(CollectionUtils.isEmpty(fileIdList)){
//            return;
//        }
//        //查询所有的子文件
//        List<GCloudUserFile> allRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList);
//        List<Long> allAvailableFileIdList = allRecords.stream()
//                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
//                .map(GCloudUserFile::getFileId)
//                .collect(Collectors.toList());
//        allAvailableFileIdList.addAll(fileIdList);
//        gCloudShareService.refreshShareStatus(allAvailableFileIdList);
//
//    }
//}
