package com.grazy.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: grazy
 * @Date: 2024-02-02 2:57
 * @Description: 文件删除标注枚举类
 */

@AllArgsConstructor
@Getter
public enum DelFlagEnum {

    /**
     * 未删除
     */
    NO(0),

    /**
     * 已删除
     */
    YES(1);

    private Integer code;
}
