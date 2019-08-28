package cn.itcast.web;

import cn.itcast.domain.company.Company;
import cn.itcast.service.company.CompanyService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApplyController {
    /**
     * @Referenceb retries=0  重置配置次数
     */
    @Reference(retries = 2)
    private CompanyService companyService;

    /**
     * 企业入驻，保存、申请
     */
    @RequestMapping("/apply")
    @ResponseBody
    public String apply(Company company) {
        try {
            company.setState(0);
            companyService.save(company);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "2";
        }
    }
}
