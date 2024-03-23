package com.grazy.modules.recycle.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-24 0:54
 * @Description: 删除回收站文件参数
 */

@ApiModel(value = "删除回收站文件参数")
@Data
public class DeletePo implements Serializable {

    private static final long serialVersionUID = -994949903632897122L;

    @ApiModelProperty(value = "要删除的文件ID集合，多个使用公用分割符分隔",required = true)
    @NotBlank(message = "请选择要删除的文件")
    private String fileIds;
}
