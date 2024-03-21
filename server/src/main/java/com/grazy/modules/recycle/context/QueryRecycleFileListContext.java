package com.grazy.modules.recycle.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-21 14:33
 * @Description: 查询回收站文件列表上下文参数
 */

@Data
public class QueryRecycleFileListContext implements Serializable {

    private static final long serialVersionUID = 3953787158219755123L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
