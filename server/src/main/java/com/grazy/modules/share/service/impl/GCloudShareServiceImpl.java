package com.grazy.modules.share.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.common.config.GCloudServerConfigProperties;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.response.ResponseCode;
import com.grazy.core.utils.IdUtil;
import com.grazy.core.utils.JwtUtil;
import com.grazy.core.utils.UUIDUtil;
import com.grazy.modules.share.constants.ShareConstant;
import com.grazy.modules.share.context.*;
import com.grazy.modules.share.domain.GCloudShare;
import com.grazy.modules.share.domain.GCloudShareFile;
import com.grazy.modules.share.enums.ShareDayTypeEnum;
import com.grazy.modules.share.enums.ShareStatusEnum;
import com.grazy.modules.share.mapper.GCloudShareMapper;
import com.grazy.modules.share.service.GCloudShareFileService;
import com.grazy.modules.share.service.GCloudShareService;
import com.grazy.modules.share.vo.GCloudShareUrlListVo;
import com.grazy.modules.share.vo.GCloudShareUrlVo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    @Transactional(rollbackFor = GCloudBusinessException.class)
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


    /**
     * 取消分享链接
     *  1.校验当前用户是否有取消权限
     *  2.删除对应的分享记录
     *  3.删除对应的分享文件关联关系记录
     *
     * @param context
     */
    @Transactional(rollbackFor = GCloudBusinessException.class)
    @Override
    public void cancelShare(CancelShareContext context) {
        checkUserCancelSharePermission(context);
        doDeleteShare(context);
        doDeleteShareFile(context);
    }


    /**
     * 校验分享提取码
     * 1.检查分享的状态是不是正常
     * 2.检查分享提取码是否正确
     * 3.生成一个短时间的分享token并返回
     *
     * @param context
     * @return
     */
    @Override
    public String checkShareCode(CheckShareCodeContext context) {
        checkShareStatus(context);
        doCheckShareCode(context);
        return generateShareToken(context);
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


    /**
     * 删除对应的分享文件关联关系记录
     *
     * @param context
     */
    private void doDeleteShareFile(CancelShareContext context) {
        LambdaQueryWrapper<GCloudShareFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GCloudShareFile::getCreateUser,context.getUserId())
                .in(GCloudShareFile::getShareId,context.getShareIdList());
        if(!gCloudShareFileService.remove(lambdaQueryWrapper)){
            throw new GCloudBusinessException("取消分享失败");
        }
    }


    /**
     * 执行取消文件分享的动作
     *
     * @param context
     */
    private void doDeleteShare(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        if (!removeByIds(shareIdList)) {
            throw new GCloudBusinessException("取消分享失败");
        }
    }


    /**
     * 校验当前用户是否有取消权限
     *
     * @param context
     */
    private void checkUserCancelSharePermission(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        List<GCloudShare> records = listByIds(shareIdList);
        if(CollectionUtils.isEmpty(records)){
            throw new GCloudBusinessException("分享链接不存在");
        }
        records.stream().forEach(record -> {
            if(!Objects.equals(record.getCreateUser(),context.getUserId())){
                throw new GCloudBusinessException("当前用户暂无取消此分享链接的权限");
            }
        });
    }


    /**
     * 构建一个短期临时分享token
     *
     * @param context
     * @return
     */
    private String generateShareToken(CheckShareCodeContext context) {
        return JwtUtil.generateToken(UUIDUtil.getUUID(), ShareConstant.SHARE_ID,
                context.getShareId(), ShareConstant.ONE_HOUR_LONG);
    }


    /**
     * 检查分享提取码是否正确
     *
     * @param context
     */
    private void doCheckShareCode(CheckShareCodeContext context) {
        GCloudShare record = context.getRecord();
        if(!Objects.equals(context.getShareCode(),record.getShareCode())){
            throw new GCloudBusinessException("提取码错误");
        }
    }


    /**
     * 检查分享的状态是不是正常
     *
     * @param context
     */
    private void checkShareStatus(CheckShareCodeContext context) {
        Long shareId = context.getShareId();
        GCloudShare record = getById(shareId);
        if(Objects.isNull(record)){
            throw new GCloudBusinessException(ResponseCode.SHARE_CANCELLED);
        }
        if(Objects.equals(ShareStatusEnum.FILE_DELETED.getCode(),record.getShareStatus())){
            throw new GCloudBusinessException(ResponseCode.SHARE_FILE_MISS);
        }
        if(Objects.equals(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode(), record.getShareDayType())){
            //如果为永久有效,直接封装记录到上下文参数中，并返回，不需要执行过期时间判断
            context.setRecord(record);
            return;
        }
        if(record.getShareEndTime().before(new Date())){
            throw new GCloudBusinessException(ResponseCode.SHARE_EXPIRE);
        }
        context.setRecord(record);
    }
}




