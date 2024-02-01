package com.grazy.modules.user.controller;

import com.grazy.core.response.R;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.user.context.UserRegisterContext;
import com.grazy.modules.user.converter.UserConverter;
import com.grazy.modules.user.po.UserRegisterPo;
import com.grazy.modules.user.service.GCloudUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: grazy
 * @Date: 2024-01-29 22:19
 * @Description: 用户模块控制层
 */

@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private GCloudUserService userService;

    @Resource
    private UserConverter userConverter;

    @ApiOperation(
            value = "用户注册接口",
            notes = "该接口提供功能为用户注册，实现了幂等性注册的逻辑，可以多并发调用",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("/register")
    public R<String> register(@RequestBody UserRegisterPo userRegisterPo){
        //实体类转换
        UserRegisterContext userRegisterContext = userConverter.UserRegisterPoTOUserRegisterContext(userRegisterPo);
        Long userId = userService.register(userRegisterContext);
        //返回加密后的用户id
        return R.data(IdUtil.encrypt(userId));
    }
}
