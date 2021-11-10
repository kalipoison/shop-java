package com.gohb.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("商品评论的详情对象")
public class ProdCommVo {

    @ApiModelProperty("商品的id")
    private Long prodId;

    @ApiModelProperty("商品的名称")
    private String prodName;

    @ApiModelProperty(value = "好评数")
    private Long praiseNumber;

    @ApiModelProperty(value = "好评率")
    private BigDecimal positiveRating;

    @ApiModelProperty(value = "总条数")
    private Integer number;

    @ApiModelProperty(value = "中评数")
    private Integer secondaryNumber;

    @ApiModelProperty(value = "差评数")
    private Integer negativeNumber;

    @ApiModelProperty(value = "有图")
    private Integer picNumber;

}
