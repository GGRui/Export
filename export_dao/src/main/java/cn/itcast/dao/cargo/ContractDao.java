package cn.itcast.dao.cargo;

import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;

import java.util.List;

public interface ContractDao {
    //条件查询
    List<Contract> selectByExample(ContractExample example);

    //id查询
    Contract selectByPrimaryKey(String id);

	//保存
    int insertSelective(Contract record);

    //更新
    int updateByPrimaryKeySelective(Contract record);

    //删除
    int deleteByPrimaryKey(String id);

    //根据部门id查询当前部门及所有子部门登录的用户创建购销合同
    List<Contract> selectByDeptId(String deptId);
}