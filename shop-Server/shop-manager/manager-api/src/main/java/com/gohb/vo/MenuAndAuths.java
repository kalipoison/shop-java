package com.gohb.vo;

import com.gohb.domain.SysMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class MenuAndAuths {

    @ApiModelProperty(value = "菜单数据")
    private List<SysMenu> menuList;

    @ApiModelProperty(value = "用户的权限数据")
    private List<String> authorities;


}
