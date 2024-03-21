package com.grazy.modules.recycle.service.impl;

import com.grazy.modules.file.context.QueryFileListContext;
import com.grazy.modules.file.enums.DelFlagEnum;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.modules.recycle.context.QueryRecycleFileListContext;
import com.grazy.modules.recycle.service.GCloudRecycleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-21 14:28
 * @Description:
 */

@Service
public class GCloudRecycleServiceImpl implements GCloudRecycleService {

    @Resource
    private GCloudUserFileService userFileService;


    /**
     * 获取回收站文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<UserFileVO> recycle(QueryRecycleFileListContext context) {
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(context.getUserId());
        queryFileListContext.setDelFlag(DelFlagEnum.YES.getCode());
        return userFileService.getFileList(queryFileListContext);
    }
}
