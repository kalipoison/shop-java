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


@ApiModel(value="com-gohb-domain-Area")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "area")
public class Area implements Serializable {
    @TableId(value = "area_id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long areaId;

    @TableField(value = "area_name")
    @ApiModelProperty(value="")
    private String areaName;

    @TableField(value = "parent_id")
    @ApiModelProperty(value="")
    private Long parentId;

    @TableField(value = "level")
    @ApiModelProperty(value="")
    private Integer level;

    private static final long serialVersionUID = 1L;

    public static final String COL_AREA_ID = "area_id";

    public static final String COL_AREA_NAME = "area_name";

    public static final String COL_PARENT_ID = "parent_id";

    public static final String COL_LEVEL = "level";
}