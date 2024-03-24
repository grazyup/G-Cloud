package com.grazy.modules.share.controller;

import com.google.common.base.Splitter;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.response.R;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.share.context.CreateShareUrlContext;
import com.grazy.modules.share.converter.ShareConverter;
import com.grazy.modules.share.po.CreateShareUrlPo;
import com.grazy.modules.share.service.GCloudShareService;
import com.grazy.modules.share.vo.GCloudShareUrlVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

}
