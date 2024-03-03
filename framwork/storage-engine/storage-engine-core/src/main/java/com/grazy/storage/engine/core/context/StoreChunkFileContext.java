package com.grazy.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-03 21:23
 * @Description: 保存物理分片文件 上下文参数对象
 */

@Data
public class StoreChunkFileContext implements Serializable {

    private static final long serialVersionUID = -1924550914832248065L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 当前分片大小
     */
    private Long currentChunkSize;

    /**
     * 分片总大小
     */
    private Integer totalChunks;

    /**
     * 当前分片下标
     */
    private Integer chunkNumber;

    /**
     * 当前登录用户ID
     */
    private Long userId;

    /**
     * 分片文件存储路径
     */
    private String realPath;

    /**
     * 文件输入流
     */
    private InputStream inputStream;
}
