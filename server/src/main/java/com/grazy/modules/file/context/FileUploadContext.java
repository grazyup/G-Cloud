package com.grazy.modules.file.context;

import com.grazy.modules.file.domain.GCloudFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-01 21:01
 * @Description: 单文件上传 上下文参数对象
 */

@Data
public class FileUploadContext implements Serializable {

    private static final long serialVersionUID = 7079926149309493410L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 要上传的文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 实体文件记录
     */
    private GCloudFile record;
}
