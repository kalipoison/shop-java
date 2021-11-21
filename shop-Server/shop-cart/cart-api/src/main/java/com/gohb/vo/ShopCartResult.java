package com.gohb.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("购物车的总体返回对象")
public class ShopCartResult {

    @ApiModelProperty("条目的集合")
    private List<ShopCartItem> shopCartItems;

    // 满减 优惠券

}
