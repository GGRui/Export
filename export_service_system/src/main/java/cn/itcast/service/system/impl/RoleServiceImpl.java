package cn.itcast.service.system.impl;

import cn.itcast.dao.system.RoleDao;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public Role findById(String id) {
        return roleDao.findById(id);
    }

    @Override
    public PageInfo<Role> findByPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Role> list = roleDao.findAll(companyId);

        return new PageInfo<Role>(list);
    }

    @Override
    public void delete(String id) {
        roleDao.delete(id);
    }

    @Override
    public void save(Role role) {
        role.setId(UUID.randomUUID().toString());
        roleDao.save(role);

    }

    @Override
    public void update(Role role) {
        roleDao.update(role);
    }

    @Override
    public void updateRoleModule(String roleId, String moduleIds) {

        //1.删除用户角色中间表的数据
        roleDao.deleteRoleModule(roleId);

        //2.判断
        if (moduleIds != null){
            //分割字符串
            String[] array=moduleIds.split(",");
            if (array != null){
                //遍历所有模块，实现角色添加模块
                for (String moduleId : array) {
                    roleDao.saveRoleModule(roleId,moduleId);
                }
            }
        }
    }

    @Override
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(companyId);
    }

    @Override
    public List<Role> findUserRole(String userId) {
        return roleDao.findUserRole(userId);
    }

}
