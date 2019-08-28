package cn.itcast.web.shiro;

import cn.itcast.domain.system.User;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;

public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //1.获取登录时用户输入的用户名
        String username = (String) token.getPrincipal();
        //2.获取密码
        String password = new String((char[]) token.getCredentials());
        //3.对用户输入的密码进行加密，用户名当盐添加
        String md5Password = new Md5Hash(password, username).toString();

        //4.获取认证后的密码，及数据库中的密码
        String dpPassword = (String) info.getCredentials();
        //对比是否相同
        return md5Password.equals(dpPassword);
    }
}
