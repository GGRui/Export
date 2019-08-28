package cn.itcast.dao.system;

import cn.itcast.domain.system.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    /**
     * 根据企业id查询全部
     */
    List<User> findAll(String companyId);

    /**
     * 根据id查询
     */
    User findById(String userId);

    /**
     * 根据id删除
     */
    void delete(String userId);

    /**
     * 保存
     */
    void save(User user);

    /**
     * 更新
     */
    void update(User user);

    /**
     * 根据用户id查询用户角色中间表
     * @return
     */
    Long findUserRoleByUserId(String userId);

    /**
     * 根据用户id删除中间表数据
     * @param userId
     */
    void deleteUserRoles(String userId);

    /**
     * 向中间表保存数据
     * @param userId
     * @param roleId
     */
    void saveUserRoles(@Param("userId") String userId,@Param("roleId") String roleId);

    /**
     * 通过邮箱查询用户对象
     * @param email
     * @return
     */
    List<User> findByEmail(String email);

}
