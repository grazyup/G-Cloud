package com.grazy.modules.user.converter;

import com.grazy.modules.user.context.*;
import com.grazy.modules.user.domain.GCloudUser;
import com.grazy.modules.user.po.*;
import org.mapstruct.Mapper;

/**
 * @Author: grazy
 * @Date: 2024-01-31 3:54
 * @Description: 用户参数对象类型转换
 */

@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * 控制层用户注册类 转换为 业务层用户注册类
     *
     * @param userRegisterPo 控制层用户注册类
     * @return 业务层用户注册类
     */
    UserRegisterContext UserRegisterPoTOUserRegisterContext(UserRegisterPo userRegisterPo);

    /**
     * 对象注册上下文实体类转换为用户类
     *
     * @param userRegisterContext 上下文实体类
     * @return 用户类
     */
    GCloudUser UserRegisterContextToGCloudUser(UserRegisterContext userRegisterContext);

    /**
     * 控制层用户登录类 转为 业务层用户登录类
     *
     * @param userLoginPo 控制层用户登录类
     * @return 业务层用户注册类
     */
    UserLoginContext UserLoginPoToUserLoginContext(UserLoginPo userLoginPo);

    /**
     * 控制层忘记密码-校验用户名参数类 转为 业务层忘记密码-校验用户名参数类
     *
     * @param checkUsernamePO 控制层忘记密码-校验用户名参数类
     * @return 业务层忘记密码-校验用户名参数类
     */
    CheckUsernameContext CheckUsernamePOToCheckUsernameContext(CheckUsernamePO checkUsernamePO);

    /**
     * 控制层忘记密码-校验密保答案参数类 转为 业务层忘记密码-校验密保答案参数类
     *
     * @param checkAnswerPo 控制层忘记密码-校验密保答案参数类
     * @return 业务层忘记密码-校验密保答案参数类
     */
    CheckAnswerContext CheckAnswerPoToCheckAnswerContext(CheckAnswerPo checkAnswerPo);

    /**
     * 控制层忘记密码-重置密码参数类 转为 业务层忘记密码-重置密码参数类
     *
     * @param passwordResetPo 控制层忘记密码-重置密码参数类
     * @return 业务层忘记密码-重置密码参数类
     */
    PasswordResetContext PasswordResetPoToPasswordResetContext(PasswordResetPo passwordResetPo);

}