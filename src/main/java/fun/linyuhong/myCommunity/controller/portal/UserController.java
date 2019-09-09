package fun.linyuhong.myCommunity.controller.portal;


import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.common.Page;
import fun.linyuhong.myCommunity.dao.UserMapper;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.service.IFollowService;
import fun.linyuhong.myCommunity.service.ILikeService;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.util.GetGenerateUUID;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.JSONUtil;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    private UserMapper userMapper;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;


    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        // 上传文件名称
        String fileName = GetGenerateUUID.generateUUID();
        // 设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", JSONUtil.getJSONString(0));
        // 生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }


    // 更新头像路径
    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return JSONUtil.getJSONString(1, "文件名不能为空!");
        }

        String url = headerBucketUrl + "/" + fileName;
        iUserService.updateHeader(XORUtil.encryptId(hostHolder.getUser().getId(), Const.getIdEncodeKeys.userIdKeys), url);

        return JSONUtil.getJSONString(0);
    }


    // 个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model, Page page) {

        // 解密
        userId = XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys);
        UserVo user = iUserService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 帖子分页信息
        page.setLimit(5);
        page.setPath("/user/profile/" + XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys));
        int discussPostCounts = iDiscussPostService.findDiscussPostRows(userId);
        page.setRows(discussPostCounts);
        model.addAttribute("discussPostCounts", discussPostCounts);

        List<DiscussPost> discussPostList = iDiscussPostService.selectDiscussPosts(userId, 0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> list = new ArrayList<>();
        // id 加密
        for (DiscussPost post : discussPostList) {
            Map<String, Object> map = new HashMap();
            // 注意先获取 userId，因为 post 那里就要加密了
            UserVo userVo = assembleUserVo(userMapper.selectByPrimaryKey(post.getUserId()));
            map.put("user", userVo);
            long likeCount = iLikeService.findEntityLikeCount(Const.like.ENTITY_TYPE_POST, post.getId());
            map.put("likeCount", likeCount);
            post.setId(XORUtil.encryptId(post.getId(), Const.getIdEncodeKeys.postIdKeys));
            post.setUserId(XORUtil.encryptId(post.getUserId(), Const.getIdEncodeKeys.userIdKeys));
            map.put("post", post);
            list.add(map);
        }

        model.addAttribute("discussPost", list);


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

//        return "/site/profile";
        return "/site/my-page";
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


//    // 个人主页
//    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
//    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
//
//        userId = XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys);
//        UserVo user = iUserService.findUserById(userId);
//        if (user == null) {
//            throw new RuntimeException("用户不存在");
//        }
//
//        model.addAttribute("user", user);
//        // 获得的赞的数量
//        int likeCount = iLikeService.findUserLikeCount(userId);
//        model.addAttribute("likeCount", likeCount);
//        // 关注数量
//        long followeeCount = iFollowService.findFolloweeCount(userId, Const.follow.ENTITY_TYPE_USER);
//        model.addAttribute("followeeCount", followeeCount);
//        // 粉丝数量
//        long followerCount = iFollowService.findFollowerCount(Const.follow.ENTITY_TYPE_USER, userId);
//        model.addAttribute("followerCount", followerCount);
//        // 是否已关注
//        boolean hasFollowed = false;
//        if (hostHolder.getUser() != null) {
//            hasFollowed = iFollowService.hasFollowed(XORUtil.encryptId(hostHolder.getUser().getId(), Const.getIdEncodeKeys.userIdKeys),
//                    Const.follow.ENTITY_TYPE_USER, userId);
//        }
//        model.addAttribute("hasFollowed", hasFollowed);
//
//        return "/site/profile";
//    }



    // 发布过的帖子
//    @RequestMapping(path = "/profile/discuss/{userId}", method = RequestMethod.GET)
//    public String getPost(@PathVariable("userId") int userId, Page page, Model model) {
//
//        UserVo user = iUserService.findUserById(XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys));
//        if (user == null) {
//            throw new RuntimeException("用户不存在");
//        }
//        model.addAttribute("user", user);
//
//        page.setLimit(5);
//        page.setPath("/user/profile/discuss/" + userId);
//        // 解密
//        userId = XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys);
//        int discussPostCounts = iDiscussPostService.findDiscussPostRows(userId);
//        page.setRows(discussPostCounts);
//        model.addAttribute("discussPostCounts", discussPostCounts);
//
//        List<Map<String, Object>> list = iDiscussPostService.selectDiscussPosts(userId, 0, page.getOffset(), page.getLimit());
//        model.addAttribute("discussPost", list);
//
//        return "/site/my-post";
//    }



}





