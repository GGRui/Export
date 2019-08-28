package cn.itcast.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class ContractProvided {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext cp = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
        cp.start();
        System.in.read();
    }
}
