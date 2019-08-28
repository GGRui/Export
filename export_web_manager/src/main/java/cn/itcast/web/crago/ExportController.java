package cn.itcast.web.crago;

import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.service.cargo.ExportProductService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.vo.ExportProductVo;
import cn.itcast.vo.ExportResult;
import cn.itcast.vo.ExportVo;
import cn.itcast.web.system.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 出口报运模块控制器
 */
@Controller
@RequestMapping("/cargo/export")
public class ExportController extends BaseController{

    //注入购销合同构造类



    //注入购销合同业务类
    @Reference
    private ContractService contractService;
    @Reference
    private ExportProductService exportProductService;
    @Reference
    private ExportService exportService;


    /**
     * -1.报运单查看
     */
    @RequestMapping("toView")
    public String toView(String id){
        //根据id查询
        Export export = exportService.findById(id);
        request.setAttribute("export",export);
        //跳转到登录页面
        return "cargo/export/export-view";
    }

    /**
     * 0.删除
     */
    @RequestMapping("/delete")
    public String delete(String id){
        exportService.delete(id);
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 1.合同管理，显示已经上报待报运的合同
     */
    @RequestMapping("/contractList")
    public String contractList
    (
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ){
        //构造条件
        ContractExample example = new ContractExample();
        example.setOrderByClause("create_time desc");
        //查询条件
        ContractExample.Criteria criteria = example.createCriteria();

        //1.3 查询条件: 企业id
        criteria.andCompanyIdEqualTo(getLoginCompanyId());
        //1.4 按照购销合同的状态查询
        criteria.andStateEqualTo(1);

        //2 调用service查询
        PageInfo<Contract> pageInfo =
                contractService.findByPage(example,pageNum,pageSize);
        //返回
        request.setAttribute("pageInfo",pageInfo);
        // 转发: 合同管理页面
        return "cargo/export/export-contractList";
    }

    /**
     * 2.出口报运单列表
     */
    @RequestMapping("/list")
    public String list
    (
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize
    ){
            //构造条件
        ExportExample exportExample = new ExportExample();
        ExportExample.Criteria criteria = exportExample.createCriteria();

        criteria.andCompanyIdEqualTo(getLoginCompanyId());

        //调用
        PageInfo<Export> pageInfo = exportService.findByPage(exportExample, pageNum, pageSize);

        request.setAttribute("pageInfo",pageInfo);
        return "cargo/export/export-list";
    }

    /**
     * 3.添加报运单（1） 进入报运页面
     */
    @RequestMapping("/toExport")
    public String toExport(String id){
        //保存购销合同id
        request.setAttribute("id",id);
        return "cargo/export/export-toExport";
    }

    /**
     * 4.添加报运单，修改报运单
     */
    @RequestMapping("/edit")
    public String edit(Export export){
        //设置所属企业id，名称
        export.setCompanyId(getLoginCompanyId());
        export.setCompanyName(getLoginCompanyName());

        if (StringUtils.isEmpty(export.getId())){
            //添加
            exportService.save(export);
        }else {
            //修改
            exportService.update(export);
        }
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 5.进入报运单修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        //根据报运单id查询
        Export export = exportService.findById(id);

        //根据报运单id查询报运商品
        ExportProductExample exportProductExample = new ExportProductExample();
        exportProductExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> eps = exportProductService.findAll(exportProductExample);
        //保存回显数据
        request.setAttribute("export",export);
        request.setAttribute("eps",eps);
        return "cargo/export/export-update";
    }

    /**
     * 6.出口列表，点击取消
     */
    @RequestMapping("/cancel")
    public String cancel(String id){
        Export export = exportService.findById(id);
        export.setState(0);
        exportService.update(export);
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 7.出口列表，点击提交
     */
    @RequestMapping("/submit")
    public String submit(String id){
        Export export = exportService.findById(id);
        export.setState(1);
        exportService.update(export);
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 8.电子报运
     */
    @RequestMapping("/exportE")
    public String exportE(String id){
        //根据报运单id查询报运单对象
        Export export = exportService.findById(id);
        //更具报运单id查询报运商品列表
        ExportProductExample example = new ExportProductExample();
        example.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> eps = exportProductService.findAll(example);

        //构造电子报运vo对象 并赋值
        ExportVo vo = new ExportVo();
        BeanUtils.copyProperties(export,vo);
        vo.setExportId(export.getId());

        //构造报运商品数据
        ArrayList<ExportProductVo> products = new ArrayList<>();
        for (ExportProduct ep : eps) {
            ExportProductVo epv = new ExportProductVo();
            BeanUtils.copyProperties(ep,epv);
            epv.setExportProductId(ep.getId());
            products.add(epv);
        }
        vo.setProducts(products);

        //电子报运
        WebClient client = WebClient.create("http://localhost:9001/ws/export/user");
        client.post(vo);
        //查询报运结果
        client = WebClient.create("http://localhost:9001/ws/export/user/" + id);
        ExportResult result = client.get(ExportResult.class);

        //调用service完成报运结果的入库
        exportService.updateExport(result);
        return "redirect:/cargo/export/list.do";
    }

}
