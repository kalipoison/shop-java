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

    @ApiModelProperty(value = "树菜单")
    private List<SysMenu> menuList;

    @ApiModelProperty(value = "权限")
    private List<String> authorities;


}
