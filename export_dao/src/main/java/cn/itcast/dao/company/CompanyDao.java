package cn.itcast.dao.company;

import cn.itcast.domain.company.Company;

import java.util.List;

public interface CompanyDao {
    /**
     * 查询所有企业
     */
    List<Company> findAll();

    /**
     * 保存企业信息
     */
    void save(Company company);

    /**
     * 更新企业信息
     */
    void update(Company company);

    /**
     * 进入修改页面
     */
    Company findById(String id);

    /**
     * 根据id删除
     */
    void delete(String id);
}
