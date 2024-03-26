package com.grazy.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.grazy.modules.file.vo.UserFileVO;
import com.grazy.web.serializable.Date2StringSerializer;
import com.grazy.web.serializable.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2024-03-26 9:50
 * @Description: 查看分享详情实体响应参数
 */

@ApiModel("查看分享详情实体响应参数")
@Data
public class ShareDetailVo implements Serializable {

    private static final long serialVersionUID = -2886246099812051678L;

    @ApiModelProperty("分享链接的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty("分享链接的名称")
    private String shareName;

    @ApiModelProperty("分享量链接的创建时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date createTime;

    @ApiModelProperty("分享链接的有效天数")
    private Integer shareDay;

    @ApiModelProperty("分享链接的结束时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date shareEndTime;

    @ApiModelProperty("文件信息列表实体")
    private List<UserFileVO> userFileVOList;

    @ApiModelProperty("分享人的信息")
    private ShareUserInfoVo shareUserInfoVO;
}
