package cn.itcast.web.crago;

import cn.itcast.domain.cargo.ExtCproduct;
import cn.itcast.domain.cargo.ExtCproductExample;
import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import cn.itcast.service.cargo.ExtCproductService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.web.system.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/cargo/extCproduct")
public class ExtCproductController  extends BaseController{

    @Reference
    private FactoryService factoryService;
    @Reference
    private ExtCproductService extCproductService;

    /**
     * 1. 附件列表与添加页面
     * 需求：
     *      查询货物的附件
     * 功能入口：
     *      购销合同--->货物----> 点击附件
     * 请求地址：
     *      http://localhost:8080/cargo/extCproduct/list.do
     * 请求参数：
     *      contractId              购销合同
     *      contractProductId       货物
     */
    @RequestMapping(value="/list")
    public String list
    (
        String contractId,String contractProductId,
        @RequestParam(defaultValue = "1") int pageNum,
        @RequestParam(defaultValue = "5") int pageSize
    ){
        //1.查询附件的生产厂家
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);



        //2.查询当前货物下的所有附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractProductIdEqualTo(contractProductId);

        PageInfo<ExtCproduct> pageInfo =extCproductService.findByPage(extCproductExample, pageNum, pageSize);

        request.setAttribute("factoryList",factoryList);
        request.setAttribute("pageInfo",pageInfo);
        request.setAttribute("contractId",contractId);
        request.setAttribute("contractProductId",contractProductId);

        return "cargo/extc/extc-list";

    }

    /**
     * 2.附件添加，修改
     */
    @RequestMapping("/edit")
    public String edit(ExtCproduct extCproduct){
        //设置部门所属企业id，名称
        extCproduct.setCompanyId(getLoginCompanyId());
        extCproduct.setCompanyName(getLoginCompanyName());

        //判断
        if (StringUtils.isEmpty(extCproduct.getId())){
            //添加
            extCproductService.save(extCproduct);
        }else{
            extCproductService.update(extCproduct);
        }

        return "redirect:/cargo/extCproduct/list.do?contractId="+ extCproduct.getContractId()
                + "&contractProductId=" + extCproduct.getContractProductId();
    }

    /**
     * 3.附件修改（1）进入修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        //根据附件id查询
        ExtCproduct extCproduct = extCproductService.findById(id);

        //查询附件工厂
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        //保存
        request.setAttribute("extCproduct",extCproduct);
        request.setAttribute("factoryList",factoryList);

        return "cargo/extc/extc-update";

    }

    @RequestMapping("/delete")
    public String delete(String id ,String contractId,String contractProductId){
            //调用service删除
        extCproductService.delete(id);
        return "redirect:/cargo/extCproduct/list.do?contractId="+
                contractId + "&contractProductId=" + contractProductId;
    }
}
