package com.grazy.common.stream.event.file;

import lombok.*;

import java.io.Serializable;
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
@NoArgsConstructor
public class FileRestoreEvent implements Serializable {

    private static final long serialVersionUID = 5350091880098679074L;

    /**
     * 被还原的文件ID集合
     */
    private List<Long> fileIdList;


    public FileRestoreEvent(List<Long> fileIdList) {
        this.fileIdList = fileIdList;
    }
}
