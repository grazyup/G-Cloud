package com.grazy.modules.file.context;

import com.grazy.modules.file.domain.GCloudUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-12 14:36
 * @Description: 文件复制 上下文参数对象
 */

@Data
public class CopyFileContext implements Serializable {
    
    private static final long serialVersionUID = -2500317816700795101L;

    /**
     * 要复制的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 目标文件夹ID
     */
    private Long targetParentId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要复制的文件列表
     */
    private List<GCloudUserFile> prepareRecords;
}
