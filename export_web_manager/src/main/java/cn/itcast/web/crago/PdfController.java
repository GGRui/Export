package cn.itcast.web.crago;

import cn.itcast.domain.cargo.Export;
import cn.itcast.domain.cargo.ExportProduct;
import cn.itcast.domain.cargo.ExportProductExample;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ExportProductService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.web.system.BaseController;
import cn.itcast.web.utils.BeanMapUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Controller
@RequestMapping("/cargo/export")
public class PdfController extends BaseController {


    @Autowired
    private DataSource dataSource;

    @Reference
    private ExportService exportService;

    @Reference
    private ExportProductService exportProductService;

    /**
     * 1.入门案例，展示pdf
     */
    @RequestMapping("/exportPdf")
    public void  exportPdf() throws JRException, IOException {
        //1.加载jasper文件，获取文件流
        InputStream in = session.getServletContext().getResourceAsStream("/jasper/test03_parameter.jasper");

        //2.创建jasperPrint对象
        /**
         * 2.设置参数
         */
        Map<String,Object> map = new HashMap<>();
        map.put("userName","老王");
        map.put("email","lw@export.cn");
        map.put("companyName","传智播客");
        map.put("deptName","Java教研部");


        //参数1:模板文件输入流；参数2：传递到模板文件中的key-value类型的参数；参数3：数据列表参数
        JasperPrint jasperPrint = JasperFillManager.fillReport(in,map, new JREmptyDataSource());
        //3.以pdf形式输出
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 导出pdf的测试（4）填充数据：jdbc数据源
     */
    @RequestMapping("/exportPdf4")
    public void  exportPdf4() throws JRException, IOException, SQLException {
        //1.加载jasper文件，获取文件流
        InputStream in = session.getServletContext().getResourceAsStream("/jasper/test04_datasource.jasper");

        //2.创建jasperPrint对象
        //获取连接对象
        Connection connection = dataSource.getConnection();

        //参数1:模板文件输入流；参数2：传递到模板文件中的key-value类型的参数；参数3：数据列表参数
        JasperPrint jasperPrint = JasperFillManager.fillReport(in,new HashMap<>(), connection);
        //3.以pdf形式输出
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 5.导出pdf的测试（5）填充数据：JavaBean数据源
     */
    @RequestMapping("/exportPdf5")
    public void  exportPdf5() throws JRException, IOException, SQLException {
        //1.加载jasper文件，获取文件流
        InputStream in = session.getServletContext().getResourceAsStream("/jasper/test05_javabean.jasper");

        //2.创建jasperPrint对象
        //获取连接对象
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUserName("小猫咪"+i);
            user.setEmail(i+"@qq.com");
            user.setCompanyName("企业"+i);
            user.setDeptName("部门"+i);
            list.add(user);
        }


        //将lsit构造成jrdataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);


        //参数1:模板文件输入流；参数2：传递到模板文件中的key-value类型的参数；参数3：数据列表参数
        JasperPrint jasperPrint = JasperFillManager.fillReport(in,new HashMap<>(), dataSource);
        //3.以pdf形式输出
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 6.导出饼状图
     */
    @RequestMapping("/exportPdf6")
    public void  exportPdf6() throws JRException, IOException, SQLException {
        //1.加载jasper文件，获取文件流
        InputStream in = session.getServletContext().getResourceAsStream("/jasper/07_char.jasper");

        //2.创建jasperPrint对象
        //获取连接对象
        List list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Map map = new HashMap<>();
            map.put("title","标题"+i);
            map.put("value",new Random().nextInt(100));
            list.add(map);
        }


        //将lsit构造成jrdataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);


        //参数1:模板文件输入流；参数2：传递到模板文件中的key-value类型的参数；参数3：数据列表参数
        JasperPrint jasperPrint = JasperFillManager.fillReport(in,new HashMap<>(), dataSource);
        //3.以pdf形式输出
        JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }

    /**
     * 6.导出饼状图
     */
    @RequestMapping("/exportPdf7")
    public void exportPdf7(String id) throws Exception {
        //1. 加载对jrxml模板文件编译后的test01.jasper文件
        InputStream in =
                session.getServletContext()
                        .getResourceAsStream("/jasper/export.jasper");

        // 准备数据 start
        // A. 根据报运单id，查询报运单对象
        Export export = exportService.findById(id);

        // B. 把报运单对象数据，封装到map集合中，作为jasper模板的map参数
        // map的key要与jasper模板中的parameter参数一致。
        Map<String, Object> map = BeanMapUtils.beanToMap(export);

        // C. 查询报运的商品，获取商品的list集合。（作为jasper的列表数据）
        ExportProductExample example = new ExportProductExample();
        example.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> list = exportProductService.findAll(example);

        // 准备数据 end


        // 构造javabean数据源对象。
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

        //2. 通过JRPrint对象填充模板中的数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in,map,dataSource);

        //3. 导出pdf

        // 设置响应的格式与编码
        response.setContentType("application/pdf;charset=UTF-8");
        // 设置下载响应头
        response.setHeader("content-disposition","attachment;filename=export.pdf");
        // 获取response输出流
        ServletOutputStream out = response.getOutputStream();
        JasperExportManager
                .exportReportToPdfStream(jasperPrint, out);
        out.close();
    }
}
