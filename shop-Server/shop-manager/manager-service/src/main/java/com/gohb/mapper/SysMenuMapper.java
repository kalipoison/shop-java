package com.gohb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gohb.domain.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select(" SELECT t1.* from sys_menu t1 join sys_role_menu t2 on(t1.menu_id =t2.menu_id) join sys_user_role t3 on (t2.role_id=t3.role_id) where t3.user_id = #{userId} and (t1.type = 0 or t1.type = 1)  ")
    List<SysMenu> findMenuByUserId(@Param("userId") Long userId);
}
