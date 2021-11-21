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

@ApiModel(value="com-gohb-domain-OrderRefund")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "order_refund")
public class OrderRefund implements Serializable {
    /**
     * 记录ID
     */
    @TableId(value = "refund_id", type = IdType.AUTO)
    @ApiModelProperty(value="记录ID")
    private Long refundId;

    /**
     * 店铺ID
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value="店铺ID")
    private Long shopId;

    /**
     * 订单ID
     */
    @TableField(value = "order_id")
    @ApiModelProperty(value="订单ID")
    private Long orderId;

    /**
     * 订单流水号
     */
    @TableField(value = "order_number")
    @ApiModelProperty(value="订单流水号")
    private String orderNumber;

    /**
     * 订单总金额
     */
    @TableField(value = "order_amount")
    @ApiModelProperty(value="订单总金额")
    private Double orderAmount;

    /**
     * 订单项ID 全部退款是0
     */
    @TableField(value = "order_item_id")
    @ApiModelProperty(value="订单项ID 全部退款是0")
    private Long orderItemId;

    /**
     * 退款编号
     */
    @TableField(value = "refund_sn")
    @ApiModelProperty(value="退款编号")
    private String refundSn;

    /**
     * 订单支付流水号
     */
    @TableField(value = "flow_trade_no")
    @ApiModelProperty(value="订单支付流水号")
    private String flowTradeNo;

    /**
     * 第三方退款单号(微信退款单号)
     */
    @TableField(value = "out_refund_no")
    @ApiModelProperty(value="第三方退款单号(微信退款单号)")
    private String outRefundNo;

    /**
     * 订单支付方式 1 微信支付 2 支付宝
     */
    @TableField(value = "pay_type")
    @ApiModelProperty(value="订单支付方式 1 微信支付 2 支付宝")
    private Integer payType;

    /**
     * 订单支付名称
     */
    @TableField(value = "pay_type_name")
    @ApiModelProperty(value="订单支付名称")
    private String payTypeName;

    /**
     * 买家ID
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="买家ID")
    private String userId;

    /**
     * 退货数量
     */
    @TableField(value = "goods_num")
    @ApiModelProperty(value="退货数量")
    private Integer goodsNum;

    /**
     * 退款金额
     */
    @TableField(value = "refund_amount")
    @ApiModelProperty(value="退款金额")
    private BigDecimal refundAmount;

    /**
     * 申请类型:1,仅退款,2退款退货
     */
    @TableField(value = "apply_type")
    @ApiModelProperty(value="申请类型:1,仅退款,2退款退货")
    private Integer applyType;

    /**
     * 处理状态:1为待审核,2为同意,3为不同意
     */
    @TableField(value = "refund_sts")
    @ApiModelProperty(value="处理状态:1为待审核,2为同意,3为不同意")
    private Integer refundSts;

    /**
     * 处理退款状态: 0:退款处理中 1:退款成功 -1:退款失败
     */
    @TableField(value = "return_money_sts")
    @ApiModelProperty(value="处理退款状态: 0:退款处理中 1:退款成功 -1:退款失败")
    private Integer returnMoneySts;

    /**
     * 申请时间
     */
    @TableField(value = "apply_time")
    @ApiModelProperty(value="申请时间")
    private Date applyTime;

    /**
     * 卖家处理时间
     */
    @TableField(value = "handel_time")
    @ApiModelProperty(value="卖家处理时间")
    private Date handelTime;

    /**
     * 退款时间
     */
    @TableField(value = "refund_time")
    @ApiModelProperty(value="退款时间")
    private Date refundTime;

    /**
     * 文件凭证json
     */
    @TableField(value = "photo_files")
    @ApiModelProperty(value="文件凭证json")
    private String photoFiles;

    /**
     * 申请原因
     */
    @TableField(value = "buyer_msg")
    @ApiModelProperty(value="申请原因")
    private String buyerMsg;

    /**
     * 卖家备注
     */
    @TableField(value = "seller_msg")
    @ApiModelProperty(value="卖家备注")
    private String sellerMsg;

    /**
     * 物流公司名称
     */
    @TableField(value = "express_name")
    @ApiModelProperty(value="物流公司名称")
    private String expressName;

    /**
     * 物流单号
     */
    @TableField(value = "express_no")
    @ApiModelProperty(value="物流单号")
    private String expressNo;

    /**
     * 发货时间
     */
    @TableField(value = "ship_time")
    @ApiModelProperty(value="发货时间")
    private Date shipTime;

    /**
     * 收货时间
     */
    @TableField(value = "receive_time")
    @ApiModelProperty(value="收货时间")
    private Date receiveTime;

    /**
     * 收货备注
     */
    @TableField(value = "receive_message")
    @ApiModelProperty(value="收货备注")
    private String receiveMessage;

    private static final long serialVersionUID = 1L;

    public static final String COL_REFUND_ID = "refund_id";

    public static final String COL_SHOP_ID = "shop_id";

    public static final String COL_ORDER_ID = "order_id";

    public static final String COL_ORDER_NUMBER = "order_number";

    public static final String COL_ORDER_AMOUNT = "order_amount";

    public static final String COL_ORDER_ITEM_ID = "order_item_id";

    public static final String COL_REFUND_SN = "refund_sn";

    public static final String COL_FLOW_TRADE_NO = "flow_trade_no";

    public static final String COL_OUT_REFUND_NO = "out_refund_no";

    public static final String COL_PAY_TYPE = "pay_type";

    public static final String COL_PAY_TYPE_NAME = "pay_type_name";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_GOODS_NUM = "goods_num";

    public static final String COL_REFUND_AMOUNT = "refund_amount";

    public static final String COL_APPLY_TYPE = "apply_type";

    public static final String COL_REFUND_STS = "refund_sts";

    public static final String COL_RETURN_MONEY_STS = "return_money_sts";

    public static final String COL_APPLY_TIME = "apply_time";

    public static final String COL_HANDEL_TIME = "handel_time";

    public static final String COL_REFUND_TIME = "refund_time";

    public static final String COL_PHOTO_FILES = "photo_files";

    public static final String COL_BUYER_MSG = "buyer_msg";

    public static final String COL_SELLER_MSG = "seller_msg";

    public static final String COL_EXPRESS_NAME = "express_name";

    public static final String COL_EXPRESS_NO = "express_no";

    public static final String COL_SHIP_TIME = "ship_time";

    public static final String COL_RECEIVE_TIME = "receive_time";

    public static final String COL_RECEIVE_MESSAGE = "receive_message";
}