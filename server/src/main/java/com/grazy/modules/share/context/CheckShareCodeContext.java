package com.grazy.modules.share.context;

import com.grazy.modules.share.domain.GCloudShare;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-25 22:41
 * @Description: 校验分享提取码上下文参数
 */

@Data
public class CheckShareCodeContext implements Serializable {

    private static final long serialVersionUID = -5827763100239633021L;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 分享提取码
     */
    private String shareCode;

    /**
     * 对应的分享实体
     */
    private GCloudShare record;
}
