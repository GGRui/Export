package cn.itcast.dao.stat;

import java.util.List;
import java.util.Map;

public interface StatDao {
    /**
     * 统计生产厂家销量金额
     * @param companyId 用户所属公司
     * @return 返回ECharts需要的数据格式.Map的key是生产厂家，value是销售金额
     */
    List<Map<String,Object>> getFactoryData(String companyId);


    /**
     * 统计分析 厂家销量统计  前5
     */
    List<Map<String,Object>> getProductSale(String companyId,int top);

    /**
     * 统计分析 在线人数统计(按小时统计)
     */
    List<Map<String,Object>> getOnline();
}
