package com.grazy.modules.file.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.common.config.GCloudServerConfigProperties;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.file.context.FileChunkSaveContext;
import com.grazy.modules.file.converter.FileConverter;
import com.grazy.modules.file.domain.GCloudFileChunk;
import com.grazy.modules.file.enums.MergeFlagEnum;
import com.grazy.modules.file.mapper.GCloudFileChunkMapper;
import com.grazy.modules.file.service.GCloudFileChunkService;
import com.grazy.storage.engine.core.StorageEngine;
import com.grazy.storage.engine.core.context.StoreChunkFileContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author gaofu
* @description 针对表【g_cloud_file_chunk(文件分片信息表)】的数据库操作Service实现
* @createDate 2024-01-24 17:41:31
*/
@Service
public class GCloudFileChunkServiceImpl extends ServiceImpl<GCloudFileChunkMapper, GCloudFileChunk> implements GCloudFileChunkService{

    @Resource
    private GCloudServerConfigProperties serverConfigProperties;

    @Resource
    private StorageEngine storageEngine;

    @Resource
    private FileConverter fileConverter;


    /**
     * 上传分片文件并保存分片上传的记录
     * 1.保存分片
     * 2.保存记录
     * 3.判断是否需要合并
     *
     * @param context 分片文件保存上下文信息
     */
    @Override
    public void saveChunkFile(FileChunkSaveContext context) {
        doStoreChunkFile(context);
        saveChunkFileRecord(context);
        doJudgeMergeFile(context);
    }


    /********************************************** private方法 **********************************************/

    /**
     * 判断分片是否需要合并
     *
     * @param context
     */
    private void doJudgeMergeFile(FileChunkSaveContext context) {
        LambdaQueryWrapper<GCloudFileChunk> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GCloudFileChunk::getIdentifier,context.getIdentifier())
                .eq(GCloudFileChunk::getCreateUser,context.getUserId());
        if(count(lambdaQueryWrapper) == context.getTotalChunks().intValue()){
            context.setMergeFlagEnum(MergeFlagEnum.READY);
        }
    }


    /**
     * 保存分片上传记录
     *
     * @param context
     */
    private void saveChunkFileRecord(FileChunkSaveContext context) {
        GCloudFileChunk gCloudFileChunk = new GCloudFileChunk();
        gCloudFileChunk.setId(IdUtil.get());
        gCloudFileChunk.setChunkNumber(context.getChunkNumber());
        gCloudFileChunk.setRealPath(context.getRealPath());
        gCloudFileChunk.setCreateUser(context.getUserId());
        gCloudFileChunk.setIdentifier(context.getIdentifier());
        gCloudFileChunk.setExpirationTime(DateUtil.offsetDay(new Date(),
                serverConfigProperties.getChunkFileExpirationDays()));
        gCloudFileChunk.setCreateTime(new Date());

        if(!save(gCloudFileChunk)){
            //此处不删除服务器中已上传的分片是因为设置了过期时间，会有定时任务定期检查删除
            throw new GCloudBusinessException("文件分片上传失败！");
        }
    }


    /**
     * 保存分片(委托文件存储引擎保存文件分片)
     *
     * @param context
     */
    private void doStoreChunkFile(FileChunkSaveContext context) {
        try {
            StoreChunkFileContext storeChunkFileContext = fileConverter.FileChunkSaveContextToStoreChunkFileContext(context);
            storeChunkFileContext.setInputStream(context.getFile().getInputStream());
            storageEngine.storeChunkFile(storeChunkFileContext);
            context.setRealPath(storeChunkFileContext.getRealPath());
        }catch (Exception e){
            e.printStackTrace();
            throw new GCloudBusinessException("文件分片上传失败！");
        }
    }
}




