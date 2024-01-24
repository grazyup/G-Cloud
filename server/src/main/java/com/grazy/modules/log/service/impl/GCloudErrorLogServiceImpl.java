package com.grazy.modules.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.modules.log.domain.GCloudErrorLog;
import com.grazy.modules.log.service.GCloudErrorLogService;
import com.grazy.modules.log.mapper.GCloudErrorLogMapper;
import org.springframework.stereotype.Service;

/**
* @author gaofu
* @description 针对表【g_cloud_error_log(错误日志表)】的数据库操作Service实现
* @createDate 2024-01-24 17:44:07
*/
@Service
public class GCloudErrorLogServiceImpl extends ServiceImpl<GCloudErrorLogMapper, GCloudErrorLog>
    implements GCloudErrorLogService{

}




