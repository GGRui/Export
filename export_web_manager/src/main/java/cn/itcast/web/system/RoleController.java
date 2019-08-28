package cn.itcast.web.system;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.RoleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/role")
public class RoleController extends BaseController{

    @Autowired
    private RoleService roleService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private ModuleService moduleService;

    /**
     * 角色表分页
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ){
        String companyId=getLoginCompanyId();

        PageInfo<Role> pageInfo = roleService.findByPage(companyId, pageNum, pageSize);

        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/role/role-list");
        return mv;
    }

    /**
     * 进入角色新增页面
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "system/role/role-add";
    }

    /**
     * 添加或保存
     */
    @RequestMapping("/edit")
    public String edit(Role role){
        String companyId=getLoginCompanyId();
        String companyName=getLoginCompanyName();

        role.setCompanyId(companyId);
        role.setCompanyName(companyName);

        if (StringUtils.isEmpty(role.getId())){
            roleService.save(role);
        }else {
            roleService.update(role);
        }
        return "redirect:/system/role/list.do";
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public String delete(String id){
        roleService.delete(id);
        return "redirect:/system/role/list.do";
    }

    /**
     * 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView update(String id){
        Role role = roleService.findById(id);

        ModelAndView mv = new ModelAndView();
        mv.addObject("role",role);
        mv.setViewName("system/role/role-update");
        return mv;
    }

    /**
     * 角色分配权限。（2）role-module.jsp页面发送异步请求
     * A:返回所有权限的json字符串
     * B:角色已经具有的权限要默认选中
     * C:返回的json格式参考ztree
     *  如：[{id:2, pId:0, name:"随意勾选 2", checked:true, open:true}]
     */
    @RequestMapping("/getZtreeNodes")
    @ResponseBody
    public List<Map<String,Object>> getZtreeNodes(String roleId){
        //查询所有模块
        List<Module> moduleList = moduleService.findAll();
        //根据角色id查询具有的权限信息
        List<Module> roleModules = moduleService.findModuleByRoleId(roleId);

        List<Map<String,Object>> list = new ArrayList<>();
        for (Module module : moduleList) {
            Map<String, Object> map = new HashMap<>();
            //添加map数据
            map.put("id",module.getId());       //模块id
            map.put("pId",module.getParentId());//父模块id
            map.put("name",module.getName());//模块名称

            if (roleModules.contains(module)){
                map.put("checked",true); //默认勾选
            }
            //存到list集合中去
            list.add(map);

        }
        //返回list
        return list;
    }

    /**
     * 进入权限页面
     */
    @RequestMapping("/roleModule")
    public String roleModule(String roleId){
        Role role = roleService.findById(roleId);

        request.setAttribute("role",role);
        return "system/role/role-module";
    }




    /**
     * 角色分配权限
     */
    @RequestMapping("/updateRoleModule")
    public String updateRoleModule(String roleId,String moduleIds){
        //调用Service分配权限
        roleService.updateRoleModule(roleId,moduleIds);
        //跳转到角色列表
        return "redirect:/system/role/list.do";
    }
}
