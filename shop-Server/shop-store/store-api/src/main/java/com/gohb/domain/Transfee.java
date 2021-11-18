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
import java.util.Collections;
import java.util.List;


@ApiModel(value="com-gohb-domain-Transfee")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "transfee")
public class Transfee implements Serializable {
    /**
     * 运费项id
     */
    @TableId(value = "transfee_id", type = IdType.AUTO)
    @ApiModelProperty(value="运费项id")
    private Long transfeeId;

    /**
     * 运费模板id
     */
    @TableField(value = "transport_id")
    @ApiModelProperty(value="运费模板id")
    private Long transportId;

    /**
     * 续件数量
     */
    @TableField(value = "continuous_piece")
    @ApiModelProperty(value="续件数量")
    private BigDecimal continuousPiece;

    /**
     * 首件数量
     */
    @TableField(value = "first_piece")
    @ApiModelProperty(value="首件数量")
    private BigDecimal firstPiece;

    /**
     * 续件费用
     */
    @TableField(value = "continuous_fee")
    @ApiModelProperty(value="续件费用")
    private BigDecimal continuousFee;

    /**
     * 首件费用
     */
    @TableField(value = "first_fee")
    @ApiModelProperty(value="首件费用")
    private BigDecimal firstFee;

    @TableField(exist = false)
    @ApiModelProperty("收费的城市的中间表")
    private List<Area> cityList= Collections.emptyList();

    private static final long serialVersionUID = 1L;

    public static final String COL_TRANSFEE_ID = "transfee_id";

    public static final String COL_TRANSPORT_ID = "transport_id";

    public static final String COL_CONTINUOUS_PIECE = "continuous_piece";

    public static final String COL_FIRST_PIECE = "first_piece";

    public static final String COL_CONTINUOUS_FEE = "continuous_fee";

    public static final String COL_FIRST_FEE = "first_fee";
}