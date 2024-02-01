package com.grazy.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.modules.user.domain.GCloudUserSearchHistory;
import com.grazy.modules.user.service.GCloudUserSearchHistoryService;
import com.grazy.modules.user.mapper.GCloudUserSearchHistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author gaofu
* @description 针对表【g_cloud_user_search_history(用户搜索历史表)】的数据库操作Service实现
* @createDate 2024-01-24 17:37:06
*/
@Service(value = "userSearchHistoryService")
public class GCloudUserSearchHistoryServiceImpl extends ServiceImpl<GCloudUserSearchHistoryMapper, GCloudUserSearchHistory>
    implements GCloudUserSearchHistoryService{

}




