package com.grazy.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: grazy
 * @Date: 2024-02-02 2:35
 * @Description: 文件夹标注枚举类
 */

@Getter
@AllArgsConstructor
public enum FolderFlagEnum {

    /**
     *  不是文件夹
     */
    NO(0),

    /**
     * 文件夹
     */
    YES(1);

    private Integer code;
}
