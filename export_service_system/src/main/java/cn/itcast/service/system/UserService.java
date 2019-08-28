package cn.itcast.service.system;

import cn.itcast.domain.system.User;
import com.github.pagehelper.PageInfo;

import java.util.List;


public interface UserService {

	//根据企业id查询全部
	PageInfo<User> findByPage(String companyId, int pageNum, int pageSize);

	//查询所有用户
	List<User> findAll(String companyId);

	//根据id查询
    User findById(String userId);

	//根据id删除
	boolean delete(String id);

	//保存
	void save(User user);

	//更新
	void update(User user);

	//给用户分配角色
    void updateUserRoles(String userId, String[] roleIds);

    //通过邮箱账号查询用户对象
	User findByEmail(String email);
}