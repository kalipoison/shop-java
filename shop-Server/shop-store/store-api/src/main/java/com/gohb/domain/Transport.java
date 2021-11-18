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
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ApiModel(value="com-gohb-domain-Transport")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "transport")
public class Transport implements Serializable {
    /**
     * 运费模板id
     */
    @TableId(value = "transport_id", type = IdType.AUTO)
    @ApiModelProperty(value="运费模板id")
    private Long transportId;

    /**
     * 运费模板名称
     */
    @TableField(value = "trans_name")
    @ApiModelProperty(value="运费模板名称")
    private String transName;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
     * 店铺id
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value="店铺id")
    private Long shopId;

    /**
     * 收费方式（0 按件数,1 按重量 2 按体积）
     */
    @TableField(value = "charge_type")
    @ApiModelProperty(value="收费方式（0 按件数,1 按重量 2 按体积）")
    private Byte chargeType;

    /**
     * 是否包邮 0:不包邮 1:包邮
     */
    @TableField(value = "is_free_fee")
    @ApiModelProperty(value="是否包邮 0:不包邮 1:包邮")
    private Byte isFreeFee;

    /**
     * 是否含有包邮条件 0 否 1是
     */
    @TableField(value = "has_free_condition")
    @ApiModelProperty(value="是否含有包邮条件 0 否 1是")
    private Byte hasFreeCondition;

    @TableField(exist = false)
    @ApiModelProperty("所有的收费模板")
    private List<Transfee> transfees= Collections.emptyList();
    @TableField(exist = false)
    @ApiModelProperty("所有免费的模板")
    private List<TransfeeFree> transfeeFrees= Collections.emptyList();

    private static final long serialVersionUID = 1L;

    public static final String COL_TRANSPORT_ID = "transport_id";

    public static final String COL_TRANS_NAME = "trans_name";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_SHOP_ID = "shop_id";

    public static final String COL_CHARGE_TYPE = "charge_type";

    public static final String COL_IS_FREE_FEE = "is_free_fee";

    public static final String COL_HAS_FREE_CONDITION = "has_free_condition";
}