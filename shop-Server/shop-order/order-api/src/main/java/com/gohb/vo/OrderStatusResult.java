package com.gohb.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("订单状态对象")
public class OrderStatusResult {

    @ApiModelProperty("待付款")
    private Integer unPay;

    @ApiModelProperty("待发货")
    private Integer payed;

    @ApiModelProperty("待收货")
    private Integer consignment;

}