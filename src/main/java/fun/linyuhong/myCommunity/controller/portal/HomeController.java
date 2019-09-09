package fun.linyuhong.myCommunity.controller.portal;


import com.github.pagehelper.PageInfo;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.common.Page;
import fun.linyuhong.myCommunity.dao.UserMapper;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.service.ICommentService;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.service.ILikeService;
import fun.linyuhong.myCommunity.service.IMessageService;
import fun.linyuhong.myCommunity.service.Impl.DiscussPostServiceImpl;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
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

    @Autowired
    private IMessageService iMessageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ILikeService iLikeService;



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
        List<DiscussPost> discussPostList = discussPostService.selectDiscussPosts(0, orderMode, page.getOffset(), page.getLimit());
        List<Map<String, Object>> list = new ArrayList<>();


        /**
         * 这里写成这样的原因是加了缓存 Caffeine 后，里面缓存了热门的帖子
         * 但是缓存的数据是 id 加密后的，不是直接缓存数据库里取出来的数据，我也不知道为什么会这样
         * 所以这里要判断下数据是从缓存里取出来的还是数据库，如果是缓存里取出来的，id 就不用加密了
         */
        for (DiscussPost post : discussPostList) {
            Map<String, Object> map = new HashMap();
            if (post.getId() > 10000000) {
                UserVo userVo = assembleUserVo(userMapper.selectByPrimaryKey(XORUtil.encryptId(post.getUserId(), Const.getIdEncodeKeys.userIdKeys)));
                map.put("user", userVo);
                long likeCount = iLikeService.findEntityLikeCount(Const.like.ENTITY_TYPE_POST, XORUtil.encryptId(post.getId(), Const.getIdEncodeKeys.postIdKeys));
                map.put("likeCount", likeCount);
                post.setId(post.getId());
                post.setUserId(post.getUserId());
                map.put("post", post);
                list.add(map);
            } else {
                UserVo userVo = assembleUserVo(userMapper.selectByPrimaryKey(post.getUserId()));
                map.put("user", userVo);
                long likeCount = iLikeService.findEntityLikeCount(Const.like.ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                post.setId(XORUtil.encryptId(post.getId(), Const.getIdEncodeKeys.postIdKeys));
                post.setUserId(XORUtil.encryptId(post.getUserId(), Const.getIdEncodeKeys.userIdKeys));
                map.put("post", post);
                list.add(map);
            }
        }

        model.addAttribute("discussPosts", list);
        model.addAttribute("orderMode", orderMode);

        return "/index";
    }

    private UserVo assembleUserVo(User user) {
        UserVo userVo = new UserVo();
        userVo.setId(XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys));
        userVo.setUsername(user.getUsername());
        userVo.setEmail(user.getEmail());
        userVo.setHeaderUrl(user.getHeaderUrl());
        userVo.setType(user.getType());
        userVo.setCreateTime(user.getCreateTime());
        return userVo;
    }



    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }


    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }

}

