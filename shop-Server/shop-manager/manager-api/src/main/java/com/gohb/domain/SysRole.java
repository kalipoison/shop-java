package com.gohb.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色
 */
@ApiModel(value="com-gohb-domain-SysRole")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_role")
public class SysRole implements Serializable {
    @TableId(value = "role_id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long roleId;

    /**
     * 角色名称
     */
    @TableField(value = "role_name")
    @ApiModelProperty(value="角色名称")
    private String roleName;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(value="备注")
    private String remark;

    /**
     * 创建者ID
     */
    @TableField(value = "create_user_id")
    @ApiModelProperty(value="创建者ID")
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ROLE_ID = "role_id";

    public static final String COL_ROLE_NAME = "role_name";

    public static final String COL_REMARK = "remark";

    public static final String COL_CREATE_USER_ID = "create_user_id";

    public static final String COL_CREATE_TIME = "create_time";
}