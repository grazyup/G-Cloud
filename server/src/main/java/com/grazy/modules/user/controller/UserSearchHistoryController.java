package com.grazy.modules.user.controller;

import com.grazy.common.utils.UserIdUtil;
import com.grazy.core.response.R;
import com.grazy.modules.user.context.QueryUserSearchHistoryContext;
import com.grazy.modules.user.service.GCloudUserSearchHistoryService;
import com.grazy.modules.user.vo.UserSearchHistoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "用户搜索历史模块")
public class UserSearchHistoryController {

    @Resource
    private GCloudUserSearchHistoryService gCloudUserSearchHistoryService;

    @ApiOperation(
            value = "获取用户最新的搜索历史记录，默认十条",
            notes = "该接口提供了获取用户最新的搜索历史记录的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("user/search/histories")
    public R<List<UserSearchHistoryVO>> getUserSearchHistories() {
        QueryUserSearchHistoryContext context = new QueryUserSearchHistoryContext();
        context.setUserId(UserIdUtil.get());
        List<UserSearchHistoryVO> result = gCloudUserSearchHistoryService.getUserSearchHistories(context);
        return R.data(result);
    }

}
