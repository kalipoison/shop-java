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

@ApiModel(value="com-gohb-domain-Transcity")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "transcity")
public class Transcity implements Serializable {
    @TableId(value = "transcity_id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long transcityId;

    /**
     * 运费项id
     */
    @TableField(value = "transfee_id")
    @ApiModelProperty(value="运费项id")
    private Long transfeeId;

    /**
     * 城市id
     */
    @TableField(value = "city_id")
    @ApiModelProperty(value="城市id")
    private Long cityId;

    private static final long serialVersionUID = 1L;

    public static final String COL_TRANSCITY_ID = "transcity_id";

    public static final String COL_TRANSFEE_ID = "transfee_id";

    public static final String COL_CITY_ID = "city_id";
}