package com.grazy.modules.share.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户分享文件表
 * @TableName g_cloud_share_file
 */
@TableName(value ="g_cloud_share_file")
@Data
public class GCloudShareFile implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分享id
     */
    @TableField(value = "share_id")
    private Long shareId;

    /**
     * 文件记录ID
     */
    @TableField(value = "file_id")
    private Long fileId;

    /**
     * 分享创建人
     */
    @TableField(value = "create_user")
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}