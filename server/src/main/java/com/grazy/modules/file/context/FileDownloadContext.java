package com.grazy.modules.file.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-07 21:47
 * @Description: 文件下载 上下文参数信息
 */

@Data
public class FileDownloadContext implements Serializable {

    private static final long serialVersionUID = -98578496535604393L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 请求响应对象
     */
    private HttpServletResponse response;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
