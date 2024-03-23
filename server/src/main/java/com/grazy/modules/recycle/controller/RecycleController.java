package com.grazy.modules.recycle.controller;

import com.google.common.base.Splitter;
import com.grazy.common.utils.UserIdUtil;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.response.R;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.modules.recycle.context.QueryRecycleFileListContext;
import com.grazy.modules.recycle.context.RestoreContext;
import com.grazy.modules.recycle.po.RestorePo;
import com.grazy.modules.recycle.service.GCloudRecycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2024-03-21 14:24
 * @Description: 回收站模块控制层
 */

@Api(tags = "回收站模块")
@RestController
@Validated
public class RecycleController {

    @Resource
    private GCloudRecycleService recycleService;


    @ApiOperation(
            value = "获取回收站文件列表",
            notes = "该接口提供获取回收站文件列表",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("/recycle")
    public R<List<UserFileVO>> recycle(){
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(UserIdUtil.get());
        List<UserFileVO> result = recycleService.recycle(queryRecycleFileListContext);
        return R.data(result);
    }


    @ApiOperation(
            value = "回收站批量还原文件",
            notes = "该接口提供回收站批量还原文件功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PutMapping("/recycle/restore")
    public R<String> restore(@Validated @RequestBody RestorePo restorePo){
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setUserId(UserIdUtil.get());
        List<Long> fileIds = Splitter.on(GCloudConstants.COMMON_SEPARATOR)
                .splitToList(restorePo.getFileIds())
                .stream()
                .map(IdUtil::decrypt)
                .collect(Collectors.toList());
        restoreContext.setFileIdList(fileIds);
        recycleService.restore(restoreContext);
        return R.success();
    }
}
