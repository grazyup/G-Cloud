package com.grazy.modules.recycle.context;

import com.grazy.modules.file.domain.GCloudUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-23 14:47
 * @Description: 文件还原上下文参数
 */

@Data
public class RestoreContext implements Serializable {

    private static final long serialVersionUID = 4500250632694727727L;

    /**
     * 要还原的文件id集合
     */
    private List<Long> fileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 用户文件实体
     */
    private List<GCloudUserFile> records;
}
