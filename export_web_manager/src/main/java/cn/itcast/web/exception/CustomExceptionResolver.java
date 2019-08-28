package cn.itcast.web.exception;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义异常处理类。
 * 作用： 当控制器方法出现异常自动去容器中找HandlerExceptionResolver类型的对象，处理异常。
 */
@Component  // 只需要创建对象，加入容器
public class CustomExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler, Exception ex) {
        // 打印错误信息到控制台
        ex.printStackTrace();
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("errorMsg","对不起，系统忙稍后再试！ 或联系管理员：120");
        mv.setViewName("error");

        return mv;
    }
}
