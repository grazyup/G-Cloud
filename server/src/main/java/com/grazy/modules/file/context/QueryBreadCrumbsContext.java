package com.grazy.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-13 14:41
 * @Description: 查询面包屑列表 上下文参数
 */

@Data
public class QueryBreadCrumbsContext implements Serializable {

    private static final long serialVersionUID = -4628106618914973581L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
