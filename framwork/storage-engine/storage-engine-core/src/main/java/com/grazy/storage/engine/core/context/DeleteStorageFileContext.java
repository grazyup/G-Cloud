package com.grazy.storage.engine.core.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-02 21:12
 * @Description: 删除物理文件 上下文参数对象
 */

@Data
public class DeleteStorageFileContext implements Serializable {

    private static final long serialVersionUID = -4950827140055052719L;

    /**
     * 文件存储路径集合
     */
    private List<String> FileRealPathList;
}
