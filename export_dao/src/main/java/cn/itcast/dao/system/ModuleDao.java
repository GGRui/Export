package cn.itcast.dao.system;

import cn.itcast.domain.system.Module;

import java.util.List;

public interface ModuleDao {

    //根据id查询
    Module findById(String id);

    //根据id删除
    void delete (String moduleId);

    //添加
    void save(Module module);

    //更新
    void update(Module module);

    //查询全部
    List<Module> findAll();

    //根据角色id查询角色所具有的权限
    List<Module> findModuleByRoleId(String roleId);

    //根据从属belong字段进行查询
    List<Module> findByBelong(String s);

    //根据用户Id查询用户权限
    List<Module> findModuleByUserId(String userId);
}
