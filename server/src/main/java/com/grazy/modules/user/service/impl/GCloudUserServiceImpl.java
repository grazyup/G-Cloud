package com.grazy.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.modules.user.domain.GCloudUser;
import com.grazy.modules.user.service.GCloudUserService;
import com.grazy.modules.user.mapper.GCloudUserMapper;
import org.springframework.stereotype.Service;

/**
* @author gaofu
* @description 针对表【g_cloud_user(用户信息表)】的数据库操作Service实现
* @createDate 2024-01-24 17:35:44
*/
@Service
public class GCloudUserServiceImpl extends ServiceImpl<GCloudUserMapper, GCloudUser>
    implements GCloudUserService{

}




