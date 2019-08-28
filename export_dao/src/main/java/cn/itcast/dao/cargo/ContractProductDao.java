package cn.itcast.dao.cargo;

import cn.itcast.domain.cargo.ContractProduct;
import cn.itcast.domain.cargo.ContractProductExample;
import cn.itcast.vo.ContractProductVo;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface ContractProductDao {
	//条件查询
    List<ContractProduct> selectByExample(ContractProductExample example);

	//id查询
    ContractProduct selectByPrimaryKey(String id);

	//更新
    int updateByPrimaryKeySelective(ContractProduct record);

    //保存
    int insertSelective(ContractProduct record);

    //删除
    int deleteByPrimaryKey(String id);

    // 出货表统计，根据船期查询
    List<ContractProductVo> findByShipTime(@Param("companyId")String companyId, @Param("inputDate")String inputDate);
}