package com.grazy.modules.share.context;

import com.grazy.modules.share.domain.GCloudShare;
import com.grazy.modules.share.vo.ShareDetailVo;
import com.grazy.modules.user.domain.GCloudUser;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-26 10:06
 * @Description: 查询分享详情上下文参数
 */

@Data
public class QueryShareDetailContext implements Serializable {

    private static final long serialVersionUID = -7570522236439939364L;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 分享实体
     */
    private GCloudShare record;

    /**
     * 分享详情响应对象
     */
    private ShareDetailVo shareDetailVo;
}
