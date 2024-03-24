package com.grazy.modules.share.context;

import lombok.Data;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-24 23:00
 * @Description: 保存分享文件关联关系上下文参数
 */

@Data
public class SaveShareFilesContext {

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 分享的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
