package cn.itcast.service.system.impl;

import cn.itcast.dao.system.DeptDao;
import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptDao deptDao;

    @Override
    public PageInfo<Dept> findByPage(String companyId, int pageSize, int pageNum) {
        //1.调用startPage方法
        PageHelper.startPage(pageSize,pageNum);
        //查询所有列表
        List<Dept> list = deptDao.findAll(companyId);
        //返回分页对象
        return new PageInfo<Dept>(list);
    }

    @Override
    public Dept findById(String id) {
        return deptDao.findById(id);
    }

    @Override
    public List<Dept> findAll(String companyId) {
        return deptDao.findAll(companyId);
    }

    @Override
    public void save(Dept dept) {
        dept.setId(UUID.randomUUID().toString());
        deptDao.save(dept);
    }

    @Override
    public void update(Dept dept) {
        deptDao.update(dept);
    }

    @Override
    public boolean delete(String id) {
        //先根据当前要删除的部门id，作为父部门查询条件进行查询
        //如：select * from pe_dept where parent_id='100'
       List<Dept> list =  deptDao.findDeptByParentId(id);
       //2.判断：如果查询到数据，说明当前的部门有被其他部门引用。不能删除
        if (list !=null && list.size()>0){
            return false;
        }else{
            //后期这里不需要查询判断， 因为pe_user也引用了部门id
            deptDao.delete(id);
            return true;
        }
    }
}
