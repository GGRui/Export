package cn.itcast.dao.cargo;

import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-*.xml")
public class CargoDaoTest {
    @Autowired
    private FactoryDao factoryDao;

    @Test
    public void findByFactoryName() {
        FactoryExample example = new FactoryExample();
        FactoryExample.Criteria criteria = example.createCriteria();
        criteria.andFactoryNameEqualTo("升华");
        List<Factory> list = factoryDao.selectByExample(example);
        System.out.println(list);
        System.out.println(list.size());
    }
    @Test
    public void findById(){
        System.out.println(factoryDao.selectByPrimaryKey("1"));
    }

    /**
     * update co_factory SET ctype = ?, full_name = ?, factory_name = ?,
     * state = ?, create_by = ?, create_time = ?, update_time = ? where id = ?
     */
    @Test
    public void update(){
        Factory factory = new Factory();
        factory.setId("4028817a3ae2ac42013ae3550357000d");
        factory.setCtype("附件");
        factory.setFactoryName("福耀玻璃厂");
        factory.setFullName("福耀");
        factory.setCreateTime(new Date());
        factory.setState(1);
        factory.setUpdateTime(new Date());
        factory.setCreateBy("GRui");
        factoryDao.updateByPrimaryKeySelective(factory);
    }

    /**
     * update co_factory SET state = ?, create_time = ?, update_time = ? where id = ?
     */
    @Test
    public void updateByPrimaryKeySelective(){
        Factory factory = new Factory();
        factory.setId("4028817a3ae2ac42013ae3550357000d");
        factory.setCreateTime(new Date());
        factory.setUpdateTime(new Date());
        factory.setState(1);
        factoryDao.updateByPrimaryKeySelective(factory);

    }

}
