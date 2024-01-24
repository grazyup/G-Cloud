package com.grazy.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.modules.file.domain.GCloudFile;
import com.grazy.modules.file.service.GCloudFileService;
import com.grazy.modules.file.mapper.GCloudFileMapper;
import org.springframework.stereotype.Service;

/**
* @author gaofu
* @description 针对表【g_cloud_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2024-01-24 17:41:31
*/
@Service
public class GCloudFileServiceImpl extends ServiceImpl<GCloudFileMapper, GCloudFile>
    implements GCloudFileService{

}




