package com.grazy.modules.share.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-28 11:16
 * @Description: 分享文件下载上下文参数
 */
@Data
public class ShareFileDownloadContext implements Serializable {

    private static final long serialVersionUID = -4179754119578870568L;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 需要下载的文件ID
     */
    private Long fileId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 响应实体
     */
    private HttpServletResponse response;
}
