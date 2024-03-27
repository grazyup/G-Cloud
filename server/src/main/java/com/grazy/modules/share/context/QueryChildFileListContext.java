package com.grazy.modules.share.context;

import com.grazy.modules.share.domain.GCloudShare;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-27 20:04
 * @Description: 查询子文件列表上下文参数
 */

@Data
public class QueryChildFileListContext implements Serializable {

    private static final long serialVersionUID = -5097430554665947286L;

    /**
     * 父文件夹Id
     */
    private Long parentId;

    /**
     * 分享Id
     */
    private Long ShareId;

    /**
     * 分享对应的实体信息
     */
    private GCloudShare record;
}
