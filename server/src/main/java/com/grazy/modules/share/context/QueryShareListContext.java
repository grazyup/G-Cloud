package com.grazy.modules.share.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-25 0:16
 * @Description: 查询分享链接列表的上下文参数
 */

@Data
public class QueryShareListContext implements Serializable {

    private static final long serialVersionUID = -6750894659024891187L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
