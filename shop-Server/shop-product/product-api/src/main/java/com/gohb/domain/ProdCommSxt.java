package com.gohb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 前台评论对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdCommSxt {

    private Long prodId;
    private Date recTime;
    private String pic;
    private String nickName;
    private Integer score;
    private String content;
    private String pics;
    private String replyContent;
}
