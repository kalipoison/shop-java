package com.gohb.vo;

import com.gohb.domain.SysRole;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class SysRoleVo extends SysRole {

    //添加角色时权限的vo
    private List<String> menuIdList;
}
