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
import java.util.Date;


/**
 * 主页轮播图
 */
@ApiModel(value = "com-gohb-domain-IndexImg")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "index_img")
public class IndexImg implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "img_id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long imgId;

    /**
     * 店铺ID
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    /**
     * 图片
     */
    @TableField(value = "img_url")
    @ApiModelProperty(value = "图片")
    private String imgUrl;

    /**
     * 说明文字,描述
     */
    @TableField(value = "des")
    @ApiModelProperty(value = "说明文字,描述")
    private String des;

    /**
     * 标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 链接
     */
    @TableField(value = "link")
    @ApiModelProperty(value = "链接")
    private String link;

    /**
     * 状态
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "状态")
    private Boolean status;

    /**
     * 顺序
     */
    @TableField(value = "seq")
    @ApiModelProperty(value = "顺序")
    private Integer seq;

    /**
     * 上传时间
     */
    @TableField(value = "upload_time")
    @ApiModelProperty(value = "上传时间")
    private Date uploadTime;

    /**
     * 关联商品prodId
     */
    @TableField(value = "relation")
    @ApiModelProperty(value = "关联商品prodId")
    private Long relation;

    /**
     * 类型
     */
    @TableField(value = "type")
    @ApiModelProperty(value = "类型")
    private Integer type;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品的名字")
    private String prodName;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品的图片")
    private String pic;

    private static final long serialVersionUID = 1L;

    public static final String COL_IMG_ID = "img_id";

    public static final String COL_SHOP_ID = "shop_id";

    public static final String COL_IMG_URL = "img_url";

    public static final String COL_DES = "des";

    public static final String COL_TITLE = "title";

    public static final String COL_LINK = "link";

    public static final String COL_STATUS = "status";

    public static final String COL_SEQ = "seq";

    public static final String COL_UPLOAD_TIME = "upload_time";

    public static final String COL_RELATION = "relation";

    public static final String COL_TYPE = "type";
}