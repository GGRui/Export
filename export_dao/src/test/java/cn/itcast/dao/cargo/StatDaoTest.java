package cn.itcast.dao.cargo;

import cn.itcast.dao.stat.StatDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml" )
public class StatDaoTest {
    @Autowired
    private StatDao statDao;

    @Test
    public void testUpdate(){
        List<Map<String,Object>> list = statDao.getFactoryData("1");
        for (Map<String, Object> temp : list) {
            //{name=会龙, value=26201.40}
            System.out.println(temp);
        }
    }

    @Test
    public void getFactoryData(){
        System.out.println(statDao.getFactoryData("1"));
    }
}
