package cn.itcast.service.system;

import cn.itcast.domain.system.Dept;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface DeptService {
    /**
     * 分页查询
     * @param companyId  公司的id
     * @param pageNum    当前页
     * @param pageSize   页大小
     * @return
     */
    PageInfo<Dept> findByPage(String companyId,int pageNum,int pageSize);

    /**
     * 根据id查询部门
     */
    Dept findById(String id);

    /**
     * 查询所有的部门
     * @param companyId 公司的id
     * @return
     */
    List<Dept> findAll(String companyId);

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
     * 删除部门
     * @param id
     * @return
     */
    boolean delete(String id);
}
