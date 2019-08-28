package cn.itcast.service.system;

import cn.itcast.domain.system.Dept;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/applicationContext-*.xml")
public class DeptServiceTest {
    //注入service
    @Autowired
    private DeptService deptService;

    @Test
    public void findByPage(){
        String companyId="1";
        int pageNum=1;
        int pageSize=5;
        PageInfo<Dept> pageInfo = deptService.findByPage(companyId, pageNum, pageSize);
        System.out.println();
    }
}
