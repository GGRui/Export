package cn.itcast.web.system;

import cn.itcast.domain.system.Module;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.ModuleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/system/module")
public class ModuleController  extends BaseController{

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private DeptService deptService;

    /**
     * 进入列表页面
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ){
        PageInfo<Module> pageInfo = moduleService.findByPage(pageNum,pageSize);
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/module/module-list");
        return mv;
    }

    /**
     * 进入添加页面
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(){
        //查询数据，为下拉框提供数据
        List<Module> list = moduleService.findAll();
        ModelAndView mv = new ModelAndView();

        mv.addObject("menus",list);
        mv.setViewName("system/module/module-add");
        return mv;
    }

    /**
     * 添加和修改
     */
    @RequestMapping("/edit")
    public String edit(Module module){
        if (StringUtils.isEmpty(module.getId())){
            moduleService.save(module);
        }else {
            moduleService.update(module);
        }
        return "redirect:/system/module/list.do";
    }

    /**
     * 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        //下拉框
        List<Module> list = moduleService.findAll();
        //获得数据
        Module module = moduleService.findById(id);

        ModelAndView mv = new ModelAndView();
        mv.setViewName("/system/module/module-update");
        mv.addObject("menus",list);
        mv.addObject("module",module);

        return mv;
    }

    /**
     * 删除模块
     */
    @RequestMapping("delete")
    public String delete(String id){
        moduleService.delete(id);
        return "redirect:/system/module/list.do";
    }
}
