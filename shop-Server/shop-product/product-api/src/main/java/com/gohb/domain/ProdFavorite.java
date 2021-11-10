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
 * 商品收藏表
 */
@ApiModel(value="com-gohb-domain-ProdFavorite")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prod_favorite")
public class ProdFavorite implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "favorite_id", type = IdType.AUTO)
    @ApiModelProperty(value="主键")
    private Long favoriteId;

    /**
     * 商品ID
     */
    @TableField(value = "prod_id")
    @ApiModelProperty(value="商品ID")
    private Long prodId;

    /**
     * 收藏时间
     */
    @TableField(value = "rec_time")
    @ApiModelProperty(value="收藏时间")
    private Date recTime;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户ID")
    private String userId;

    private static final long serialVersionUID = 1L;

    public static final String COL_FAVORITE_ID = "favorite_id";

    public static final String COL_PROD_ID = "prod_id";

    public static final String COL_REC_TIME = "rec_time";

    public static final String COL_USER_ID = "user_id";
}