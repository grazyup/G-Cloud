package com.grazy.modules.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-06 12:04
 * @Description: 已上传的分片实体VO
 */

@ApiModel(value = "查询已上传的分片实体响应对象")
@Data
public class UploadChunksVo implements Serializable {

    private static final long serialVersionUID = -5695582403858883162L;

    @ApiModelProperty(value = "已上传的分片编号列表")
    private List<Integer> uploadedChunks;

}
