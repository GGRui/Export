package cn.itcast.web.crago;


import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.web.system.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/cargo/contract")
public class ContractController extends BaseController {

    // 注入购销合同的服务接口
    @Reference
    private ContractService contractService;


    /**
     * 1.分页里列表查询
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize
    )
    {
        //构造查询条件
        ContractExample contractExample = new ContractExample();
        //根据create_time进行降序
        contractExample.setOrderByClause("create_time desc");

        //1.2查询条件对象
        ContractExample.Criteria criteria = contractExample.createCriteria();
        //1.3查询条件，企业id
        criteria.andCompanyIdEqualTo(getLoginCompanyId());

        /**
         * 细粒度控制，根据用户的degree等级判断，不同级别显示不同的购销合同数据
         * degree 级别
         * 0-saas管理员
         * 1-企业管理员
         * 2-管理所有下属部门和人员
         * 3-管理本部门
         * 4-普通员工
         */
        User user = getLoginUser();
        if (user.getDegree() == 4){
            //说明是普通员工，只能查询自己创建的购销合同
            criteria.andCreateByEqualTo(user.getId());
        }else if (user.getDegree() == 3){
            //说明是部门经理，可以查看本部门下所有员工创建的购销合同
            criteria.andCreateByEqualTo(user.getDeptId());
        }else if (user.getDegree() == 2){
            PageInfo<Contract> pageInfo = contractService.selectByDeptId(user.getDeptId(),pageNum,pageSize);

            //返回
            ModelAndView mv = new ModelAndView();
            mv.addObject("pageInfo",pageInfo);
            mv.setViewName("cargo/contract/contract-list");
            return mv;
        }


        //1.2 调用service
        PageInfo<Contract> pageInfo = contractService.findByPage(contractExample, pageNum, pageSize);

        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("cargo/contract/contract-list");
        return mv;
    }
    /**
     * 2.添加（1）进入添加页面
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "cargo/contract/contract-add";
    }

    /**
     * 3.添加修改
     */
    @RequestMapping("/edit")
    public String edit(Contract contract){
        contract.setCompanyId(getLoginCompanyId());
        contract.setCompanyName(getLoginCompanyName());

        if (StringUtils.isEmpty(contract.getId())){
            /**
             * 细粒度的权限控制
             */
            //设置创建者
            contract.setCreateBy(getLoginUser().getId());
            // 设置创建者所属部门
            contract.setCreateDept(getLoginUser().getDeptId());

            contractService.save(contract);
        }else {
            contractService.update(contract);
        }
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        Contract contract = contractService.findById(id);
        request.setAttribute("contract",contract);
        return "cargo/contract/contract-update";
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public String delete(String id){
        contractService.delete(id);
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 购销合同查看
     */
    @RequestMapping("toView")
    public String toView(String id){
        //根据id查询
        Contract contract = contractService.findById(id);
       request.setAttribute("contract",contract);
       //跳转到登录页面
        return "cargo/contract/contract-view";
    }

    /**
     * 购销合同（2）提交，将状态由0改为1
     */
    @RequestMapping("/submit")
    public String submit(String id){
        //购销合同对象
        Contract contract = new Contract();
        //设置id
        contract.setId(id);
        //设置状态
        contract.setState(1);
        //更新
        contractService.update(contract);
        //跳转到修改页面
        return "redirect:/cargo/contract/list.do";
    }
    /**
     * 购销合同（3）取消，将状态由1改为0
     */
    @RequestMapping("/cancel")
    public String cancel(String id) {
        //判断
        //1.构造购销合同对象
        Contract contract = new Contract();
        //2.设置id
        contract.setId(id);
        //3.设置状态
        contract.setState(0);
        //4.更新
        contractService.update(contract);
        //跳转到修改界面
        return "redirect:/cargo/contract/list.do";
    }

}
