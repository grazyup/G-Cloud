package com.grazy.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.grazy.modules.file.domain.GCloudUserFile;
import com.grazy.web.serializable.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author: grazy
 * @Date: 2024-03-13 14:32
 * @Description: 查询面包屑列表响应实体参数
 */

@ApiModel(value = "查询面包屑列表响应实体参数")
@Data
public class BreadCrumbsVo implements Serializable {

    private static final long serialVersionUID = -2286389672854751529L;

    @ApiModelProperty(value = "当前文件Id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @ApiModelProperty(value = "父文件夹Id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @ApiModelProperty(value = "文件夹名称")
    private String name;

    /**
     * 实体转换
     * @param record
     * @return
     */
    public static BreadCrumbsVo transfer(GCloudUserFile record){
        BreadCrumbsVo breadCrumbsVo = new BreadCrumbsVo();
        if(Objects.nonNull(record)){
            breadCrumbsVo.setId(record.getFileId());
            breadCrumbsVo.setParentId(record.getParentId());
            breadCrumbsVo.setName(record.getFilename());
        }
        return breadCrumbsVo;
    }
}
