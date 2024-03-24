package com.grazy.modules.share.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-25 0:54
 * @Description: 取消分享链接上下文参数
 */

@Data
public class CancelShareContext implements Serializable {

    private static final long serialVersionUID = 3883826779209894765L;

    /**
     * 需要取消的分享ID集合
     */
    private List<Long> shareIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
