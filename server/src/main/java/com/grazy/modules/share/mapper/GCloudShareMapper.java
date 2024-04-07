package com.grazy.modules.share.mapper;

import com.grazy.modules.share.domain.GCloudShare;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.grazy.modules.share.vo.GCloudShareUrlListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author gaofu
* @description 针对表【g_cloud_share(用户分享表)】的数据库操作Mapper
* @createDate 2024-01-24 17:43:16
* @Entity com.grazy.modules.share.domain.GCloudShare
*/
public interface GCloudShareMapper extends BaseMapper<GCloudShare> {


    /**
     * 根据用户ID查询当前用户的分享链接列表
     *
     * @param userId
     * @return
     */
    List<GCloudShareUrlListVo> selectShareVOListByUserId(@Param("userId") Long userId);


    /**
     * 滚动查询分享ID
     *
     * @param startId
     * @param limit
     * @return
     */
    List<Long> rollingQueryShareId(@Param("startId") long startId, @Param("limit") long limit);
}




