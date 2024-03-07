package com.grazy.modules.file.context;

import com.grazy.modules.file.domain.GCloudFile;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-06 23:09
 * @Description: 合并分片并保存记录 上下文参数
 */

@Data
public class FileChunkMergeAndSaveContext implements Serializable {

    private static final long serialVersionUID = 1452959438816234104L;

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

    /**
     * 合并后的文件存储路径
     */
    private String realPath;
}
