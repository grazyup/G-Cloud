package com.grazy.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.common.cache.AnnotationCacheService;
import com.grazy.constants.CacheConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.response.ResponseCode;
import com.grazy.core.utils.IdUtil;
import com.grazy.core.utils.JwtUtil;
import com.grazy.core.utils.PasswordUtil;
import com.grazy.modules.file.constants.FileConstants;
import com.grazy.modules.file.context.CreateFolderContext;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.user.constants.UserConstant;
import com.grazy.modules.user.context.*;
import com.grazy.modules.user.converter.UserConverter;
import com.grazy.modules.user.domain.GCloudUser;
import com.grazy.modules.user.mapper.GCloudUserMapper;
import com.grazy.modules.user.service.GCloudUserService;
import com.grazy.modules.user.vo.UserInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author gaofu
 * @description 针对表【g_cloud_user(用户信息表)】的数据库操作Service实现
 * @createDate 2024-01-24 17:35:44
 */

@Service(value = "userService")
public class GCloudUserServiceImpl extends ServiceImpl<GCloudUserMapper, GCloudUser> implements GCloudUserService {

    @Resource
    private UserConverter userConverter;

    @Resource
    private GCloudUserFileService gCloudUserFileService;

    @Resource
    private CacheManager cacheManager;

    @Resource
    private GCloudUserMapper userMapper;

    @Resource
    @Qualifier(value = "userAnnotationCacheService")   //因为使用接口形式注入，可能容器中存在多个该接口的实现类，需要我们手动指定
    private AnnotationCacheService<GCloudUser> cacheService;

    /**
     * 用户注册业务实现
     * 需要实现的功能点：
     * 1、注册用户信息
     * 2、注册新用户的根本目录信息
     * <p>
     * 需要实现的技术难点:
     * 1、该业务是幂等性的
     * 2、要保证用户名的全局唯一性
     * <p>
     * 实现技术难点的处理方案：
     * 1、幂等性通过数据库表对用户名字段添加唯一索引，我们上有业务捕捉对应的异常信息，转化返回
     *
     * @param userRegisterContext 注册对象上下文信息
     * @return 用户ID
     */
    @Override
    public Long register(UserRegisterContext userRegisterContext) {
        assembleUserEntity(userRegisterContext);
        doRegister(userRegisterContext);
        createUserRootFolder(userRegisterContext);
        return userRegisterContext.getEntity().getUserId();
    }


    /**
     * 用户登录业务实现
     * 需要实现的功能点:
     * 1、用户登录信息的校验
     * 2、生成一个具有时效性的登录凭证
     * 3、缓存登录凭证，实现简单的单机登录
     *
     * @param userLoginContext 用户登录信息
     * @return 登录凭证
     */
    @Override
    public String login(UserLoginContext userLoginContext) {
        checkLoginInfo(userLoginContext);
        generateAndSaveAccessToken(userLoginContext);
        return userLoginContext.getAccessToken();
    }


    /**
     * 用户登出业务实现
     * 1、清除缓存中的登录凭证
     * @param userId 用户id
     */
    @Override
    public void exit(Long userId) {
        try{
            //获取缓存管理器
            Cache cache = cacheManager.getCache(CacheConstants.G_CLOUD_CACHE_NAME);
            cache.evict(UserConstant.USER_LOGIN_PREFIX + userId);
        }catch (Exception ex){
            ex.printStackTrace();
            throw new GCloudBusinessException("退出登录失败！");
        }
    }


    /**
     * 忘记密码-检验用户名
     *
     * @param checkUsernameContext 用户名参数对象
     * @return 用户对应的密保问题
     */
    @Override
    public String checkUsername(CheckUsernameContext checkUsernameContext) {
        String question = userMapper.selectQuestionByUsername(checkUsernameContext.getUsername());
        if(StringUtils.isBlank(question)){
            throw new GCloudBusinessException("用户名不存在！");
        }
        return question;
    }


    /**
     * 忘记密码-校验密保答案
     *
     * @param checkAnswerContext 密保校验参数对象
     * @return 临时用户身份凭证
     */
    @Override
    public String checkAnswer(CheckAnswerContext checkAnswerContext) {
        LambdaQueryWrapper<GCloudUser> gCloudUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        gCloudUserLambdaQueryWrapper.eq(GCloudUser::getUsername,checkAnswerContext.getUsername())
                .eq(GCloudUser::getQuestion,checkAnswerContext.getQuestion())
                .eq(GCloudUser::getAnswer,checkAnswerContext.getAnswer());
        int count = count(gCloudUserLambdaQueryWrapper);
        if(count == 0){
            throw new GCloudBusinessException("密保答案错误！");
        }
        return generateCheckAnswerToken(checkAnswerContext);
    }


    /**
     * 忘记密码-重置密码
     *
     * @param passwordResetContext 重置密码参数对象
     */
    @Override
    public void passwordReset(PasswordResetContext passwordResetContext) {
        checkForgetPasswordToken(passwordResetContext);
        checkAndResetPassword(passwordResetContext);
    }


    /**
     * 在线修改密码
     *
     * @param onlineChangePasswordContext 在线修改密码参数对象
     */
    @Override
    public void passwordOnlineChange(OnlineChangePasswordContext onlineChangePasswordContext) {
        checkOldPassword(onlineChangePasswordContext);
        changePassword(onlineChangePasswordContext);
        //退出登录
        exit(onlineChangePasswordContext.getUserId());
    }


    /**
     * 获取用户基本信息
     *
     * @param userId 用户id
     * @return 用户信息实体Vo
     */
    @Override
    public UserInfoVo info(Long userId) {
        GCloudUser entity = getById(userId);
        if(Objects.isNull(entity)){
            throw new GCloudBusinessException("用户信息不存在！");
        }
        GCloudUserFile userFile = gCloudUserFileService.getFIleInfo(entity.getUserId());
        if(Objects.isNull(userFile)){
            throw new GCloudBusinessException("查询用户根文件夹信息失败");
        }
        return userConverter.assembleUserInfoVO(entity,userFile);
    }


    /********************************************** 重写mybatis-plus方法 **********************************************/

    /**
      重写的原因是，业务之前使用到Mybatis-Plus中封装的数据操作方法，避免更改麻烦，直接重新其中的方法，替换成带有缓存业务的代码逻辑实现方法
     */

    @Override
    public boolean removeById(Serializable id) {
        return cacheService.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        //该业务层使用的是注解形式的缓存实现的方式，为此不支持批量操作  --> (使用什么类型按自己业务需求注入即可)
        throw new GCloudBusinessException("请更换手动缓存");
    }

    @Override
    public boolean updateById(GCloudUser entity) {
        return cacheService.updateById(entity.getUserId(),entity);
    }

    @Override
    public boolean updateBatchById(Collection<GCloudUser> entityList) {
        throw new GCloudBusinessException("请更换手动缓存");
    }

    @Override
    public GCloudUser getById(Serializable id) {
        return cacheService.getById(id);
    }

    @Override
    public List<GCloudUser> listByIds(Collection<? extends Serializable> idList) {
        throw new GCloudBusinessException("请更换手动缓存");
    }


    /********************************************** private方法 **********************************************/

    /**
     * 创建注册用户的根目录
     *
     * @param userRegisterContext 注册对象上下文信息
     */
    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);
        createFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        gCloudUserFileService.createFolder(createFolderContext);
    }


    /**
     * 注册
     *
     * @param userRegisterContext 用户注册上下文参数
     */
    private void doRegister(UserRegisterContext userRegisterContext) {
        GCloudUser entity = userRegisterContext.getEntity();
        if (Objects.nonNull(entity)) {
            try {
                if (!save(entity)) {
                    //数据库保存失败
                    throw new GCloudBusinessException("用户注册失败！");
                }
            } catch (DuplicateKeyException duplicateKeyException) {
                //数据库用户字段唯一，存在则抛出DuplicateKeyException异常
                throw new GCloudBusinessException("用户名已存在");
            }
            return;
        }
        //entity对象为为空，业务出现错误
        throw new GCloudBusinessException(ResponseCode.ERROR);
    }


    /**
     * 实体类的组装
     * 向userRegisterContext中的entity属性对象赋值
     *
     * @param userRegisterContext 用户注册上下文参数
     */
    private void assembleUserEntity(UserRegisterContext userRegisterContext) {
        //将匹配的属性转换匹配到GCloudUser中
        GCloudUser gCloudUser = userConverter.UserRegisterContextToGCloudUser(userRegisterContext);
        //密码加密
        String salt = PasswordUtil.getSalt();
        String encryptPassword = PasswordUtil.encryptPassword(salt, userRegisterContext.getPassword());
        //雪花算法生成userId
        gCloudUser.setUserId(IdUtil.get());
        gCloudUser.setPassword(encryptPassword);
        gCloudUser.setSalt(salt);
        gCloudUser.setCreateTime(new Date());
        gCloudUser.setUpdateTime(new Date());
        userRegisterContext.setEntity(gCloudUser);
    }


    /**
     * 构建并保存登录凭证accessToke
     *
     * @param userLoginContext 用户登录对象上下文参数信息
     */
    private void generateAndSaveAccessToken(UserLoginContext userLoginContext) {
        GCloudUser entity = userLoginContext.getEntity();
        String accessToken = JwtUtil.generateToken(entity.getUsername(), UserConstant.LOGIN_USER_ID,
                entity.getUserId(), UserConstant.ONE_DAY_LONG);
        userLoginContext.setAccessToken(accessToken);

        //存储到Redis缓存中
        Cache managerCache = cacheManager.getCache(CacheConstants.G_CLOUD_CACHE_NAME);
        managerCache.put(UserConstant.USER_LOGIN_PREFIX + entity.getUserId(), accessToken);
    }


    /**
     * 检查用户登录信息
     *
     * @param userLoginContext 用户登录对象上下文参数信息
     */
    private void checkLoginInfo(UserLoginContext userLoginContext) {
        String username = userLoginContext.getUsername();
        String password = userLoginContext.getPassword();

        //根据用户名查询数据库中用户对象信息
        GCloudUser entity = selectUserInfo(username);
        if(Objects.isNull(entity)){
            throw new GCloudBusinessException("用户名不存在！");
        }
        //检查密码是否正确
        String salt = entity.getSalt();
        String encryptPassword = PasswordUtil.encryptPassword(salt, password);
        if(!Objects.equals(encryptPassword,entity.getPassword())){
            throw new GCloudBusinessException("密码错误！");
        }
        userLoginContext.setEntity(entity);
    }


    /**
     * 根据用户名查询数据库中用户对象信息
     * @param username 登录用户名
     * @return 用户对象实体
     */
    private GCloudUser selectUserInfo(String username) {
        LambdaQueryWrapper<GCloudUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GCloudUser::getUsername,username);
        return getOne(lambdaQueryWrapper);
    }


    /**
     * 生成 校验密保答案 通过后的临时身份凭证 -- 有效期为5分钟
     *
     * @param checkAnswerContext 密保参数对象
     * @return 临时身份凭证
     */
    private String generateCheckAnswerToken(CheckAnswerContext checkAnswerContext) {
        return JwtUtil.generateToken(checkAnswerContext.getUsername(), UserConstant.FORGET_USERNAME,
                checkAnswerContext.getUsername(), UserConstant.FIVE_MINUTES_LONG);
    }


    /**
     * 校验和重置密码
     * @param passwordResetContext 重置密码参数对象
     */
    private void checkAndResetPassword(PasswordResetContext passwordResetContext) {
        String username = passwordResetContext.getUsername();
        String password = passwordResetContext.getPassword();
        LambdaQueryWrapper<GCloudUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GCloudUser::getUsername,username);
        GCloudUser dbGCloudUser = getOne(lambdaQueryWrapper);
        if(Objects.isNull(dbGCloudUser)){
            throw new GCloudBusinessException("用户信息不存在！");
        }
        //处理新密码加密
        String encryptPassword = PasswordUtil.encryptPassword(dbGCloudUser.getSalt(), password);
        dbGCloudUser.setPassword(encryptPassword);
        dbGCloudUser.setUpdateTime(new Date());
        if(!updateById(dbGCloudUser)){
            throw new GCloudBusinessException("重置用户密码失败!");
        }
    }


    /**
     * 校验忘记密码的临时身份凭证
     * @param passwordResetContext 重置密码参数对象
     */
    private void checkForgetPasswordToken(PasswordResetContext passwordResetContext) {
        String token = passwordResetContext.getToken();
        Object username = JwtUtil.analyzeToken(token, UserConstant.FORGET_USERNAME);
        if(Objects.isNull(username)){
            throw new GCloudBusinessException("非法Token");
        }
        if(!Objects.equals(String.valueOf(username), passwordResetContext.getUsername())){
            throw new GCloudBusinessException("token令牌与用户身份不一致");
        }
    }


    /**
     * 执行修改在线修改密码逻辑
     *
     * @param onlineChangePasswordContext 在线修改密码参数对象
     */
    private void changePassword(OnlineChangePasswordContext onlineChangePasswordContext) {
        String newPassword = onlineChangePasswordContext.getNewPassword();
        GCloudUser entity = onlineChangePasswordContext.getEntity();
        String encryptPassword = PasswordUtil.encryptPassword(entity.getSalt(), newPassword);
        entity.setPassword(encryptPassword);
        entity.setUpdateTime(new Date());
        if(!updateById(entity)){
            throw new GCloudBusinessException("修改用户密码失败");
        }
    }


    /**
     * 校验旧密码
     *
     * @param onlineChangePasswordContext 在线修改密码参数对象
     */
    private void checkOldPassword(OnlineChangePasswordContext onlineChangePasswordContext) {
        GCloudUser entity = getById(onlineChangePasswordContext.getUserId());
        if (Objects.isNull(entity)) {
            throw new GCloudBusinessException("该用户信息不存在！");
        }
        String oldPassword = onlineChangePasswordContext.getOldPassword();
        String encryptPassword = PasswordUtil.encryptPassword(entity.getSalt(), oldPassword);
        if (!Objects.equals(encryptPassword, entity.getPassword())) {
            throw new GCloudBusinessException("旧密码不正确！");
        }
        onlineChangePasswordContext.setEntity(entity);
    }
}




