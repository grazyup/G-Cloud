package com.grazy.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.grazy.web.serializable.Date2StringSerializer;
import com.grazy.web.serializable.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2024-02-18 9:55
 * @Description: 服务器返回前端 文件实体类
 */

@ApiModel(value = "用户搜索文件列表相应实体")
@Data
public class UserFileVO implements Serializable {

    private static final long serialVersionUID = 1585400761787981051L;

    @ApiModelProperty("文件的加密ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long fileId;

    @ApiModelProperty("父文件夹的加密ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @ApiModelProperty("文件名称")
    private String filename;

    @ApiModelProperty("文件大小描述")
    private String fileSizeDesc;

    @ApiModelProperty("文件夹标识 0 否 1 是")
    private Integer folderFlag;

    @ApiModelProperty("文件类型 1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv")
    private String fileType;

    @ApiModelProperty("更新时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date updateTime;
}
