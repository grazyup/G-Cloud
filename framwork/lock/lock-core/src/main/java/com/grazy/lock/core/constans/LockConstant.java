package com.grazy.lock.core.constans;

/**
 * @Author: grazy
 * @Date: 2024-04-09 9:47
 * @Description: 锁相关的公用常量类
 */

public interface LockConstant {

    /**
     * 公用Lock的名称
     */
    String G_CLOUD_LOCK = "g-cloud-lock;";

    /**
     * 公用lock的path
     * 主要针对zk等节点型软件
     */
    String G_CLOUD_LOCK_PATH = "/g-cloud-lock";

}
