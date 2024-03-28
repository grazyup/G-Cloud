package com.grazy.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-02-29 10:24
 * @Description: 文件删除事件(放入回收站)
 */

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class DeleteFileEvent extends ApplicationEvent {

    private List<Long> fileIdList;

    public DeleteFileEvent(Object source, List<Long> fileIdList){
        super(source);
        this.fileIdList = fileIdList;
    }
}
