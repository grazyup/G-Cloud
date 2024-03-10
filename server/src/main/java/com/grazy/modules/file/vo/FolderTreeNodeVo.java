package com.grazy.modules.file.vo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.grazy.web.serializable.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-10 15:49
 * @Description: 查询文件夹树 响应实体参数
 */

@ApiModel(value = "查询文件夹树响应参数")
@Data
public class FolderTreeNodeVo implements Serializable {

    private static final long serialVersionUID = 8920830070506159787L;

    @ApiModelProperty(value = "文件名称")
    private String label;

    @ApiModelProperty(value = "文件ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @ApiModelProperty(value = "父文件夹ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @ApiModelProperty(value = "子节点集合")
    private List<FolderTreeNodeVo> children;


    /**
     * 仅提供测试使用
     */
    public void print(){
        String jsonString = JSON.toJSONString(this);
        System.out.println(jsonString);
    }
}
