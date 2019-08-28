package cn.itcast.web.shiro;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 自定义reamlm
 */
public class AuthRealm extends AuthorizingRealm{
    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;

    //登录认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //获取到用户界面输入的邮箱地址和密码
        String email = (String) token.getPrincipal();
        //根据email查询
        User user = userService.findByEmail(email);
        //判断
        if (user == null){
            //一旦认证方法返回null，就是UnknownAccountException异常
            return null;
        }
        //获取数据库中正确密码
        String password = user.getPassword();

        //返回
        SimpleAuthenticationInfo sai = new SimpleAuthenticationInfo(user, password, this.getName());

        return sai;
    }

    //授权访问校验
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取认证后的身份对象，（realm认证方法返回对象的构造函数的第一个参数
        User user = (User) principals.getPrimaryPrincipal();
        //根据用户id查询用户的权限
        List<Module> moduleList = moduleService.findModuleByUserId(user.getId());
        //返回
        SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo();
        //遍历用户的权限集合
        if (moduleList != null && moduleList.size()>0){
            for (Module module : moduleList) {
                //返回用户的权限
                sai.addStringPermission(module.getName());
            }
        }

        return sai;
    }
}
