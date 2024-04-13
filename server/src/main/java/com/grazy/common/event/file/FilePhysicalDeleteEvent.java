//package com.grazy.common.event.file;
//
//import com.grazy.modules.file.domain.GCloudUserFile;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//import org.springframework.context.ApplicationEvent;
//
//import java.util.List;
//
///**
// * @Author: grazy
// * @Date: 2024-03-24 1:35
// * @Description: 删除回收站文件事件
// */
//
//@Getter
//@Setter
//@EqualsAndHashCode
//@ToString
//public class FilePhysicalDeleteEvent extends ApplicationEvent {
//
//    /**
//     * 所有被物理删除的文件实体集合
//     */
//    private List<GCloudUserFile> allRecords;
//
//    public FilePhysicalDeleteEvent(Object source, List<GCloudUserFile> allRecords) {
//        super(source);
//        this.allRecords = allRecords;
//    }
//}
