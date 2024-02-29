package com.grazy.core.utils;

import com.grazy.core.constants.GCloudConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

/**
 * @Author: grazy
 * @Date: 2024-02-29 21:06
 * @Description: 文件工具类
 */
public class FileUtils {

    /**
     * 根据文件名获取文件后缀
     */
    public static String getFileSuffix(String filename){
        if(Strings.isBlank(filename) || filename.indexOf(GCloudConstants.POINT_STR) == GCloudConstants.MINUS_ONE_STR){
            return StringUtils.EMPTY;
        }
        return filename.substring(filename.indexOf(GCloudConstants.POINT_STR) + 1);
    }
}
