package com.gohb.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("前台标签的vo")
public class ProdTagVo {

    @ApiModelProperty(value = "分组标签id")
    private Long id;

    @ApiModelProperty(value = "分组标题")
    private String title;

    @ApiModelProperty(value = "列表样式(0:一列一个,1:一列两个,2:一列三个)")
    private Integer style;

}

