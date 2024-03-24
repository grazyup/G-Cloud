package com.grazy.modules.share.context;

import com.grazy.modules.share.domain.GCloudShare;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-24 20:54
 * @Description: 创建文件分享URL上下文参数
 */

@Data
public class CreateShareUrlContext implements Serializable {

    private static final long serialVersionUID = 6952122907446542593L;

    /**
     * 分享链接的名称
     */
    private String shareName;

    /**
     * 分享的类型 0-有提取码
     */
    private Integer shareType;

    /**
     * 分享的有效天数类型 （0 永久有效；1 7天有效；2 30天有效）
     */
    private Integer shareDayType;

    /**
     * 分享的文件ID集合
     */
    private List<Long> shareFileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 已经保存的分享实体信息
     */
    private GCloudShare record;
}
