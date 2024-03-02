package com.grazy.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-02 21:09
 * @Description: 保存物理文件 上下文参数对象
 */

@Data
public class StoreFileContext implements Serializable {

    private static final long serialVersionUID = 6724876647853228977L;

    /**
     * 文件名称
     */
    private String filename;


    /**
     * 文件总大小
     */
    private Long totalSize;


    /**
     * 物理文件存储路径
     */
    private String realPath;


    /**
     * 文件的输入流信息
     */
    private InputStream inputStream;
}
