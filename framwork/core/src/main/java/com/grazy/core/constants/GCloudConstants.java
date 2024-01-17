package com.grazy.core.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: grazy
 * @Date: 2024-01-18 0:39
 * @Description: G-Cloud云盘项目的公共基础常量类
 */

public interface GCloudConstants {

    /**
     * 公共的字符串分隔符
     */
    String COMMON_SEPARATOR = "__,__";

    /**
     * 空字符串
     */
    String EMPTY_STR = StringUtils.EMPTY;

    /**
     * 点 常量
     */
    String POINT_STR = ".";

    /**
     * 斜杠 常量
     */
    String SLASH_STR = "/";

    /**
     * Long 类型常量 0
     */
    Long ZERO_LONG = 0L;

    /**
     * Integer类型常量 0
     */
    Integer ZERO_INT = 0;

    /**
     * Integer类型常量 1
     */
    Integer ONE_INT = 1;

    /**
     * Integer类型常量 2
     */
    Integer TWO_INT = 2;

    /**
     * Integer类型常量 -1
     */
    Integer MINUS_ONE_STR = -1;

    /**
     * TRUE字符串常量
     */
    String TRUE_STR = "true";

    /**
     * FALSE字符串常量
     */
    String FALSE_STR = "false";

    /**
     * 组件扫描基础路径
     */
    String BASE_COMPONENT_SCAN_PATH = "com.grazy";

}
