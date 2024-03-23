package com.grazy.modules.recycle.service;

import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.modules.recycle.context.QueryRecycleFileListContext;
import com.grazy.modules.recycle.context.RestoreContext;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-21 14:27
 * @Description:
 */
public interface GCloudRecycleService {

    /**
     * 获取回收站文件列表
     *
     * @param queryRecycleFileListContext
     * @return
     */
    List<UserFileVO> recycle(QueryRecycleFileListContext queryRecycleFileListContext);


    /**
     * 文件批量还原
     *
     * @param restoreContext
     */
    void restore(RestoreContext restoreContext);
}
