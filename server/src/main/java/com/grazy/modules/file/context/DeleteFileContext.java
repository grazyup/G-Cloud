package com.grazy.modules.file.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-02-29 9:36
 * @Description: 批量删除文件 上下文参数对象
 */

@Data
public class DeleteFileContext implements Serializable {

    private static final long serialVersionUID = -5683857492894659092L;

    /**
     * 需要删除的文件id集合
     */
    private List<Long> fileIdList;


    /**
     * 当前登录用户ID
     */
    private Long userId;
}
