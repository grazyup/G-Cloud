package com.grazy.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: grazy
 * @Date: 2024-03-03 20:49
 * @Description: 分片是否合并标识枚举
 */

@Getter
@AllArgsConstructor
public enum MergeFlagEnum {

    /**
     * 不需要合并
     */
    NO_READY(0),
    /**
     * 需要合并
     */
    READY(1);

    private Integer code;
}
