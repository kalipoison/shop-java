package com.gohb.vo;

import com.gohb.domain.OrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("单个商家订单的详情")
public class OrderVo {

    @ApiModelProperty(value = "商品信息")
    private List<OrderItem> shopCartItemDiscounts;

    // 哪些组合可以用 满减和优惠 coupons
    @ApiModelProperty(value = "这些商品组合的运费")
    private BigDecimal transfee;

    @ApiModelProperty(value = "这些商品组合的商家满减")
    private BigDecimal shopReduce;


}
