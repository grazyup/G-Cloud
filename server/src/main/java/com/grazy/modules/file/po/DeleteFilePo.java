package com.grazy.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-29 9:33
 * @Description: 批量删除文件 前端传参交互对象类
 */

@ApiModel(value = "批量删除文件参数")
@Data
public class DeleteFilePo implements Serializable {

    private static final long serialVersionUID = 551174177472312486L;

    @ApiModelProperty(value = "要删除的文件ID，多个使用公用的分隔符分割",required = true)
    @NotBlank(message = "请选择要删除的文件信息")
    private String fileIds;
}
