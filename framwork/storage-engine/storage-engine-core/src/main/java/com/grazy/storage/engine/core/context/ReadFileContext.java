package com.grazy.storage.engine.core.context;

import lombok.Data;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-07 22:51
 * @Description: 读取文件内容 上下文参数
 */

@Data
public class ReadFileContext implements Serializable {

    private static final long serialVersionUID = 8062305556551385697L;


    /**
     * 文件输出流
     */
    private OutputStream outputStream;

    /**
     * 文件的存储路径
     */
    private String realPath;
}
