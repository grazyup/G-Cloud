package com.grazy.modules.file.context;

import com.grazy.modules.file.domain.GCloudUserFile;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-28 21:59
 * @Description: 业务层文件重命名上下文参数
 */

@Data
public class UpdateFilenameContext implements Serializable {

    private static final long serialVersionUID = -450664762721066793L;

    /**
     * 更新的文件ID
     */
    private Long fileId;


    /**
     * 新的文件名称
     */
    private String newFilename;


    /**
     * 用户ID
     */
    private Long userId;


    /**
     * 用户文件实体
     */
    private GCloudUserFile entity;
}
