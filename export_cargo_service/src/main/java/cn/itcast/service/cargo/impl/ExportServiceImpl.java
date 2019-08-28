package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.*;
import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.vo.ExportProductResult;
import cn.itcast.vo.ExportResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private ExportDao exportDao;
    // 注入货物dao
    @Autowired
    private ContractProductDao contractProductDao;
    // 注入附件dao
    @Autowired
    private ExtCproductDao extCproductDao;
    // 注入购销合同dao
    @Autowired
    private ContractDao contractDao;
    // 注入商品dao
    @Autowired
    private ExportProductDao exportProductDao;
    // 注入商品附件dao
    @Autowired
    private ExtEproductDao extEproductDao;


    @Override
    public Export findById(String id) {
        return exportDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Export export) {

        //1. 设置报运单id
        export.setId(UUID.randomUUID().toString());

        // 设置制单时间
        export.setInputDate(new Date());
        String[] array = export.getContractIds().split(",");
        // 设置合同号
        String contractNos= "";
        for (String contractId : array) {
            //根据购销合同id查询
            Contract contract = contractDao.selectByPrimaryKey(contractId);
            // 获取多个购销合同id数组
            contractNos += contract.getContractNo() + " ";
            // 修改购销合同状态为2
            contract.setState(2);
            contractDao.updateByPrimaryKeySelective(contract);
        }

        // 设置合同号
        export.setCustomerContract(contractNos);

        //保存报运的商品
        /**
         * 定义一个map集合，存储货物id，商品id
         * Map（货物id，商品id） map
         */
        Map<String, String> map = new HashMap<>();

        //根据购销合同id，查询货物
        ContractProductExample contractProductExample = new ContractProductExample();
        contractProductExample.createCriteria().andContractIdIn(Arrays.asList(array));
        List<ContractProduct> list = contractProductDao.selectByExample(contractProductExample);
        //遍历货物，构造报运商品
        for (ContractProduct contractProduct : list) {
            ExportProduct exportProduct = new ExportProduct();
            BeanUtils.copyProperties(contractProduct,exportProduct);
            //设置商品属性
            exportProduct.setId(UUID.randomUUID().toString());
            exportProduct.setExportId(export.getId());
            //保存商品
            exportProductDao.insertSelective(exportProduct);

            //存储货物id，以及对应商品id
            map.put(contractProduct.getId(),exportProduct.getId());
        }
        //3.保持报运商品附件（关键点，报运单id，每一个报运商品的id）
        //报运商品附件数据来源，购销合同的附件
        //根据购销合同id查询附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractIdIn(Arrays.asList(array));
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);

        //遍历购销合同附件，作为报运商品附件
        for (ExtCproduct extCproduct : extCproductList) {
            //创建商品附件
            ExtEproduct extEproduct = new ExtEproduct();
            //货物附件，商品附件
            BeanUtils.copyProperties(extCproduct,extEproduct);
            //设置商品属性
            extEproduct.setId(UUID.randomUUID().toString());
            //设置报运id
            extEproduct.setExportId(export.getId());
            /**
             * 设置商品id，
             * 已知条件：货物id
             */
            extEproduct.setExportProductId(map.get(extCproduct.getContractProductId()));
            extEproductDao.insertSelective(extEproduct);

        }
        //保存报运单
        //设置报运单状态
        export.setState(0);
        //设置商品数
        export.setProNum(list.size());
        export.setExtNum(extCproductList.size());
        //保存报运单
        exportDao.insertSelective(export);
    }

    @Override
    public void update(Export export) {

        //1.修改报运单
        exportDao.updateByPrimaryKeySelective(export);
        //2.修改报运商品
        //2.1获取报运单的集合
        List<ExportProduct> list = export.getExportProducts();
        //遍历
        if (list != null && list.size()>0){

            for (ExportProduct exportProduct : list) {
                //2.2修改商品
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }
    }

    @Override
    public void delete(String id) {
        exportDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Export> findByPage(ExportExample exportExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Export> list = exportDao.selectByExample(exportExample);
        return new PageInfo<>(list);
    }

    @Override
    public void updateExport(ExportResult result) {
        //1.查询报运单
        Export export = exportDao.selectByPrimaryKey(result.getExportId());
        //2.设置报运单属性，状态和说明
        export.setState(result.getState());
        export.setRemark(result.getRemark());
        exportDao.updateByPrimaryKeySelective(export);

        //3.循环处理报运商品
        for (ExportProductResult epr : result.getProducts()) {
            ExportProduct exportProduct = exportProductDao.selectByPrimaryKey(epr.getExportProductId());
            //对货运商品的税收修改
            exportProduct.setTax(epr.getTax());
            exportProductDao.updateByPrimaryKeySelective(exportProduct);
        }
    }
}
