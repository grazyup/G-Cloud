package com.grazy.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-06 13:51
 * @Description: 查询已上传分片信息 前端参数对象
 */

@ApiModel(value = "查询已上传分片信息参数")
@Data
public class QueryUploadChunkPo implements Serializable {

    private static final long serialVersionUID = 5371585928922098286L;

    @ApiModelProperty(value = "文件的唯一标识",required = true)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;
}
