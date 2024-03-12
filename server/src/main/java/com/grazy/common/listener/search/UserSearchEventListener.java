package com.grazy.common.listener.search;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.grazy.common.event.search.SearchEvent;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.user.domain.GCloudUserSearchHistory;
import com.grazy.modules.user.service.GCloudUserSearchHistoryService;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2024-03-12 17:53
 * @Description: 用户搜索事件监听
 */

@Component
public class UserSearchEventListener {

    @Resource
    private GCloudUserSearchHistoryService gCloudUserSearchHistoryService;

    /**
     * 监听用户搜索事件，将其保存到用户的搜索历史记录当中
     */
    @EventListener(SearchEvent.class)
    public void saveSearchHistory(SearchEvent event){
        GCloudUserSearchHistory gCloudUserSearchHistory = new GCloudUserSearchHistory();
        gCloudUserSearchHistory.setUserId(event.getUserId());
        gCloudUserSearchHistory.setSearchContent(event.getKeyword());
        gCloudUserSearchHistory.setId(IdUtil.get());
        gCloudUserSearchHistory.setCreateTime(new Date());
        gCloudUserSearchHistory.setUpdateTime(new Date());
        try{
            gCloudUserSearchHistoryService.save(gCloudUserSearchHistory);
        }catch (DuplicateKeyException e){
            UpdateWrapper<GCloudUserSearchHistory> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id",event.getUserId())
                    .eq("search_content",event.getKeyword())
                    .set("update_time",new Date());
            gCloudUserSearchHistoryService.update(updateWrapper);
        }
    }

}
