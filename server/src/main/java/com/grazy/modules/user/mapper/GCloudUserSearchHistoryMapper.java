package com.grazy.modules.user.mapper;

import com.grazy.modules.user.context.QueryUserSearchHistoryContext;
import com.grazy.modules.user.domain.GCloudUserSearchHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grazy.modules.user.vo.UserSearchHistoryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author gaofu
* @description 针对表【g_cloud_user_search_history(用户搜索历史表)】的数据库操作Mapper
* @createDate 2024-01-24 17:37:06
* @Entity com.grazy.modules.user.domain.GCloudUserSearchHistory
*/
public interface GCloudUserSearchHistoryMapper extends BaseMapper<GCloudUserSearchHistory> {


    /**
     * 查询用户的最近十条搜索历史记录
     *
     * @param context
     * @return
     */
    List<UserSearchHistoryVO> selectUserSearchHistories(@Param("param") QueryUserSearchHistoryContext context);
}




