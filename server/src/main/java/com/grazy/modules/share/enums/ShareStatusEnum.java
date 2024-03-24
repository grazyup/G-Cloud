package com.grazy.modules.share.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: grazy
 * @Date: 2024-03-24 22:13
 * @Description: 文件分享状态枚举
 */

@AllArgsConstructor
@Getter
public enum ShareStatusEnum {

    NORMAL(0,"正常"),
    FILE_DELETED(1,"有文件被删除");

    private Integer code;

    private String desc;
}
