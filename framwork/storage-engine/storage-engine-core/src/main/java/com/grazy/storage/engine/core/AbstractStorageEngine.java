package com.grazy.storage.engine.core;

import cn.hutool.core.lang.Assert;
import com.grazy.constants.CacheConstants;
import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.storage.engine.core.context.DeleteStorageFileContext;
import com.grazy.storage.engine.core.context.StoreFileContext;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-03-01 14:48
 * @Description: 顶级文件存储引擎的公用父类
 */

public abstract class AbstractStorageEngine implements StorageEngine {

    @Resource
    private CacheManager cacheManager;


    /**
     * 公用获取缓存的方法
     *
     * @return
     */
    protected Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new GCloudBusinessException("the cache manager is empty!");
        }
        return cacheManager.getCache(CacheConstants.G_CLOUD_CACHE_NAME);
    }


    /**
     * 保存物理文件
     * 1.校验保存物理文件参数
     * 2.执行保存操作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void store(StoreFileContext context) throws IOException {
        checkStoreFileContext(context);
        doStore(context);
    }


    /**
     * 校验保存物理文件的参数
     *
     * @param context
     */
    private void checkStoreFileContext(StoreFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notNull(context.getTotalSize(), "文件总大小不能为空");
        Assert.notNull(context.getInputStream(), "文件不能为空");
    }


    /**
     * 执行保存物理文件
     * 下沉到具体的子类去实现功能
     *
     * @param context
     */
    protected abstract void doStore(StoreFileContext context) throws IOException;


    /**
     * 删除物理文件
     * 1.校验删除物理文件参数
     * 2.执行删除物理文件操作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void delete(DeleteStorageFileContext context) throws IOException {
        checkDeleteFileContext(context);
        doDelete(context);
    }


    /**
     * 校验删除物理文件参数
     *
     * @param context
     */
    private void checkDeleteFileContext(DeleteStorageFileContext context) {
        Assert.notEmpty(context.getFileRealPathList(),"要删除的文件存储路径列表不能为空");
    }


    /**
     * 执行删除物理文件
     * 下沉到具体的子类去实现
     *
     * @param context
     */
    protected abstract void doDelete(DeleteStorageFileContext context) throws IOException;
}
