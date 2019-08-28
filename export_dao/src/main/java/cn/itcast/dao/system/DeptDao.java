package cn.itcast.dao.system;

import cn.itcast.domain.system.Dept;

import java.util.List;

public interface DeptDao {

    /**
     * 查询所有部门
     * @param companyId 根据企业id查询
     * @return
     */
    List<Dept> findAll(String companyId);

    /**
     * 根据id查询部门
     */
    Dept findById(String id);

    /**
     * 添加部门
     * @param dept
     */
    void save(Dept dept);

    /**
     * 修改部门
     * @param dept
     */
    void update(Dept dept);

    /**
     * 根据父部门查询
     * @param parentId
     * @return
     */
    List<Dept> findDeptByParentId(String parentId);

    /**
     * 删除
     * @param id
     */
    void delete(String id);
}
