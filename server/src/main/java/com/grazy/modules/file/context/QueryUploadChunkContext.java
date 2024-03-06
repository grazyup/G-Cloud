package com.grazy.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-06 13:54
 * @Description: 查询已上传分片 上下文参数
 */

@Data
public class QueryUploadChunkContext implements Serializable {

    private static final long serialVersionUID = -2002410630802194037L;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
