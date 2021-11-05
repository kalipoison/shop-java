package com.gohb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gohb.domain.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select(" SELECT DISTINCT t1.perms  FROM `sys_menu` t1 JOIN sys_role_menu t2 ON ( t1.menu_id = t2.menu_id ) JOIN sys_user_role t3 ON ( t2.role_id = t3.role_id )  WHERE t3.user_id = #{userId}  AND ( t1.type = 1 OR t1.type = 2 ) ")
    List<String> findUserAuthsById(@Param("userId") Long userId);

}
