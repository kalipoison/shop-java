package com.gohb.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


/**
* 短信记录表
*/
@ApiModel(value="com-gohb-domain-SmsLog")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sms_log")
public class SmsLog implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="ID")
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户id")
    private String userId;

    /**
     * 手机号码
     */
    @TableField(value = "user_phone")
    @ApiModelProperty(value="手机号码")
    private String userPhone;

    /**
     * 短信内容
     */
    @TableField(value = "content")
    @ApiModelProperty(value="短信内容")
    private String content;

    /**
     * 手机验证码
     */
    @TableField(value = "mobile_code")
    @ApiModelProperty(value="手机验证码")
    private String mobileCode;

    /**
     * 短信类型  1:注册  2:验证
     */
    @TableField(value = "type")
    @ApiModelProperty(value="短信类型  1:注册  2:验证")
    private Integer type;

    /**
     * 发送时间
     */
    @TableField(value = "rec_date")
    @ApiModelProperty(value="发送时间")
    private Date recDate;

    /**
     * 发送短信返回码
     */
    @TableField(value = "response_code")
    @ApiModelProperty(value="发送短信返回码")
    private String responseCode;

    /**
     * 状态  1:有效  0：失效
     */
    @TableField(value = "status")
    @ApiModelProperty(value="状态  1:有效  0：失效")
    private Integer status;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_USER_PHONE = "user_phone";

    public static final String COL_CONTENT = "content";

    public static final String COL_MOBILE_CODE = "mobile_code";

    public static final String COL_TYPE = "type";

    public static final String COL_REC_DATE = "rec_date";

    public static final String COL_RESPONSE_CODE = "response_code";

    public static final String COL_STATUS = "status";
}