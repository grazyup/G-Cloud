package com.grazy.modules.file.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-10 14:24
 * @Description: 文件预览 上下文参数对象
 */

@Data
public class FilePreviewContext implements Serializable {


    private static final long serialVersionUID = -6795877814626084486L;

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
