package com.grazy.modules.share.controller;

import com.google.common.base.Splitter;
import com.grazy.common.annotation.LoginIgnore;
import com.grazy.common.annotation.NeedShareToken;
import com.grazy.common.utils.ShareIdUtil;
import com.grazy.common.utils.UserIdUtil;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.response.R;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.modules.share.context.*;
import com.grazy.modules.share.converter.ShareConverter;
import com.grazy.modules.share.po.*;
import com.grazy.modules.share.service.GCloudShareService;
import com.grazy.modules.share.vo.GCloudShareUrlListVo;
import com.grazy.modules.share.vo.GCloudShareUrlVo;
import com.grazy.modules.share.vo.ShareDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2024-03-24 20:36
 * @Description: 文件分享控制层
 */

@Api(tags = "分享模块")
@RestController
@Validated
public class ShareController {

    @Resource
    private GCloudShareService gCloudShareService;

    @Resource
    private ShareConverter shareConverter;


    @ApiOperation(
            value = "创建分享链接",
            notes = "该接口提供了创建分享链接的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("/share")
    public R<GCloudShareUrlVo> create(@Validated @RequestBody CreateShareUrlPo createShareUrlPo){
        CreateShareUrlContext context = shareConverter.createShareUrlPoToCreateShareUrlContext(createShareUrlPo);
        List<Long> shareFileIdList = Splitter.on(GCloudConstants.COMMON_SEPARATOR)
                .splitToList(createShareUrlPo.getShareFileIds())
                .stream()
                .map(IdUtil::decrypt)
                .collect(Collectors.toList());
        context.setShareFileIdList(shareFileIdList);
        GCloudShareUrlVo result = gCloudShareService.create(context);
        return R.data(result);
    }


    @ApiOperation(
            value = "查询分享链接列表",
            notes = "该接口提供了查询分享链接列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("/shares")
    public R<List<GCloudShareUrlListVo>> getShares(){
        QueryShareListContext context = new QueryShareListContext();
        context.setUserId(UserIdUtil.get());
        List<GCloudShareUrlListVo> result = gCloudShareService.getShares(context);
        return R.data(result);
    }


    @ApiOperation(
            value = "取消分享链接",
            notes = "该接口提供了取消分享链接的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @DeleteMapping("/share")
    public R<String> cancelShare(@Validated @RequestBody CancelSharePo cancelSharePo){
        CancelShareContext context = new CancelShareContext();
        context.setUserId(UserIdUtil.get());
        List<Long> shareIdList = Splitter.on(GCloudConstants.COMMON_SEPARATOR)
                .splitToList(cancelSharePo.getShareIds())
                .stream()
                .map(IdUtil::decrypt)
                .collect(Collectors.toList());
        context.setShareIdList(shareIdList);
        gCloudShareService.cancelShare(context);
        return R.success();
    }


    @ApiOperation(
            value = "校验分享提取码",
            notes = "该接口提供校验分享提取码的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @PostMapping("share/code/check")
    public R<String> checkShareCode(@Validated @RequestBody CheckShareCodePo checkShareCodePo){
        CheckShareCodeContext context = new CheckShareCodeContext();
        context.setShareId(IdUtil.decrypt(checkShareCodePo.getShareId()));
        context.setShareCode(checkShareCodePo.getShareCode());
        String token = gCloudShareService.checkShareCode(context);
        return R.data(token);
    }


    @ApiOperation(
            value = "获取分享详情",
            notes = "该接口提供获取分享详情的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @NeedShareToken
    @GetMapping("/share")
    public R<ShareDetailVo> detail(){
        QueryShareDetailContext context = new QueryShareDetailContext();
        context.setShareId(ShareIdUtil.get());
        ShareDetailVo result = gCloudShareService.detail(context);
        return R.data(result);
    }


    @ApiOperation(
            value = "查询分享的简单详情",
            notes = "该接口提供了查询分享的简单详情的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @GetMapping("/share/simple")
    public R<ShareSimpleDetailVo> simpleDetail(@NotBlank(message = "分享链接ID不能为空") @RequestParam(value = "shareId",required = false) String shareId){
        ShareSimpleDetailContext context = new ShareSimpleDetailContext();
        context.setShareId(IdUtil.decrypt(shareId));
        ShareSimpleDetailVo result = gCloudShareService.simpleDetail(context);
        return R.data(result);
    }


    @ApiOperation(
            value = "获取下一级文件列表",
            notes = "该接口提供了获取下一级文件列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @NeedShareToken
    @GetMapping("/share/file/list")
    public R<List<UserFileVO>> fileList(@NotBlank(message = "文件的父文件夹不能为空") @RequestParam(value = "parentId",required = false) String parentId){
        QueryChildFileListContext context = new QueryChildFileListContext();
        context.setParentId(IdUtil.decrypt(parentId));
        context.setShareId(ShareIdUtil.get());
        List<UserFileVO> result = gCloudShareService.fileList(context);
        return R.data(result);
    }


    @ApiOperation(
            value = "转存到我的网盘",
            notes = "该接口提供了转存到我的网盘的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @NeedShareToken
    @PostMapping("share/save")
    public R<String> saveFiles(@Validated @RequestBody ShareSavePo shareSavePo){
        ShareSaveContext context = new ShareSaveContext();
        context.setShareId(ShareIdUtil.get());
        context.setUserId(UserIdUtil.get());
        context.setTargetParentId(IdUtil.decrypt(shareSavePo.getTargetParentId()));
        String fileIds = shareSavePo.getFileIds();
        List<Long> fileIdList = Splitter.on(GCloudConstants.COMMON_SEPARATOR)
                .splitToList(fileIds)
                .stream()
                .map(IdUtil::decrypt)
                .collect(Collectors.toList());
        context.setFileIdList(fileIdList);
        gCloudShareService.saveFiles(context);
        return R.success();
    }


    @ApiOperation(
            value = "分享文件下载",
            notes = "该接口提供了分享文件下载的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("share/file/download")
    @NeedShareToken
    public void download(@NotBlank(message = "文件ID不能为空") @RequestParam(value = "fileId",required = false) String fileId,
                         HttpServletResponse response){
        ShareFileDownloadContext context = new ShareFileDownloadContext();
        context.setFileId(IdUtil.decrypt(fileId));
        context.setShareId(ShareIdUtil.get());
        context.setUserId(UserIdUtil.get());
        context.setResponse(response);
        gCloudShareService.download(context);
    }
}
