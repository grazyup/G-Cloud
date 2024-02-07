package com.grazy.modules.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.grazy.web.serializable.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-02-07 16:37
 * @Description: 服务器返回前端实体类
 */

@ApiModel("用户基本信息实体")
@Data
public class UserInfoVo implements Serializable {

    private static final long serialVersionUID = 7994165984919807987L;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("根文件夹的加密Id")
    @JsonSerialize(using = IdEncryptSerializer.class) //序列化，避免Long类型的参数传递到前端出现精度丢失
    private Long rootFileId;

    @ApiModelProperty("根文件夹的名称")
    private String rootFilename;
}
