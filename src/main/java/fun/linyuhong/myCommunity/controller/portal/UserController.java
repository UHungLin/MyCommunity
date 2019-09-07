package fun.linyuhong.myCommunity.controller.portal;


import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.common.Page;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.service.IFollowService;
import fun.linyuhong.myCommunity.service.ILikeService;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ILikeService iLikeService;

    @Autowired
    private IFollowService iFollowService;

    @Autowired
    private IDiscussPostService iDiscussPostService;

    // 个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {

        userId = XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys);
        UserVo user = iUserService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        model.addAttribute("user", user);
        // 获得的赞的数量
        int likeCount = iLikeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        // 关注数量
        long followeeCount = iFollowService.findFolloweeCount(userId, Const.follow.ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = iFollowService.findFollowerCount(Const.follow.ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = iFollowService.hasFollowed(XORUtil.encryptId(hostHolder.getUser().getId(), Const.getIdEncodeKeys.userIdKeys),
                    Const.follow.ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }


    // 发布过的帖子
    @RequestMapping(path = "/profile/discuss/{userId}", method = RequestMethod.GET)
    public String getPost(@PathVariable("userId") int userId, Page page, Model model) {

        UserVo user = iUserService.findUserById(XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/user/profile/discuss/" + userId);
        // 解密
        userId = XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys);
        int discussPostCounts = iDiscussPostService.findDiscussPostRows(userId);
        page.setRows(discussPostCounts);
        model.addAttribute("discussPostCounts", discussPostCounts);

        List<Map<String, Object>> list = iDiscussPostService.selectDiscussPosts(userId, 0, page.getOffset(), page.getLimit());
        model.addAttribute("discussPost", list);

        return "/site/my-post";
    }



}





