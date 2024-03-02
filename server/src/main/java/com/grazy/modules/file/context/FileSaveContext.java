package com.grazy.modules.file.context;

import com.grazy.modules.file.domain.GCloudFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-01 21:21
 * @Description: 文件保存 上下文参数对象
 */

@Data
public class FileSaveContext implements Serializable {

    private static final long serialVersionUID = 6602419261319459599L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件大小
     */
    private Long totalSize;

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

    /**
     * 文件上传的物理路径
     */
    private String realPath;
}
