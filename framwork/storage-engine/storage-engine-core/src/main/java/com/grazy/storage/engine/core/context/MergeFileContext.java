package com.grazy.storage.engine.core.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-06 23:38
 * @Description: 合并分片文件 上下文参数
 */

@Data
public class MergeFileContext implements Serializable {

    private static final long serialVersionUID = 2665249029962526588L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 分片文件的存储路径集合
     */
    private List<String> realPathList;

    /**
     * 文件合并后的真实物理存储路径
     */
    private String realPath;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
