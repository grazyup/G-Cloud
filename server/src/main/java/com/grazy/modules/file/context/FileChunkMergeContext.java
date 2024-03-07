package com.grazy.modules.file.context;

import com.grazy.modules.file.domain.GCloudFile;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-06 22:49
 * @Description: 合并分片文件 上下文参数对象
 */

@Data
public class FileChunkMergeContext implements Serializable {

    private static final long serialVersionUID = 5381117283480169954L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 文件实体记录
     */
    private GCloudFile record;
}
