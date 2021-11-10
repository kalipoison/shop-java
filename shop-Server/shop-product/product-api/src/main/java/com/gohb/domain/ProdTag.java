package com.gohb.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品分组表
 */
@ApiModel(value="com-gohb-domain-ProdTag")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prod_tag")
public class ProdTag implements Serializable {
    /**
     * 分组标签id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="分组标签id")
    private Long id;

    /**
     * 分组标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value="分组标题")
    private String title;

    /**
     * 店铺Id
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value="店铺Id")
    private Long shopId;

    /**
     * 状态(1为正常,0为删除)
     */
    @TableField(value = "status")
    @ApiModelProperty(value="状态(1为正常,0为删除)")
    private Boolean status;

    /**
     * 默认类型(0:商家自定义,1:系统默认)
     */
    @TableField(value = "is_default")
    @ApiModelProperty(value="默认类型(0:商家自定义,1:系统默认)")
    private Boolean isDefault;

    /**
     * 商品数量
     */
    @TableField(value = "prod_count")
    @ApiModelProperty(value="商品数量")
    private Long prodCount;

    /**
     * 列表样式(0:一列一个,1:一列两个,2:一列三个)
     */
    @TableField(value = "style")
    @ApiModelProperty(value="列表样式(0:一列一个,1:一列两个,2:一列三个)")
    private Integer style;

    /**
     * 排序
     */
    @TableField(value = "seq")
    @ApiModelProperty(value="排序")
    private Integer seq;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="修改时间")
    private Date updateTime;

    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    @ApiModelProperty(value="删除时间")
    private Date deleteTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_TITLE = "title";

    public static final String COL_SHOP_ID = "shop_id";

    public static final String COL_STATUS = "status";

    public static final String COL_IS_DEFAULT = "is_default";

    public static final String COL_PROD_COUNT = "prod_count";

    public static final String COL_STYLE = "style";

    public static final String COL_SEQ = "seq";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_DELETE_TIME = "delete_time";
}
