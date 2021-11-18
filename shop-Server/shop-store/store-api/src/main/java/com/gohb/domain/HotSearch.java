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
 * 热搜
 */
@ApiModel(value = "com-gohb-domain-HotSearch")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "hot_search")
public class HotSearch implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "hot_search_id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long hotSearchId;

    /**
     * 店铺ID 0为全局热搜
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value = "店铺ID 0为全局热搜")
    private Long shopId;

    /**
     * 内容
     */
    @TableField(value = "content")
    @ApiModelProperty(value = "内容")
    private String content;

    /**
     * 录入时间
     */
    @TableField(value = "rec_date")
    @ApiModelProperty(value = "录入时间")
    private Date recDate;

    /**
     * 顺序
     */
    @TableField(value = "seq")
    @ApiModelProperty(value = "顺序")
    private Integer seq;

    /**
     * 状态 0下线 1上线
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "状态 0下线 1上线")
    private Integer status;

    /**
     * 热搜标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value = "热搜标题")
    private String title;

    private static final long serialVersionUID = 1L;

    public static final String COL_HOT_SEARCH_ID = "hot_search_id";

    public static final String COL_SHOP_ID = "shop_id";

    public static final String COL_CONTENT = "content";

    public static final String COL_REC_DATE = "rec_date";

    public static final String COL_SEQ = "seq";

    public static final String COL_STATUS = "status";

    public static final String COL_TITLE = "title";
}