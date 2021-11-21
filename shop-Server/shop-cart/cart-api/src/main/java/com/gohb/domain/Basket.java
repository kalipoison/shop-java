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
* 购物车
*/
@ApiModel(value="com-gohb-domain-Basket")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "basket")
public class Basket implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "basket_id", type = IdType.AUTO)
    @ApiModelProperty(value="主键")
    private Long basketId;

    /**
     * 店铺ID
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value="店铺ID")
    private Long shopId;

    /**
     * 产品ID
     */
    @TableField(value = "prod_id")
    @ApiModelProperty(value="产品ID")
    private Long prodId;

    /**
     * SkuID
     */
    @TableField(value = "sku_id")
    @ApiModelProperty(value="SkuID")
    private Long skuId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户ID")
    private String userId;

    /**
     * 购物车产品个数
     */
    @TableField(value = "basket_count")
    @ApiModelProperty(value="购物车产品个数")
    private Integer basketCount;

    /**
     * 购物时间
     */
    @TableField(value = "basket_date")
    @ApiModelProperty(value="购物时间")
    private Date basketDate;

    /**
     * 满减活动ID
     */
    @TableField(value = "discount_id")
    @ApiModelProperty(value="满减活动ID")
    private Long discountId;

    /**
     * 分销推广人卡号
     */
    @TableField(value = "distribution_card_no")
    @ApiModelProperty(value="分销推广人卡号")
    private String distributionCardNo;

    private static final long serialVersionUID = 1L;

    public static final String COL_BASKET_ID = "basket_id";

    public static final String COL_SHOP_ID = "shop_id";

    public static final String COL_PROD_ID = "prod_id";

    public static final String COL_SKU_ID = "sku_id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_BASKET_COUNT = "basket_count";

    public static final String COL_BASKET_DATE = "basket_date";

    public static final String COL_DISCOUNT_ID = "discount_id";

    public static final String COL_DISTRIBUTION_CARD_NO = "distribution_card_no";
}