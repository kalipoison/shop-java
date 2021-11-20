package com.gohb.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("发送阿里大于短信的实体类")
public class AliSmsModel {

    @ApiModelProperty("手机号")
    private String phoneNumbers;

    @ApiModelProperty("签名")
    private String signName;

    @ApiModelProperty("模板id")
    private String templateCode;

    @ApiModelProperty("参数")
    private String templateParam;

    @ApiModelProperty("短信扩展码")
    private String smsUpExtendCode;

    @ApiModelProperty("外部流水扩展字段")
    private String outId;

}
