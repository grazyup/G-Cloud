package com.grazy.modules.share.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.grazy.common.config.GCloudServerConfigProperties;
import com.grazy.common.event.log.ErrorLogEvent;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.response.ResponseCode;
import com.grazy.core.utils.IdUtil;
import com.grazy.core.utils.JwtUtil;
import com.grazy.core.utils.UUIDUtil;
import com.grazy.modules.file.constants.FileConstants;
import com.grazy.modules.file.context.CopyFileContext;
import com.grazy.modules.file.context.FileDownloadContext;
import com.grazy.modules.file.context.QueryFileListContext;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.grazy.modules.file.enums.DelFlagEnum;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.modules.share.constants.ShareConstant;
import com.grazy.modules.share.context.*;
import com.grazy.modules.share.domain.GCloudShare;
import com.grazy.modules.share.domain.GCloudShareFile;
import com.grazy.modules.share.enums.ShareDayTypeEnum;
import com.grazy.modules.share.enums.ShareStatusEnum;
import com.grazy.modules.share.mapper.GCloudShareMapper;
import com.grazy.modules.share.po.ShareSimpleDetailVo;
import com.grazy.modules.share.service.GCloudShareFileService;
import com.grazy.modules.share.service.GCloudShareService;
import com.grazy.modules.share.vo.GCloudShareUrlListVo;
import com.grazy.modules.share.vo.GCloudShareUrlVo;
import com.grazy.modules.share.vo.ShareDetailVo;
import com.grazy.modules.share.vo.ShareUserInfoVo;
import com.grazy.modules.user.domain.GCloudUser;
import com.grazy.modules.user.service.GCloudUserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author gaofu
* @description 针对表【g_cloud_share(用户分享表)】的数据库操作Service实现
* @createDate 2024-01-24 17:43:16
*/
@Service
public class GCloudShareServiceImpl extends ServiceImpl<GCloudShareMapper, GCloudShare> implements GCloudShareService, ApplicationContextAware {

    @Resource
    private GCloudServerConfigProperties configProperties;

    @Resource
    private GCloudShareFileService gCloudShareFileService;

    @Resource
    private GCloudShareMapper gCloudShareMapper;

    @Resource
    private GCloudUserFileService gCloudUserFileService;

    @Resource
    private GCloudUserService gCloudUserService;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

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
        GCloudShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        doCheckShareCode(context);
        return generateShareToken(context);
    }


    /**
     * 获取分享详情
     * 1.校验分享链接的状态
     * 2.封装分享的主体信息
     * 3.封装分享文件信息列表
     * 4.封装分享文件的用户的信息
     *
     * @param context
     * @return
     */
    @Override
    public ShareDetailVo detail(QueryShareDetailContext context) {
        GCloudShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        ShareDetailVo shareDetailVo = new ShareDetailVo();
        context.setShareDetailVo(shareDetailVo);
        assembleShareInfo(context);
        assembleShareFileInfo(context);
        assembleShareUserInfo(context);
        return context.getShareDetailVo();
    }


    /**
     * 查询分享的简单详情
     * 1.校验分享是否有效
     * 2.封装分享信息
     * 3.封装分享用户的信息
     *
     * @param context
     * @return
     */
    @Override
    public ShareSimpleDetailVo simpleDetail(ShareSimpleDetailContext context) {
        GCloudShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        ShareSimpleDetailVo shareSimpleDetailVo = new ShareSimpleDetailVo();
        context.setShareSimpleDetailVo(shareSimpleDetailVo);
        assembleShareSimpleInfo(context);
        assembleUserSimpleInfo(context);
        return context.getShareSimpleDetailVo();
    }


    /**
     * 获取下一级文件列表
     *  1.校验分享链接是否有效
     *  2.校验所需文件ID是否为链接中的文件
     *  3.查询对应的子文件集合并返回
     *
     * @param context
     * @return
     */
    @Override
    public List<UserFileVO> fileList(QueryChildFileListContext context) {
        GCloudShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        //校验当前获取下一级文件的文件夹是否存在于分享链接中
        List<UserFileVO> allUserFileRecords = checkFileIdIsOnShareStatusAndGetAllShareUserFiles(context.getShareId(), Lists.newArrayList(context.getParentId()));
        //获取所需父类下的子类文件
        Map<Long, List<UserFileVO>> parentFileListMap = allUserFileRecords.stream().collect(Collectors.groupingBy(UserFileVO::getParentId));
        List<UserFileVO> userFileVOS = parentFileListMap.get(context.getParentId());
        if (CollectionUtils.isEmpty(userFileVOS)){
            return Lists.newArrayList();
        }
        return userFileVOS;
    }


    /**
     * 转存到我的网盘
     * 1.校验分享链接状态
     * 2.校验转存文件是否存在于分享链接中
     * 3.委托文件模块实现文件的转存
     *
     * @param context
     */
    @Override
    public void saveFiles(ShareSaveContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdIsOnShareStatus(context.getShareId(), context.getFileIdList());
        doSaveFileAsMe(context);
    }


    /**
     * 分享文件下载
     * 1.校验分享链接状态
     * 2.校验要下载的文件是否存在于链接中
     * 3.委托文件模块实现文件下载
     *
     * @param context
     */
    @Override
    public void download(ShareFileDownloadContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdIsOnShareStatus(context.getShareId(),Lists.newArrayList(context.getFileId()));
        doDownload(context);
    }


    /**
     * 刷新分享链接状态
     *  1.查询所有文件对应的分享链接id集合
     *  2.去判断每一个分享对应的文件以及其所有的父文件信息均为正常，此情况把分享状态修改为正常
     *  3.如果有分享的文件或者其父文件被删除，变更该分享的状态为有文件被删除
     *
     * @param allAvailableFileIdList
     */
    @Override
    public void refreshShareStatus(List<Long> allAvailableFileIdList) {
        List<Long> shareIdList = getShareIdListByFileIdList(allAvailableFileIdList);
        if(CollectionUtils.isEmpty(shareIdList)){
            return;
        }
        //一个链接可能对应多个文件
        HashSet<Long> shareIdSet = Sets.newHashSet(shareIdList);
        shareIdSet.stream().forEach(this::refreshOneShareStatus);
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
     * @param shareId
     */
    private GCloudShare checkShareStatus(Long shareId) {
        GCloudShare record = getById(shareId);
        if(Objects.isNull(record)){
            throw new GCloudBusinessException(ResponseCode.SHARE_CANCELLED);
        }
        if(Objects.equals(ShareStatusEnum.FILE_DELETED.getCode(),record.getShareStatus())){
            throw new GCloudBusinessException(ResponseCode.SHARE_FILE_MISS);
        }
        if(Objects.equals(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode(), record.getShareDayType())){
            //如果为永久有效,直接返回，不需要执行过期时间判断
            return record;
        }
        if(record.getShareEndTime().before(new Date())){
            throw new GCloudBusinessException(ResponseCode.SHARE_EXPIRE);
        }
        return record;
    }


    /**
     * 封装分享的主体信息
     *
     * @param context
     */
    private void assembleShareInfo(QueryShareDetailContext context) {
        ShareDetailVo shareDetailVo = context.getShareDetailVo();
        GCloudShare record = context.getRecord();

        shareDetailVo.setShareId(context.getShareId());
        shareDetailVo.setShareName(record.getShareName());
        shareDetailVo.setShareDay(record.getShareDay());
        shareDetailVo.setShareEndTime(record.getShareEndTime());
        shareDetailVo.setCreateTime(record.getCreateTime());
    }


    /**
     * 封装分享文件信息列表
     * 1.根据shareId查询fileId列表
     * 2.根据fileId查询用户文件信息列表集合
     * 3.封装vo中的文件详情列表信息
     *
     * @param context
     */
    private void assembleShareFileInfo(QueryShareDetailContext context) {
        List<Long> fileIdList = getShareFileIdList(context.getShareId());

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setFileIdList(fileIdList);
        queryFileListContext.setUserId(context.getRecord().getCreateUser());

        List<UserFileVO> fileList = gCloudUserFileService.getFileList(queryFileListContext);
        context.getShareDetailVo().setUserFileVOList(fileList);
    }


    /**
     * 根据分享ID查询对应的文件ID列表
     *
     * @param shareId
     * @return
     */
    private List<Long> getShareFileIdList(Long shareId) {
        LambdaQueryWrapper<GCloudShareFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(GCloudShareFile::getFileId);
        lambdaQueryWrapper.eq(GCloudShareFile::getShareId, shareId);
        return gCloudShareFileService.listObjs(lambdaQueryWrapper, value -> (Long) value);
    }


    /**
     * 封装分享文件的用户的信息
     *
     * @param context
     */
    private void assembleShareUserInfo(QueryShareDetailContext context) {
        Long createUser = context.getRecord().getCreateUser();
        GCloudUser dbCreateUser = gCloudUserService.getById(createUser);
        if(Objects.isNull(dbCreateUser)){
            throw new GCloudBusinessException("查询用户信息失败");
        }
        ShareUserInfoVo shareUserInfoVo = new ShareUserInfoVo();
        shareUserInfoVo.setUserId(dbCreateUser.getUserId());
        shareUserInfoVo.setUsername(encryptUsername(dbCreateUser.getUsername()));

        context.getShareDetailVo().setShareUserInfoVO(shareUserInfoVo);
    }


    /**
     * 用户名隐私加密
     *
     * @param username
     * @return
     */
    private String encryptUsername(String username) {
        StringBuffer encryptName = new StringBuffer(username)
                .replace(GCloudConstants.TWO_INT,username.length() - GCloudConstants.TWO_INT,
                        GCloudConstants.COMMON_ENCRYPT_STR);
        return encryptName.toString();
    }


    /**
     * 封装简单分享用户的信息
     *
     * @param context
     */
    private void assembleUserSimpleInfo(ShareSimpleDetailContext context) {
        GCloudShare record = context.getRecord();
        Long createUserId = record.getCreateUser();
        GCloudUser dbUser = gCloudUserService.getById(createUserId);
        if(Objects.isNull(dbUser)){
            throw new GCloudBusinessException("分享的用户不存在");
        }
        ShareUserInfoVo shareUserInfoVo = new ShareUserInfoVo();
        shareUserInfoVo.setUserId(dbUser.getUserId());
        shareUserInfoVo.setUsername(dbUser.getUsername());
        context.getShareSimpleDetailVo().setShareUserInfoVO(shareUserInfoVo);
    }


    /**
     * 封装简单分享信息
     *
     * @param context
     */
    private void assembleShareSimpleInfo(ShareSimpleDetailContext context) {
        GCloudShare record = context.getRecord();
        ShareSimpleDetailVo shareSimpleDetailVo = context.getShareSimpleDetailVo();
        shareSimpleDetailVo.setShareId(record.getShareId());
        shareSimpleDetailVo.setShareName(record.getShareName());
    }


    /**
     * 检验所需的文件是否属于分享链接里面的，并查询返回全部链接的文件
     * --查询下一集文件列表调用，校验的是当前文件夹的fileId是否存在于连接中
     * --若为其他操作，校验的是当前选择的fileId
     *
     * @param shareId
     * @param newArrayList
     * @return
     */
    private List<UserFileVO> checkFileIdIsOnShareStatusAndGetAllShareUserFiles(Long shareId, List<Long> newArrayList) {
        List<Long> fileIdList = getShareFileIdList(shareId);  //仅一级文件id
        if(CollectionUtils.isEmpty(fileIdList)){
            return Lists.newArrayList();
        }
        //返回连接中的全部文件
        List<GCloudUserFile> allFileRecords = gCloudUserFileService.findAllFileRecordsByFileIdList(fileIdList);
        if(CollectionUtils.isEmpty(allFileRecords)){
            return Lists.newArrayList();
        }
        List<Long> allFileIdList = allFileRecords
                .stream()
                .filter(Objects::nonNull)
                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .map(GCloudUserFile::getFileId)
                .collect(Collectors.toList());
        if(allFileIdList.containsAll(newArrayList)){
            return gCloudUserFileService.transferVOList(allFileRecords);
        }
        throw new GCloudBusinessException(ResponseCode.SHARE_FILE_MISS);
    }


    /**
     * 委托文件模块实现文件下载
     *
     * @param context
     */
    private void doDownload(ShareFileDownloadContext context) {
        FileDownloadContext fileDownloadContext = new FileDownloadContext();
        fileDownloadContext.setFileId(context.getFileId());
        fileDownloadContext.setResponse(context.getResponse());
        fileDownloadContext.setUserId(context.getUserId());
        gCloudUserFileService.downloadWithoutCheckUser(fileDownloadContext);
    }


    /**
     * 委托文件模块实现文件的转存
     *
     * @param context
     */
    private void doSaveFileAsMe(ShareSaveContext context) {
        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setFileIdList(context.getFileIdList());
        copyFileContext.setUserId(context.getUserId());
        copyFileContext.setTargetParentId(context.getTargetParentId());
        gCloudUserFileService.copy(copyFileContext);
    }


    /**
     * 校验转存文件是否存在于分享链接中
     *
     * @param shareId
     * @param fileIdList
     */
    private void checkFileIdIsOnShareStatus(Long shareId, List<Long> fileIdList) {
        checkFileIdIsOnShareStatusAndGetAllShareUserFiles(shareId,fileIdList);
    }


    /**
     * 刷新一个分享链接状态
     *  1.判断分享是否存在
     *  2.去判断每一个分享对应的文件以及其所有的父文件信息均为正常，此情况把分享状态修改为正常
     *  3.如果有分享的文件或者其父文件被删除，变更该分享的状态为有文件被删除
     *
     * @param shareId
     */
    private void refreshOneShareStatus(Long shareId) {
        GCloudShare record = getById(shareId);
        if(Objects.isNull(record)){
            return;
        }
        ShareStatusEnum shareStatus = ShareStatusEnum.NORMAL;
        if(!checkShareFileAvailable(shareId)){
            shareStatus = ShareStatusEnum.FILE_DELETED;
        }
        if(Objects.equals(record.getShareStatus(),shareStatus.getCode())){
            return;
        }
        doChangeShareStatus(record,shareStatus);
    }


    /**
     * 修改分享链接状态
     *
     * @param record
     * @param shareStatus
     */
    private void doChangeShareStatus(GCloudShare record, ShareStatusEnum shareStatus) {
        record.setShareStatus(shareStatus.getCode());
        if(!updateById(record)){
            applicationContext.publishEvent(new ErrorLogEvent(this,"更新分享状态失败，请手动更改状态，分享ID为：" + record.getShareId() + ", 分享" +
                    "状态改为：" + shareStatus.getCode(), GCloudConstants.ZERO_LONG));
        }
    }


    /**
     * 检查该分享所有的文件以及所有的父文件均为正常状态
     *
     * @param shareId
     * @return
     */
    private boolean checkShareFileAvailable(Long shareId) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        for(Long fileId: shareFileIdList){
            if(!checkUpFileAvailable(fileId)){
                return false;
            }
        }
        return true;
    }


    /**
     * 检查该文件以及所有的文件夹信息均为正常状态
     *
     * @param fileId
     * @return
     */
    private boolean checkUpFileAvailable(Long fileId) {
        GCloudUserFile record = gCloudUserFileService.getById(fileId);
        if(Objects.isNull(record)){
            return false;
        }
        if(Objects.equals(record.getDelFlag(),DelFlagEnum.YES.getCode())){
            return false;
        }
        if(Objects.equals(record.getParentId(), FileConstants.TOP_PARENT_ID)){
            return true;
        }
        return checkShareFileAvailable(record.getParentId());
    }


    /**
     * 通过文件ID查询对应的分享ID集合
     *
     * @param allAvailableFileIdList
     * @return
     */
    private List<Long> getShareIdListByFileIdList(List<Long> allAvailableFileIdList) {
        LambdaQueryWrapper<GCloudShareFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(GCloudShareFile::getShareId);
        lambdaQueryWrapper.in(GCloudShareFile::getFileId,allAvailableFileIdList);
        return gCloudShareFileService.listObjs(lambdaQueryWrapper, value -> (Long)value);
    }
}




