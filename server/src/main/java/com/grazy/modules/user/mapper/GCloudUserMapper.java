package com.grazy.modules.user.mapper;

import com.grazy.modules.user.domain.GCloudUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author gaofu
* @description 针对表【g_cloud_user(用户信息表)】的数据库操作Mapper
* @createDate 2024-01-24 17:35:44
* @Entity com.grazy.modules.user.domain.GCloudUser
*/
public interface GCloudUserMapper extends BaseMapper<GCloudUser> {

    /**
     * 根据用户名查询密保问题
     * @param username 用户名
     * @return 密保问题
     */
    String selectQuestionByUsername(@Param("username") String username);

}




