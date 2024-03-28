package com.grazy.modules.share.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-28 9:47
 * @Description: 转存到我的网盘 上下文参数
 */

@Data
public class ShareSaveContext implements Serializable {

    private static final long serialVersionUID = -9013698031461415283L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 需要转存的文件集合
     */
    private List<Long> fileIdList;

    /**
     * 转存目标文件夹
     */
    private Long targetParentId;
}
