package com.grazy.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-29 20:32
 * @Description: 文件秒传 上下文参数对象
 */

@Data
public class SecUploadFileContext implements Serializable {

    private static final long serialVersionUID = 3609967131815531128L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;


    /**
     * 父文件夹ID
     */
    private Long parentId;


    /**
     * 文件名称
     */
    private String filename;


    /**
     * 文件的唯一标识
     */
    private String identifier;
}
