package cn.itcast.web.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import com.github.pagehelper.PageInfo;
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
@RequestMapping("/system/dept")
public class DeptController extends BaseController{

    @Autowired
    private DeptService deptService;

    /**
     * 部门分页
     */
    @RequestMapping("list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ){

        String companyId=getLoginCompanyId();
        PageInfo<Dept> pageInfo = deptService.findByPage(companyId, pageNum, pageSize);

        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/dept/dept-list");
        return mv;
    }

    @RequestMapping("/toAdd")
    public ModelAndView toAdd(){

        //先设定用户登录的所属企业的id是1
        String companyId=getLoginCompanyId();

       List<Dept> deptList = deptService.findAll(companyId);

        ModelAndView mv = new ModelAndView();
        mv.addObject("deptList",deptList);
        mv.setViewName("system/dept/dept-add");
        return mv;
    }

    /**
     *  添加部门
     * @param dept
     * @return
     */
    @RequestMapping(value = "/edit",name = "编辑部门")
    public String edit(Dept dept){
        String companyId=getLoginCompanyId();
        String companyName=getLoginCompanyName();

        dept.setCompanyId(companyId);
        dept.setCompanyName(companyName);

        if (StringUtils.isEmpty(dept.getId())){
            deptService.save(dept);
        }else {
            deptService.update(dept);
        }
        return "redirect:/system/dept/list.do";
    }

    /**
     * 进入修改页面
     * @param id
     * @return
     */
    @RequestMapping("toUpdate")
    public ModelAndView toUpdate(String id){

        String companyId=getLoginCompanyId();

        //根据id查询
        Dept dept = deptService.findById(id);
        //查询所有部门
        List<Dept> deptList= deptService.findAll(companyId);

        //返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("deptList",deptList);
        mv.addObject("dept",dept);
        mv.setViewName("system/dept/dept-update");
        return mv;
    }

    /**
     * 删除部门
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String,String> delete(String id){
        boolean flag = deptService.delete(id);

        Map<String, String> result = new HashMap<>();
        if (flag){
            result.put("message","删除成功！");
        }else{
            result.put("message","删除失败！由于当前的部门有被引用，不能进行删除操作");
        }
        return result;
    }

}
