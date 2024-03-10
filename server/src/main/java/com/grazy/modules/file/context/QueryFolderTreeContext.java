package com.grazy.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-10 15:54
 * @Description: 查询文件夹树上下文参数
 */

@Data
public class QueryFolderTreeContext implements Serializable {

    private static final long serialVersionUID = 5158436469967857327L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
