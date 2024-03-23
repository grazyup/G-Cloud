package com.grazy.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-23 15:32
 * @Description: 文件还原事件
 */

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class FileRestoreEvent extends ApplicationEvent {

    /**
     * 被还原的文件ID集合
     */
    private List<Long> fileIdList;


    public FileRestoreEvent(Object source, List<Long> fileIdList) {
        super(source);
        this.fileIdList = fileIdList;
    }
}
