package com.grazy.modules.file.context;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-03 20:19
 * @Description: 文件分片上传 上下文参数对象
 */

@Data
public class FileChunkUploadContext implements Serializable {

    private static final long serialVersionUID = 4505344761197567156L;

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
     * 当前分片的下标 -- 从 1 开始
     */
    private Integer chunkNumber;

    /**
     * 分片文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录用户ID
     */
    private Long userId;

}
