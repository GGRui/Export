package cn.itcast.web.controller.company;

import cn.itcast.domain.company.Company;
import cn.itcast.service.company.CompanyService;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/company")
public class CompanyController {
    //注入service
    @Reference
    private CompanyService companyService;

    /**
     * 企业列表
     * 请求地址：http://localhost:8080/company/list.do
     * 请求参数：无
     * 响应地址：/WEB-INF/pages/company/company-list.jsp
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ){
        PageInfo<Company> pageInfo = companyService.findByPage(pageNum,pageSize);
        List<Company> list = companyService.findAll();
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("list",list);
        mv.addObject("pageInfo",pageInfo);
        // 前缀：/WEB-INF/pages/
        // 后缀：.jsp
        mv.setViewName("company/company-list");
        return mv;
    }

    /**
     * 测试：类型转换
     * 请求地址：http://localhost:8080/company/list.do
     * 请求参数：birth=1998-09-09
     * 访问结果：HTTP Status 400 – Bad Request
     *         封装请求数据粗问题了。
     * 观察日志：
     *    Failed to convert value of type 'java.lang.String' to required type 'java.util.Date';
     * 原因：
     *     SpringMVC不能自动把String-->Date
     * 解决：
     *     1. 写一个类(转换器类)，实现Converter接口
     *     2. 配置转换器工厂，注入转换器（springmvc.xml）
     */
    @RequestMapping("/save")
    public String save(Date birth){
        int i = 1/0;
        System.out.println(birth);
        // 保存后重定向到列表
        return "redirect:/company/list.do";
    }

    /**
     * 进入新增页面  (company-company-add.jsp)
     */
    @RequestMapping(value = "/toAdd")
    public String toAdd(){
        return "company/company-add";
    }

    /**
     * 保存或修改企业信息
     */
    @RequestMapping("/edit")
    public String edit(Company company){
        //1.如果请求参数没有传入id，说明进行的是添加操作
        if(StringUtils.isEmpty(company.getId())){
            companyService.save(company);
        }
        else{
            companyService.update(company);
        }
        return "redirect:/company/list.do";
    }

    /**
     * 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        //根据id查询
        Company company = companyService.findById(id);
        //返回
        ModelAndView mv = new ModelAndView();
        mv.setViewName("company/company-update");
        mv.addObject("company",company);
        return mv;
    }

    /**
     * 根据id删除
     */
    @RequestMapping("/delete")
    public String delete(String id){
        //调用service删除
        companyService.delete(id);
        //删除完重定向到列表
        return "redirect:/company/list.do";
    }
}
