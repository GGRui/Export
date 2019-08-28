package cn.itcast.web.system;

import cn.itcast.domain.system.User;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BaseController {

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected HttpSession session;

    //获取企业id
    protected String getLoginCompanyId(){
        return getLoginUser().getCompanyId();

    }

    //获取企业名称
    protected String getLoginCompanyName(){
        return getLoginUser().getCompanyName();
    }

    //从session中获取登录用户的对象
    protected User getLoginUser(){
        return (User) session.getAttribute("loginUser");
    }
}
