package fun.linyuhong.myCommunity.controller.portal;


import com.github.pagehelper.PageInfo;
import fun.linyuhong.myCommunity.common.Page;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.service.Impl.DiscussPostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private IDiscussPostService discussPostService;


    @RequestMapping(path = {"/", "/index"})
    public String home(Model model, Page page,
                       @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){
        // 参数传入的时候由SpringMVC初始化
        // 方法调用前,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.

        // 查询总页数
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);

        /**
         * userId == 0 表示查询所有用户帖子
         */
        List<Map<String, Object>> discussPosts = discussPostService.selectDiscussPosts(0, orderMode, page.getOffset(), page.getLimit());

        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode", orderMode);


        return "/index";
    }


}
