package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.service.cargo.ContractService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 购销合同模块服务接口实现
 */
@Service
public class ContractServiceImpl implements ContractService {
    //注入dao
    @Autowired
    private ContractDao contractDao;
    @Override
    public PageInfo<Contract> findByPage(ContractExample contractExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Contract> list = contractDao.selectByExample(contractExample);
        return new PageInfo<>(list);
    }

    @Override
    public List<Contract> findAll(ContractExample contractExample) {
        return contractDao.selectByExample(contractExample);
    }

    @Override
    public Contract findById(String id) {
        return contractDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Contract contract) {
        contract.setId(UUID.randomUUID().toString());
        contract.setCreateTime(new Date());
        contract.setUpdateTime(new Date());
        contract.setState(0);
        // 初始化： 总金额为0
        contract.setTotalAmount(0d);
        // 初始化： 货物数、附件数
        contract.setProNum(0);
        contract.setExtNum(0);
        contractDao.insertSelective(contract);
    }

    @Override
    public void update(Contract contract) {
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void delete(String id) {
        contractDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Contract> selectByDeptId(String deptId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Contract> list = contractDao.selectByDeptId(deptId);

        return new PageInfo<>(list);
    }
}
