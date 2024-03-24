package com.grazy.modules.share.service;

import com.grazy.modules.share.context.SaveShareFilesContext;
import com.grazy.modules.share.domain.GCloudShareFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author gaofu
* @description 针对表【g_cloud_share_file(用户分享文件表)】的数据库操作Service
* @createDate 2024-01-24 17:43:16
*/
public interface GCloudShareFileService extends IService<GCloudShareFile> {

    /**
     * 保存分享与文件的关联关系记录
     *
     * @param saveShareFilesContext
     */
    void saveShareFiles(SaveShareFilesContext saveShareFilesContext);
}
