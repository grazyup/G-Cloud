package com.grazy.common.stream.event.file;

import lombok.*;

import java.io.Serializable;
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
@NoArgsConstructor
public class DeleteFileEvent implements Serializable {

    private static final long serialVersionUID = 9013744299773098987L;

    private List<Long> fileIdList;

    public DeleteFileEvent(List<Long> fileIdList) {
        this.fileIdList = fileIdList;
    }
}
