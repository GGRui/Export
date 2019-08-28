package cn.itcast.service.company;

import cn.itcast.domain.company.Company;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CompanyService {
    /**
     * 查询所有企业
     */
    List<Company> findAll();

    /**
     *保存企业信息
     */
    void save(Company company);

    /**
     *更新企业信息
     */
    void update(Company company);

    /**
     * 根据id查询
     */
    Company findById(String id);

    /**
     * 根据id删除
     */
    void delete(String id);

    /**
     * 分页查询
     */
    PageInfo<Company> findByPage(int pageNum, int pageSize);
}
