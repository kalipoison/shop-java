package com.gohb.vo;

import com.gohb.domain.UserAddr;
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
@ApiModel("订单的确认对象")
public class OrderConfirmResult {

    @ApiModelProperty("用户的默认收货地址")
    private UserAddr userAddr;

    @ApiModelProperty("订单详情的集合")
    private List<OrderVo> shopCartOrders;

    @ApiModelProperty("用户的优惠券")
    private List<Long> coupList;

    @ApiModelProperty("实际的订单的总金额")
    private BigDecimal actualTotal;

    @ApiModelProperty("订单的总金额")
    private BigDecimal total;

    @ApiModelProperty("订单商品数量")
    private Integer totalCount;

    @ApiModelProperty("订单的总运费")
    private BigDecimal yunfei;


}
