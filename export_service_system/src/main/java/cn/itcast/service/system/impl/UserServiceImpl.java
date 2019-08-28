package cn.itcast.service.system.impl;

import cn.itcast.dao.system.UserDao;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public PageInfo<User> findByPage(String companyId, int pageNum, int pageSize) {
        //调用startPage方法
        PageHelper.startPage(pageNum,pageSize);
        //查询全部列表
        List<User> list = userDao.findAll(companyId);

        //构造pageBean
        return new PageInfo<User>(list);
    }

    @Override
    public List<User> findAll(String companyId) {
        return userDao.findAll(companyId);
    }


    @Override
    public User findById(String userId) {
        return userDao.findById(userId);
    }

    @Override
    public boolean delete(String id) {

        Long count=userDao.findUserRoleByUserId(id);
        if (count != null && count>0){
            //删除失败
            return false;
        }else{
            //删除成功
            userDao.delete(id);
            return true;
        }
    }

    @Override
    public void save(User user) {
        user.setId(UUID.randomUUID().toString());
        if(user.getPassword() != null){
            String encodPw = new Md5Hash(user.getPassword(),user.getEmail()).toString();user.setPassword(encodPw);
        }
        userDao.save(user);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public void updateUserRoles(String userId, String[] roleIds) {
        //根据UserId删除中间表数据
        userDao.deleteUserRoles(userId);

        for (String roleId : roleIds) {
            userDao.saveUserRoles(userId,roleId);
        }
        
    }

    @Override
    public User findByEmail(String email) {
        List<User> list = userDao.findByEmail(email);
        return list!=null&&list.size()>0 ? list.get(0) : null;
    }
}
