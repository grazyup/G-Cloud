package com.grazy.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.modules.file.domain.GCloudFileChunk;
import com.grazy.modules.file.service.GCloudFileChunkService;
import com.grazy.modules.file.mapper.GCloudFileChunkMapper;
import org.springframework.stereotype.Service;

/**
* @author gaofu
* @description 针对表【g_cloud_file_chunk(文件分片信息表)】的数据库操作Service实现
* @createDate 2024-01-24 17:41:31
*/
@Service
public class GCloudFileChunkServiceImpl extends ServiceImpl<GCloudFileChunkMapper, GCloudFileChunk>
    implements GCloudFileChunkService{

}




