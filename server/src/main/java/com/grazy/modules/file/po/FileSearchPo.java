package com.grazy.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-12 17:13
 * @Description: 文件搜索 前端交互参数
 */

@ApiModel(value = "文件搜索参数")
@Data
public class FileSearchPo implements Serializable {

    private static final long serialVersionUID = 412057500995310727L;

    @ApiModelProperty(value = "搜索的关键字", required = true)
    @NotBlank(message = "搜索关键字不能为空")
    private String keyword;

    @ApiModelProperty(value = "文件类型，多个文件类型使用公用分隔符拼接")
    private String fileTypes;
}
