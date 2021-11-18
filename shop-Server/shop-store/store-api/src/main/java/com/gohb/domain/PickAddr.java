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

/**
* 用户配送地址
*/
@ApiModel(value="com-gohb-domain-PickAddr")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "pick_addr")
public class PickAddr implements Serializable {
    /**
     * ID
     */
    @TableId(value = "addr_id", type = IdType.AUTO)
    @ApiModelProperty(value="ID")
    private Long addrId;

    /**
     * 自提点名称
     */
    @TableField(value = "addr_name")
    @ApiModelProperty(value="自提点名称")
    private String addrName;

    /**
     * 地址
     */
    @TableField(value = "addr")
    @ApiModelProperty(value="地址")
    private String addr;

    /**
     * 手机
     */
    @TableField(value = "mobile")
    @ApiModelProperty(value="手机")
    private String mobile;

    /**
     * 省份ID
     */
    @TableField(value = "province_id")
    @ApiModelProperty(value="省份ID")
    private Long provinceId;

    /**
     * 省份
     */
    @TableField(value = "province")
    @ApiModelProperty(value="省份")
    private String province;

    /**
     * 城市ID
     */
    @TableField(value = "city_id")
    @ApiModelProperty(value="城市ID")
    private Long cityId;

    /**
     * 城市
     */
    @TableField(value = "city")
    @ApiModelProperty(value="城市")
    private String city;

    /**
     * 区/县ID
     */
    @TableField(value = "area_id")
    @ApiModelProperty(value="区/县ID")
    private Long areaId;

    /**
     * 区/县
     */
    @TableField(value = "area")
    @ApiModelProperty(value="区/县")
    private String area;

    /**
     * 店铺id
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value="店铺id")
    private Long shopId;

    private static final long serialVersionUID = 1L;

    public static final String COL_ADDR_ID = "addr_id";

    public static final String COL_ADDR_NAME = "addr_name";

    public static final String COL_ADDR = "addr";

    public static final String COL_MOBILE = "mobile";

    public static final String COL_PROVINCE_ID = "province_id";

    public static final String COL_PROVINCE = "province";

    public static final String COL_CITY_ID = "city_id";

    public static final String COL_CITY = "city";

    public static final String COL_AREA_ID = "area_id";

    public static final String COL_AREA = "area";

    public static final String COL_SHOP_ID = "shop_id";
}