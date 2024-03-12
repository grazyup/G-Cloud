package com.grazy.modules.file.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-12 17:18
 * @Description: 文件搜索 上下文参数
 */

@Data
public class FileSearchContext implements Serializable {

    private static final long serialVersionUID = 8829363703564026638L;

    /**
     * 搜索关键字
     */
    private String keyword;

    /**
     * 文件类型
     */
    private List<Integer> fileTypeList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
