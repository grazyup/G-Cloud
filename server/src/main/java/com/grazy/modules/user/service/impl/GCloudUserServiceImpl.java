package com.grazy.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.grazy.constants.CacheConstants;
import com.grazy.core.constants.GCloudConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.response.ResponseCode;
import com.grazy.core.utils.IdUtil;
import com.grazy.core.utils.JwtUtil;
import com.grazy.core.utils.PasswordUtil;
import com.grazy.modules.file.constants.FileConstants;
import com.grazy.modules.file.context.CreateFolderContext;
import com.grazy.modules.file.service.GCloudUserFileService;
import com.grazy.modules.user.constants.UserConstant;
import com.grazy.modules.user.context.UserLoginContext;
import com.grazy.modules.user.context.UserRegisterContext;
import com.grazy.modules.user.converter.UserConverter;
import com.grazy.modules.user.domain.GCloudUser;
import com.grazy.modules.user.mapper.GCloudUserMapper;
import com.grazy.modules.user.service.GCloudUserService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
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
            cache.evict(UserConstant.USER_LOGIN_PREFIX +userId);
        }catch (Exception ex){
            ex.printStackTrace();
            throw new GCloudBusinessException("退出登录失败！");
        }
    }



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
}




