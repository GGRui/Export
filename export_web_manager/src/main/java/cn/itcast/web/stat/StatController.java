package cn.itcast.web.stat;

import cn.itcast.service.stat.StatService;
import cn.itcast.web.system.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stat")
public class StatController extends BaseController {
    @Reference
    private StatService statService;

    @RequestMapping("/toCharts")
    public String toCharts(String chartsType){
        return "stat/stat-"+chartsType;
    }
    /**
     * 统计生产厂家销售金额
     */
    @RequestMapping("/getFactoryDate")
    @ResponseBody
    public List<Map<String,Object>> getFactoryData() {
        List<Map<String, Object>> list = statService.getFactoryData(getLoginCompanyId());
        return list;
    }

    /**
     * 统计分析 厂家销量统计  前5
     */
    @RequestMapping("/getProductSale")
    @ResponseBody
    public List<Map<String,Object>> getProductSale(){
        List<Map<String, Object>> list = statService.getProductSale(getLoginCompanyId(), 5);
        return list;
    }
    /**
     * 统计分析 在线人数统计(按小时统计)
     */
    @RequestMapping("/getOnline")
    @ResponseBody
    public List<Map<String,Object>> getOnline(){
        List<Map<String, Object>> list = statService.getOnline();
        return list;
    }

}
