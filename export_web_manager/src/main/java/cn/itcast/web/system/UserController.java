package cn.itcast.web.system;


import cn.itcast.domain.system.Dept;
import cn.itcast.domain.system.Role;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.RoleService;
import cn.itcast.service.system.UserService;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private RoleService roleService;

    //用户列表分页
    @RequestMapping("/list")
    @RequiresPermissions("用户管理")
    public String list(
            @RequestParam(defaultValue = "1")int pageNum,
            @RequestParam(defaultValue = "5")int pageSize
    ){
        /*//获取subject对象
        Subject subject = SecurityUtils.getSubject();
        //权限校验（访问当前用户列表，需要“用户管理”）
        subject.checkPermission("用户管理");*/

        PageInfo<User> pageInfo = userService.findByPage(getLoginCompanyId(), pageNum, pageSize);
        //将用户列表存到request中
        request.setAttribute("pageInfo",pageInfo);
        //跳转对象到页面
        return "system/user/user-list";
    }

    /**
     * 进入用户新增页面
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        String companyId=getLoginCompanyId();
        //查询所有部门
        List<Dept> deptList = deptService.findAll(companyId);
        //存入到request域中
        request.setAttribute("deptList",deptList);
        return "system/user/user-add";
    }

    /**
     * 添加用户和更新
     */
    //注入RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @RequestMapping("/edit")
    public String edit(User user){
        String companyId=getLoginCompanyId();
        String companyName=getLoginCompanyName();

        user.setCompanyId(companyId);
        user.setCompanyName(companyName);

        //判断是否具有id属性
        if (StringUtils.isEmpty(user.getId())){
            userService.save(user);

            //发送消息队列
            Map<String, Object> map = new HashMap<>();
            map.put("email",user.getEmail());
            map.put("subject","Hello");
            map.put("content","你个大傻子");
            rabbitTemplate.convertAndSend("msg.email",map);


        }else{
            userService.update(user);
        }
        return "redirect:/system/user/list.do";
    }
    /**
     * 进入修改界面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView update(String id){
        User user = userService.findById(id);
        String companyId=getLoginCompanyId();

        List<Dept> deptList = deptService.findAll(companyId);

        ModelAndView mv = new ModelAndView();

        mv.addObject("deptList",deptList);
        mv.addObject("user",user);
        mv.setViewName("system/user/user-update");
        return mv;
    }

    /**
     * 删除用户
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String,String> delete(String id){

        Map<String, String> map = new HashMap<>();

        boolean flag=userService.delete(id);
        if (flag){
            map.put("message","删除成功");
        }else{
            map.put("message","删除失败，当前部门被引用，不能删除");
        }
        return map;
    }

    /**
     * 进入用户“角色”页面
     */
    @RequestMapping("/roleList")
    public String roleList(String id){
        //根据id查询用户
        User user = userService.findById(id);

        //查询所有角色列表
        List<Role> roleList= roleService.findAll(getLoginCompanyId());

        //根据用户id查询用户已经具有所有角色的集合
        List<Role> userRoles = roleService.findUserRole(id);

        //保存角色字符串，1，2，3
        String userRoleStr = "";
        for (Role userRole : userRoles) {
            userRoleStr += userRole.getId()+ ",";
        }
        //保存数据
        request.setAttribute("user",user);
        request.setAttribute("roleList",roleList);
        request.setAttribute("userRoleStr",userRoleStr);

        return "system/user/user-role";
    }

    /**
     * 保存用户角色
     */
    @RequestMapping("/changeRole")
    public String changeRole(String userId,String[] roleIds){
        //调用service分配权限
        userService.updateUserRoles(userId,roleIds);

        //跳转到list页面
        return "redirect:/system/user/list.do";
    }
}
