package cn.itcast.web.crago;

import cn.itcast.domain.cargo.ContractProduct;
import cn.itcast.domain.cargo.ContractProductExample;
import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.web.system.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cargo/contractProduct")
public class ContractProductController extends BaseController {

    @Reference
    private ContractProductService contractProductService;
    @Reference
    private FactoryService factoryService;

    @RequestMapping("/list")
    public String list
     (
             String contractId,
         @RequestParam(defaultValue = "1") Integer pageNum,
         @RequestParam(defaultValue = "5") Integer pageSize
     ){
        //1.查询厂家
        FactoryExample factoryExample = new FactoryExample();
        FactoryExample.Criteria factoryExampleCriteria = factoryExample.createCriteria();
        factoryExampleCriteria.andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);


        //2.根据不同的购销合同id，查询该购销合同下的所有货物
        ContractProductExample contractProductExample = new ContractProductExample();
        contractProductExample.createCriteria().andContractIdEqualTo(contractId);
        ContractProductExample.Criteria criteria = contractProductExample.createCriteria();
        criteria.andContractIdEqualTo(contractId);
        PageInfo<ContractProduct> pageInfo = contractProductService.findByPage(contractProductExample, pageNum, pageSize);

        //3.存储购销合同
        request.setAttribute("pageInfo",pageInfo);
        request.setAttribute("factoryList",factoryList);
        request.setAttribute("contractId",contractId);
        //4.  返回
        return "cargo/product/product-list";
    }

    /**
     * 添加和修改
     */
    @RequestMapping("/edit")
    public String edit(ContractProduct contractProduct){
        //设置部门所属的企业，id，名称
        contractProduct.setCompanyId(getLoginCompanyId());
        contractProduct.setCompanyName(getLoginCompanyName());

        //判断
        if (StringUtils.isEmpty(contractProduct.getId())){
            contractProductService.save(contractProduct);
        }else{
            //修改
            contractProductService.update(contractProduct);
        }
        return "redirect:/cargo/contractProduct/list.do?contractId="
                +contractProduct.getContractId();
    }

    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        //3.1 根据货物id查询
        ContractProduct contractProduct = contractProductService.findById(id);

        //3.2 查询货物工厂
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        //3.3 保存
        request.setAttribute("contractProduct",contractProduct);
        request.setAttribute("factoryList",factoryList);

        return "cargo/product/product-update";
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public String delete(String id,String contractId){
        contractProductService.delete(id);
        return "redirect:/cargo/contractProduct/list.do?contractId="+contractId;
    }

    /**
     * 5. ApachePOI实现货物上传 (1) 进入上传货物页面
     */
    @RequestMapping("/toImport")
    public String toImport(String contractId){
        // 保存购销合同id，因为后面上传货物，要指定对哪个购销合同添加货物
        request.setAttribute("contractId",contractId);
        return "cargo/product/product-import";
    }

    /**
     *  6. ApachePOI实现货物上传 (2) 上传   读取excel--->封装对象--->调用service保存
     *  请求参数：<input type="file" name="file">
     */
    @RequestMapping("/import")
    public String improtExcel(String contractId,MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

        XSSFSheet sheetAt = workbook.getSheetAt(0);

        //获取总行数
        int totalRow = sheetAt.getPhysicalNumberOfRows();

        //遍历每一行，第一行是表头，从第二行开始
        for (int i = 1; i < totalRow; i++) {
            //获取每一行
            Row row = sheetAt.getRow(i);

            //创建货物对象，把excel的每一行封装成一个货物对象
            ContractProduct cp = new ContractProduct();
            cp.setContractId(contractId); // 注意：要设置购销合同id
            cp.setFactoryName(row.getCell(1).getStringCellValue());
            cp.setProductNo(row.getCell(2).getStringCellValue());
            cp.setCnumber((int) row.getCell(3).getNumericCellValue());
            cp.setPackingUnit(row.getCell(4).getStringCellValue());
            cp.setLoadingRate(row.getCell(5).getNumericCellValue()+"");
            cp.setBoxNum((int) row.getCell(6).getNumericCellValue());
            cp.setPrice(row.getCell(7).getNumericCellValue());
            cp.setProductDesc(row.getCell(8).getStringCellValue());
            cp.setProductRequest(row.getCell(9).getStringCellValue());
            // 设置厂家id
            Factory factory = factoryService.findByName(cp.getFactoryName());
            if (factory != null) {
                cp.setFactoryId(factory.getId());
            }

            //保存货物
            contractProductService.save(cp);

        }
        return "cargo/product/product-import";
    }
}
