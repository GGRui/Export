package cn.itcast;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;

public class Shiro {
    @Test
    public void md5(){
        // 明文    算法     密文
        //  1      md5     c4ca4238a0b923820dcc509a6f75849b
        System.out.println(new Md5Hash("1").toString());
    }

    //加盐
    @Test
    public void md5Salt(){
        //用户
        String username="zhangsan@export.com";
        //密码
        String password = "1";
        //参数1：密码。参数2：盐。把用户当作盐
        Md5Hash endcodePassword = new Md5Hash(password, username);
        System.out.println("加盐后的味道:"+ endcodePassword);

    }
}
