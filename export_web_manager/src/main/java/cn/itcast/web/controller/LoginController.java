package cn.itcast.web.controller;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import cn.itcast.web.system.BaseController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class LoginController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;

    /**
     * 登录
     */
    /*@RequestMapping("/login")
    public String login(String email,String password){
        //1。判断用户输入的数据
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
            return "forward:/login.jsp";
        }
        //2.通过邮箱账号查询用户对象
        User user = userService.findByEmail(email);

        //3.比较查询到的用户对象输入的密码是否一致
        if (user != null && user.getPassword().equals(password)){
            //4.登录成功：保存用户数据到session中
            session.setAttribute("loginUser",user);

            *//*获取当前用户的所有权限*//*
            List<Module> modules = moduleService.findModuleByUserId(user.getId());
            session.setAttribute("modules",modules);

            return "home/main";
        }else{
            //5.登录失败，跳转到登录页面
            request.setAttribute("error","用户名或密码错误");
            return "forward:/login.jsp";
        }
    }*/

    /**
     * 通过shiro进行登录验证
     */
    @RequestMapping("/login")
    public String login(String email,String password){
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
            // 跳转到登陆页面
            return "forward:/login.jsp";
        }

        try {
            //获取subject
            Subject subject = SecurityUtils.getSubject();
            //构造用户名和密码
            UsernamePasswordToken uptoken = new UsernamePasswordToken(email, password);
            //借助subjec完成用户登录
            subject.login(uptoken);
            //通过shiro获取用户对象，保存到session中
            User user = (User) subject.getPrincipal();      //获取安全数据（用户对象）
            session.setAttribute("loginUser",user);
            //获取菜单数据
            List<Module> modules = moduleService.findModuleByUserId(user.getId());
            session.setAttribute("modules",modules);
            //登录成功跳转页面
            return "home/main";
        } catch (Exception e) {
            e.printStackTrace();
            //登录失败跳转到失败页面
            request.setAttribute("error","用户名或密码错误");
            return "forward:/login.jsp";
        }

    }


    /**
     * 添加home方法进行跳转
     */
    @RequestMapping("/home")
    public String home(){
        return "home/home";
    }

    /**
     * 注销
     */
    @RequestMapping("/logout")
    public String logout(){
        //shiro也提供了退出的方法（清除shiro 的认证的信息
        SecurityUtils.getSubject().logout();
        //删除session中用户
        session.removeAttribute("loginUser");
        //销毁session
        session.invalidate();
        return "forward:/login.jsp";
    }
}
