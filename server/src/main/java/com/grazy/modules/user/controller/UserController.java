package com.grazy.modules.user.controller;

import com.grazy.common.annotation.LoginIgnore;
import com.grazy.common.utils.UserIdUtil;
import com.grazy.core.response.R;
import com.grazy.core.utils.IdUtil;
import com.grazy.modules.user.context.*;
import com.grazy.modules.user.converter.UserConverter;
import com.grazy.modules.user.po.*;
import com.grazy.modules.user.service.GCloudUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @LoginIgnore
    @PostMapping("/register")
    public R<String> register(@Validated @RequestBody UserRegisterPo userRegisterPo){
        //实体类转换
        UserRegisterContext userRegisterContext = userConverter.UserRegisterPoTOUserRegisterContext(userRegisterPo);
        Long userId = userService.register(userRegisterContext);
        //返回加密后的用户id
        return R.data(IdUtil.encrypt(userId));
    }


    @ApiOperation(
            value = "用户登录接口",
            notes = "该接口提供功能为用户登录，登录成功后返回一个具有时效性的登录凭证accessToken",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @PostMapping("/login")
    public R<String> login(@Validated @RequestBody UserLoginPo userLoginPo){
        UserLoginContext userLoginContext = userConverter.UserLoginPoToUserLoginContext(userLoginPo);
        String accessToken = userService.login(userLoginContext);
        return R.data(accessToken);
    }


    @ApiOperation(
            value = "用户登出",
            notes = "该接口提供功能为用户登出",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ApiImplicitParam(name = "Authorization", value = "Authorization",required = true, dataType = "String",paramType="header")
    @PostMapping("/exit")
    public R<String> exit(){
        //从ThreadLocal中获取用户ID
        userService.exit(UserIdUtil.get());
        return R.success("退出登录成功!");
    }


    @ApiOperation(
            value = "忘记密码-检验用户名",
            notes = "该接口提供了用户忘记密码-校验用户名的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @PostMapping("/username/check")
    public R<String> checkUsername(@Validated @RequestBody CheckUsernamePO checkUsernamePO){
        CheckUsernameContext checkUsernameContext = userConverter.CheckUsernamePOToCheckUsernameContext(checkUsernamePO);
        String question = userService.checkUsername(checkUsernameContext);
        return R.data(question);
    }


    @ApiOperation(
            value = "忘记密码-校验密保答案",
            notes = "该接口提供了用户忘记密码-校验密保答案的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @PostMapping("/answer/check")
    public R<String> checkAnswer(@Validated @RequestBody CheckAnswerPo checkAnswerPo){
        CheckAnswerContext checkAnswerContext = userConverter.CheckAnswerPoToCheckAnswerContext(checkAnswerPo);
        String userTemporarilyToken = userService.checkAnswer(checkAnswerContext);
        return R.data(userTemporarilyToken);
    }


    @ApiOperation(
            value = "忘记密码-重置密码",
            notes = "该接口提供了用户忘记密码-重置密码的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @PostMapping("/password/reset")
    public R<String> passwordReset(@Validated @RequestBody PasswordResetPo passwordResetPo){
        PasswordResetContext passwordResetContext = userConverter.PasswordResetPoToPasswordResetContext(passwordResetPo);
        userService.passwordReset(passwordResetContext);
        return R.success("密码修改成功!");
    }


    @ApiOperation(
            value = "在线修改密码",
            notes = "该接口提供了在线修改密码的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ApiImplicitParam(name = "Authorization", value = "Authorization",required = true, dataType = "String",paramType="header")
    @PostMapping("/password/onlineChange")
    public R<String> passwordOnlineChange(@Validated @RequestBody OnlineChangePasswordPo onlineChangePasswordPo){
        OnlineChangePasswordContext onlineChangePasswordContext = userConverter.OnlineChangePasswordPoToOnlineChangePasswordContext(onlineChangePasswordPo);
        onlineChangePasswordContext.setUserId(UserIdUtil.get());
        userService.passwordOnlineChange(onlineChangePasswordContext);
        return R.success("密码修改成功!");
    }
}
