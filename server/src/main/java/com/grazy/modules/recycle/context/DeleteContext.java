package com.grazy.modules.recycle.context;

import com.grazy.modules.file.domain.GCloudUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-24 0:56
 * @Description: 删除回收站文件上下文参数
 */

@Data
public class DeleteContext implements Serializable {

    private static final long serialVersionUID = 4330592403537586798L;

    /**
     * 要删除的文件id集合
     */
    private List<Long> fileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要被删除的文件记录列表
     */
    private List<GCloudUserFile> records;

    /**
     * 所有要被删除的文件记录列表
     */
    private List<GCloudUserFile> allRecords;
}
