package cn.itcast.service.system;

import cn.itcast.domain.system.Module;
import com.github.pagehelper.PageInfo;


import java.util.List;

public interface ModuleService {

    //根据Id查询
    Module findById(String id);

    //根据id删除
    void delete(String id);

    //添加
    void save(Module module);

    //更新
    void update(Module module);

    //查询全部
    List<Module> findAll();

    //分页查询
    PageInfo<Module> findByPage(int pageNum,int pageSize);

    //根据角色id查询
    List<Module> findModuleByRoleId(String roleId);

    //根据用户id查询权限
    List<Module> findModuleByUserId(String userId);
}
