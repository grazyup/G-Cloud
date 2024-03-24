package com.grazy.modules.share.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.share.context.SaveShareFilesContext;
import com.grazy.modules.share.domain.GCloudShareFile;
import com.grazy.modules.share.service.GCloudShareFileService;
import com.grazy.modules.share.mapper.GCloudShareFileMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author gaofu
* @description 针对表【g_cloud_share_file(用户分享文件表)】的数据库操作Service实现
* @createDate 2024-01-24 17:43:16
*/
@Service
public class GCloudShareFileServiceImpl extends ServiceImpl<GCloudShareFileMapper, GCloudShareFile> implements GCloudShareFileService{


    /**
     * 保存分享与文件的关联关系记录
     *
     * @param saveShareFilesContext
     */
    @Override
    public void saveShareFiles(SaveShareFilesContext saveShareFilesContext) {
        List<GCloudShareFile> shareFileList = new ArrayList<>();
        List<Long> fileIdList = saveShareFilesContext.getFileIdList();
        fileIdList.stream().forEach(fileId ->{
            GCloudShareFile gCloudShareFile = new GCloudShareFile();
            gCloudShareFile.setId(IdUtil.get());
            gCloudShareFile.setFileId(fileId);
            gCloudShareFile.setShareId(saveShareFilesContext.getShareId());
            gCloudShareFile.setCreateUser(saveShareFilesContext.getUserId());
            gCloudShareFile.setCreateTime(new Date());
            shareFileList.add(gCloudShareFile);
        });
        if(!saveBatch(shareFileList)){
            throw new GCloudBusinessException("保存文件分享关联关系失败");
        }
    }
}




