package com.grazy.modules.file.context;

import com.grazy.modules.file.domain.GCloudUserFile;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-02 1:27
 * @Description: 业务层创建文件夹上下文参数
 */

@Data
public class CreateFolderContext implements Serializable {

    private static final long serialVersionUID = 6640010441869005004L;


    /**
     * 父文件夹Id
     */
    private Long parentId;


    /**
     * 文件夹名称
     */
    private String folderName;


    /**
     * 用户Id
     */
    private Long userId;

}
