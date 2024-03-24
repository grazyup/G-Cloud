package com.grazy.modules.share.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.common.config.GCloudServerConfigProperties;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.share.context.CreateShareUrlContext;
import com.grazy.modules.share.context.QueryShareListContext;
import com.grazy.modules.share.context.SaveShareFilesContext;
import com.grazy.modules.share.domain.GCloudShare;
import com.grazy.modules.share.enums.ShareDayTypeEnum;
import com.grazy.modules.share.enums.ShareStatusEnum;
import com.grazy.modules.share.mapper.GCloudShareMapper;
import com.grazy.modules.share.service.GCloudShareFileService;
import com.grazy.modules.share.service.GCloudShareService;
import com.grazy.modules.share.vo.GCloudShareUrlListVo;
import com.grazy.modules.share.vo.GCloudShareUrlVo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author gaofu
* @description 针对表【g_cloud_share(用户分享表)】的数据库操作Service实现
* @createDate 2024-01-24 17:43:16
*/
@Service
public class GCloudShareServiceImpl extends ServiceImpl<GCloudShareMapper, GCloudShare> implements GCloudShareService{

    @Resource
    private GCloudServerConfigProperties configProperties;

    @Resource
    private GCloudShareFileService gCloudShareFileService;

    @Resource
    private GCloudShareMapper gCloudShareMapper;


    /**
     * 创建分享链接
     * 1.拼装分享实体记录，保存到数据库
     * 2.保存分享与文件的关联关系记录
     * 3.拼装响应实体信息并返回
     *
     * @param context
     * @return
     */
    @Override
    public GCloudShareUrlVo create(CreateShareUrlContext context) {
        saveShare(context);
        saveShareFiles(context);
        return assembleShareVO(context);
    }


    /**
     * 查询分享链接列表
     *
     * @param context
     * @return
     */
    @Override
    public List<GCloudShareUrlListVo> getShares(QueryShareListContext context) {
        return gCloudShareMapper.selectShareVOListByUserId(context.getUserId());
    }


    /********************************************** private方法 **********************************************/

    /**
     * 拼装响应实体信息并返回
     *
     * @param context
     * @return
     */
    private GCloudShareUrlVo assembleShareVO(CreateShareUrlContext context) {
        GCloudShare record = context.getRecord();
        GCloudShareUrlVo gCloudShareUrlVo = new GCloudShareUrlVo();
        gCloudShareUrlVo.setShareUrl(record.getShareUrl());
        gCloudShareUrlVo.setShareId(record.getShareId());
        gCloudShareUrlVo.setShareCode(record.getShareCode());
        gCloudShareUrlVo.setShareName(record.getShareName());
        gCloudShareUrlVo.setShareStatus(record.getShareStatus());
        return gCloudShareUrlVo;
    }


    /**
     * 保存分享与文件的关联关系记录
     *
     * @param context
     */
    private void saveShareFiles(CreateShareUrlContext context) {
        GCloudShare record = context.getRecord();
        SaveShareFilesContext saveShareFilesContext = new SaveShareFilesContext();
        saveShareFilesContext.setShareId(record.getShareId());
        saveShareFilesContext.setFileIdList(context.getShareFileIdList());
        saveShareFilesContext.setUserId(context.getUserId());
        gCloudShareFileService.saveShareFiles(saveShareFilesContext);
    }


    /**
     * 拼装分享实体记录，保存到数据库
     *
     * @param context
     */
    private void saveShare(CreateShareUrlContext context) {
        GCloudShare record = new GCloudShare();
        record.setShareId(IdUtil.get());
        record.setShareName(context.getShareName());
        record.setShareType(context.getShareType());
        record.setShareDayType(context.getShareDayType());
        Integer shareDay = ShareDayTypeEnum.getShareDayByCode(context.getShareDayType());
        if (Objects.equals(GCloudConstants.MINUS_ONE_INT, shareDay)) {
            throw new GCloudBusinessException("分享天数非法");
        }
        record.setShareDay(shareDay);
        record.setShareEndTime(DateUtil.offsetDay(new Date(), shareDay));
        record.setShareUrl(createShareUrl(record.getShareId()));
        record.setShareCode(createShareCode());
        record.setShareStatus(ShareStatusEnum.NORMAL.getCode());
        record.setCreateUser(context.getUserId());
        record.setCreateTime(new Date());

        if(!save(record)){
            throw new GCloudBusinessException("保存分享信息失败");
        }
        context.setRecord(record);
    }


    /**
     * 创建分享提取码
     *
     * @return
     */
    private String createShareCode() {
        return RandomStringUtils.randomAlphabetic(4).toLowerCase();
    }


    /**
     * 创建分享链接URL
     *
     * @param shareId
     * @return
     */
    private String createShareUrl(Long shareId) {
        if(Objects.isNull(shareId)){
            throw new GCloudBusinessException("分享的ID不能为空");
        }
        String sharePrefix = configProperties.getSharePrefix();
        if(sharePrefix.lastIndexOf(GCloudConstants.SLASH_STR) == GCloudConstants.MINUS_ONE_INT){
            sharePrefix = sharePrefix + GCloudConstants.SLASH_STR;
        }
        return sharePrefix + shareId;
    }
}




