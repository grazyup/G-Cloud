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
 * @Date: 2024-03-03 18:14
 * @Description: 文件分片上传 前端交互参数对象
 */

@ApiModel(value = "文件分片上传参数")
@Data
public class FileChunkUploadPo implements Serializable {

    private static final long serialVersionUID = 4709182236690286481L;

    @ApiModelProperty(value = "文件名称",required = true)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @ApiModelProperty(value = "文件唯一标识",required = true)
    @NotBlank(message = "文件名称不能为空")
    private String identifier;

    @ApiModelProperty(value = "文件总大小",required = true)
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @ApiModelProperty(value = "总体分片数",required = true)
    @NotNull(message = "分片总数不能为空")
    private Integer totalChunks;

    @ApiModelProperty(value = "当前分片文件下标",required = true)
    @NotNull(message = "当前分片文件下标不能为空")
    private Integer chunkNumber;

    @ApiModelProperty(value = "当前分片文件大小",required = true)
    @NotNull(message = "当前分片文件大小不能为空")
    private Long currentChunkSize;

    @ApiModelProperty(value = "分片文件实体",required = true)
    @NotNull(message = "分片文件实体不能为空")
    private MultipartFile file;


}
