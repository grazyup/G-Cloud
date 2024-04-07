package com.grazy.modules.share.service;

import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.modules.share.context.*;
import com.grazy.modules.share.domain.GCloudShare;
import com.baomidou.mybatisplus.extension.service.IService;
import com.grazy.modules.share.po.ShareSimpleDetailVo;
import com.grazy.modules.share.vo.GCloudShareUrlListVo;
import com.grazy.modules.share.vo.GCloudShareUrlVo;
import com.grazy.modules.share.vo.ShareDetailVo;

import java.util.List;

/**
* @author gaofu
* @description 针对表【g_cloud_share(用户分享表)】的数据库操作Service
* @createDate 2024-01-24 17:43:16
*/
public interface GCloudShareService extends IService<GCloudShare> {

    /**
     * 创建分享链接
     *
     * @param context
     * @return
     */
    GCloudShareUrlVo create(CreateShareUrlContext context);


    /**
     * 查询分享链接列表
     *
     * @param context
     * @return
     */
    List<GCloudShareUrlListVo> getShares(QueryShareListContext context);


    /**
     * 取消分享链接
     *
     * @param context
     */
    void cancelShare(CancelShareContext context);


    /**
     * 校验分享提取码
     *
     * @param context
     * @return
     */
    String checkShareCode(CheckShareCodeContext context);


    /**
     * 获取分享详情
     *
     * @param context
     * @return
     */
    ShareDetailVo detail(QueryShareDetailContext context);


    /**
     * 查询分享的简单详情
     *
     * @param context
     * @return
     */
    ShareSimpleDetailVo simpleDetail(ShareSimpleDetailContext context);


    /**
     * 获取下一级文件列表
     *
     * @param context
     * @return
     */
    List<UserFileVO> fileList(QueryChildFileListContext context);


    /**
     * 转存到我的网盘
     *
     * @param context
     */
    void saveFiles(ShareSaveContext context);


    /**
     * 分享文件下载
     *
     * @param context
     */
    void download(ShareFileDownloadContext context);


    /**
     * 刷新分享链接状态
     *
     * @param allAvailableFileIdList
     */
    void refreshShareStatus(List<Long> allAvailableFileIdList);


    /**
     * 滚动查询分享ID
     *
     * @param startId
     * @param limit
     * @return
     */
    List<Long> rollingQueryShareId(long startId, long limit);
}
