package com.grazy.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-03-01 20:55
 * @Description: 单文件上传 前端交互参数对象
 */

@ApiModel(value = "单文件上传参数")
@Data
public class FileUploadPo implements Serializable {

    private static final long serialVersionUID = 9063096145134180723L;

    @ApiModelProperty(value = "文件名称",required = true)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @ApiModelProperty(value = "文件的唯一标识",required = true)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;


    @ApiModelProperty(value = "文件总大小",required = true)
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @ApiModelProperty(value = "父文件夹ID",required = true)
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;


    @ApiModelProperty(value = "文件实体",required = true)
    @NotNull(message = "文件实体不能为空")
    private MultipartFile file;
}
