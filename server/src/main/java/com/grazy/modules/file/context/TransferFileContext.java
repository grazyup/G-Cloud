package com.grazy.modules.file.context;

import com.grazy.modules.file.domain.GCloudUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-12 10:11
 * @Description: 移动文件 上下文参数
 */

@Data
public class TransferFileContext implements Serializable {

    private static final long serialVersionUID = 7672021175676866493L;

    /**
     * 要转移的文件ID集合
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
     * 要转移的文件列表
     */
    private List<GCloudUserFile> prepareRecords;
}
