package com.grazy.modules.share.context;

import com.grazy.modules.share.domain.GCloudShare;
import com.grazy.modules.share.po.ShareSimpleDetailVo;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-27 12:17
 * @Description:
 */

@Data
public class ShareSimpleDetailContext implements Serializable {

    private static final long serialVersionUID = -2845582091658102027L;

    /**
     * 分享链接ID
     */
    private Long shareId;

    /**
     * 分享信息记录
     */
    private GCloudShare record;

    /**
     * 查询简单分享链接信息响应对象
     */
    private ShareSimpleDetailVo shareSimpleDetailVo;
}
