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
import java.math.BigDecimal;
import java.util.Date;


/**
* 订单项
*/
@ApiModel(value="com-gohb-domain-OrderItem")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "order_item")
public class OrderItem implements Serializable {
    /**
     * 订单项ID
     */
    @TableId(value = "order_item_id", type = IdType.AUTO)
    @ApiModelProperty(value="订单项ID")
    private Long orderItemId;

    /**
     * 店铺id
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value="店铺id")
    private Long shopId;

    /**
     * 订单order_number
     */
    @TableField(value = "order_number")
    @ApiModelProperty(value="订单order_number")
    private String orderNumber;

    /**
     * 产品ID
     */
    @TableField(value = "prod_id")
    @ApiModelProperty(value="产品ID")
    private Long prodId;

    /**
     * 产品SkuID
     */
    @TableField(value = "sku_id")
    @ApiModelProperty(value="产品SkuID")
    private Long skuId;

    /**
     * 购物车产品个数
     */
    @TableField(value = "prod_count")
    @ApiModelProperty(value="购物车产品个数")
    private Integer prodCount;

    /**
     * 产品名称
     */
    @TableField(value = "prod_name")
    @ApiModelProperty(value="产品名称")
    private String prodName;

    /**
     * sku名称
     */
    @TableField(value = "sku_name")
    @ApiModelProperty(value="sku名称")
    private String skuName;

    /**
     * 产品主图片路径
     */
    @TableField(value = "pic")
    @ApiModelProperty(value="产品主图片路径")
    private String pic;

    /**
     * 产品价格
     */
    @TableField(value = "price")
    @ApiModelProperty(value="产品价格")
    private BigDecimal price;

    /**
     * 用户Id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户Id")
    private String userId;

    /**
     * 商品总金额
     */
    @TableField(value = "product_total_amount")
    @ApiModelProperty(value="商品总金额")
    private BigDecimal productTotalAmount;

    /**
     * 购物时间
     */
    @TableField(value = "rec_time")
    @ApiModelProperty(value="购物时间")
    private Date recTime;

    /**
     * 评论状态： 0 未评价  1 已评价
     */
    @TableField(value = "comm_sts")
    @ApiModelProperty(value="评论状态： 0 未评价  1 已评价")
    private Integer commSts;

    /**
     * 推广员使用的推销卡号
     */
    @TableField(value = "distribution_card_no")
    @ApiModelProperty(value="推广员使用的推销卡号")
    private String distributionCardNo;

    /**
     * 加入购物车时间
     */
    @TableField(value = "basket_date")
    @ApiModelProperty(value="加入购物车时间")
    private Date basketDate;

    private static final long serialVersionUID = 1L;

    public static final String COL_ORDER_ITEM_ID = "order_item_id";

    public static final String COL_SHOP_ID = "shop_id";

    public static final String COL_ORDER_NUMBER = "order_number";

    public static final String COL_PROD_ID = "prod_id";

    public static final String COL_SKU_ID = "sku_id";

    public static final String COL_PROD_COUNT = "prod_count";

    public static final String COL_PROD_NAME = "prod_name";

    public static final String COL_SKU_NAME = "sku_name";

    public static final String COL_PIC = "pic";

    public static final String COL_PRICE = "price";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_PRODUCT_TOTAL_AMOUNT = "product_total_amount";

    public static final String COL_REC_TIME = "rec_time";

    public static final String COL_COMM_STS = "comm_sts";

    public static final String COL_DISTRIBUTION_CARD_NO = "distribution_card_no";

    public static final String COL_BASKET_DATE = "basket_date";
}