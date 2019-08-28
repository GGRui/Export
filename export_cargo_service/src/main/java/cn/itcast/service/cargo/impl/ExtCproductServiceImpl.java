package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.dao.cargo.ExtCproductDao;
import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ExtCproduct;
import cn.itcast.domain.cargo.ExtCproductExample;
import cn.itcast.service.cargo.ExtCproductService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service
public class ExtCproductServiceImpl implements ExtCproductService {

    @Autowired
    private ExtCproductDao extCproductDao;
    @Autowired
    private ContractDao contractDao;


    @Override
    public PageInfo<ExtCproduct> findByPage(ExtCproductExample extCproductExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<ExtCproduct> list = extCproductDao.selectByExample(extCproductExample);
        return new PageInfo<>(list);
    }

    @Override
    public List<ExtCproduct> findAll(ExtCproductExample extCproductExample) {
        return extCproductDao.selectByExample(extCproductExample);
    }

    @Override
    public ExtCproduct findById(String id) {
        return extCproductDao.selectByPrimaryKey(id);
    }

    //添加附件
    @Override
    public void save(ExtCproduct extCproduct) {
        //0. 设置id
        extCproduct.setId(UUID.randomUUID().toString());

        //1. 计算附件金额
        Double amount = 0d;
        if (extCproduct.getCnumber() != null &&
                extCproduct.getPrice() != null) {
            amount = extCproduct.getCnumber() * extCproduct.getPrice();
        }
        extCproduct.setAmount(amount);

        //2. 修改购销合同总金额、附件数
        //2.1 根据购销合同id查询
        Contract contract =
                contractDao.selectByPrimaryKey(extCproduct.getContractId());

        //2.2 修改购销合同总金额
        contract.setTotalAmount(contract.getTotalAmount() + amount);

        //2.3 修改购销合同附件数量要加1
        contract.setExtNum(contract.getExtNum() + 1);

        //2.4 保存购销合同
        contractDao.updateByPrimaryKeySelective(contract);

        //3. 添加附件
        extCproductDao.insertSelective(extCproduct);
    }

    @Override
    public void update(ExtCproduct extCproduct) {
        //1.计算修改后附件金额
        Double amount = 0d;
        if (extCproduct.getCnumber() != null && extCproduct.getPrice() != null){
            amount = extCproduct.getCnumber() * extCproduct.getPrice();
        }

        //获取修改前的附件金额， 需要查询数据库
        ExtCproduct ext = extCproductDao.selectByPrimaryKey(extCproduct.getId());

        Double oldAmount = ext.getAmount();

        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        contract.setTotalAmount(contract.getTotalAmount() + amount - oldAmount);

        contractDao.updateByPrimaryKeySelective(contract);
        extCproductDao.updateByPrimaryKeySelective(extCproduct);
    }

    //需求：删除附件，需要修改购销合同（总金额、附件数量）
    @Override
    public void delete(String id) {

        //1.根据附件id查询
        ExtCproduct extCproduct = extCproductDao.selectByPrimaryKey(id);

        Double amount = extCproduct.getAmount();

        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());

        contract.setTotalAmount(contract.getTotalAmount()-amount);

        contract.setProNum(contract.getProNum() - 1);

        contractDao.updateByPrimaryKeySelective(contract);

        extCproductDao.deleteByPrimaryKey(id);
    }
}
