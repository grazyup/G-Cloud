package com.grazy.modules.user.service;

import com.grazy.modules.user.context.QueryUserSearchHistoryContext;
import com.grazy.modules.user.domain.GCloudUserSearchHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.grazy.modules.user.vo.UserSearchHistoryVO;

import java.util.List;

/**
* @author gaofu
* @description 针对表【g_cloud_user_search_history(用户搜索历史表)】的数据库操作Service
* @createDate 2024-01-24 17:37:06
*/
public interface GCloudUserSearchHistoryService extends IService<GCloudUserSearchHistory> {


    /**
     * 查询用户的搜索历史记录，默认十条
     *
     * @param context
     * @return
     */
    List<UserSearchHistoryVO> getUserSearchHistories(QueryUserSearchHistoryContext context);
}
