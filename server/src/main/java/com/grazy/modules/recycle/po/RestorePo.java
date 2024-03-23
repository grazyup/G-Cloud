package com.grazy.modules.recycle.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-23 14:42
 * @Description: 文件还原参数
 */

@ApiModel(value = "文件还原参数")
@Data
public class RestorePo implements Serializable {

    private static final long serialVersionUID = 4526483063339100184L;

    @ApiModelProperty(value = "要还原的文件ID集合，多个使用公用分割符分隔",required = true)
    @NotBlank(message = "请选择要还原的文件")
    private String fileIds;
}
