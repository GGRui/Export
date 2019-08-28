package cn.itcast.service.system.impl;

import cn.itcast.dao.system.SysLogDao;
import cn.itcast.domain.system.SysLog;
import cn.itcast.service.system.SysLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogDao sysLogDao;

    @Override
    public PageInfo<SysLog> findByPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);

        //1.调用startPage方法
        PageHelper.startPage(pageNum,pageSize);
        //2.查询全部列表
        List<SysLog> list = sysLogDao.findAll(companyId);
        //3. 构造pagebean
        return new PageInfo<SysLog>(list);
    }

    @Override
    public void save(SysLog log) {

        sysLogDao.save(log);

    }
}
