package fun.linyuhong.myCommunity.controller.portal;

import fun.linyuhong.myCommunity.async.EventModel;
import fun.linyuhong.myCommunity.async.EventProducer;
import fun.linyuhong.myCommunity.async.EventType;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.common.Page;
import fun.linyuhong.myCommunity.service.IFollowService;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.JSONUtil;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * @author linyuhong
 * @date 2019/9/5
 */
@Controller
public class FollowController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IFollowService iFollowService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private EventProducer eventProducer;


    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {

        UserVo user = hostHolder.getUser();
        int userId = XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys);
        // 解密
        entityId = XORUtil.encryptId(entityId, Const.getIdEncodeKeys.userIdKeys);
        iFollowService.follow(userId, entityType, entityId);


        /**
         * 触发关注事件
         */
        EventModel eventModel = new EventModel(EventType.FOLLOW)
                .setActorId(userId)
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(eventModel);


        return JSONUtil.getJSONString(0, "已关注");
    }


    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {

        UserVo user = hostHolder.getUser();
        int userId = XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys);

        // 解密
        entityId = XORUtil.encryptId(entityId, Const.getIdEncodeKeys.userIdKeys);
        iFollowService.unfollow(userId, entityType, entityId);

        return JSONUtil.getJSONString(0, "已取消关注");
    }


    /**
     * 查看用户关注列表
     * @param userId  被查看的用户
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){

        UserVo user = iUserService.findUserById(XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys));
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        // 解密Id  注意不要放在上面  上面的 userId 不解密
        userId = XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys);
        Long count = iFollowService.findFolloweeCount(userId, Const.follow.ENTITY_TYPE_USER);
        page.setRows(count.intValue());

        List<Map<String, Object>> userList = iFollowService.findFollowee(userId, Const.follow.ENTITY_TYPE_USER, page.getOffset(), page.getLimit());
        if (!userList.isEmpty()) {
            for (Map<String, Object> map : userList) {
                UserVo u = (UserVo) map.get("user");
                // 查看者是否有关注列表里的这个用户
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/followee";
    }

    /**
     * 查看用户粉丝列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {

        UserVo user = iUserService.findUserById(XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys));
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        userId = XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys);
        Long count = iFollowService.findFollowerCount(Const.follow.ENTITY_TYPE_USER, userId);
        page.setRows(count.intValue());

        List<Map<String, Object>> userList = iFollowService.findFollowers(Const.follow.ENTITY_TYPE_USER, userId, page.getOffset(), page.getLimit());
        if (!userList.isEmpty()) {
            for (Map<String, Object> map : userList) {
                UserVo u = (UserVo) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/follower";
    }

    /**
     * 查看当前用户是否关注此对象
     * @param userId 对象
     * @return
     */
    private boolean hasFollowed(int userId) {

        UserVo user = hostHolder.getUser();
        // 当前用户没登录
        if (user == null) {
            return false;
        }
        // 当前登录用户Id
        int userVoId = XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys);
        // 关注的对象
        userId = XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys);
        return iFollowService.hasFollowed(userVoId, Const.follow.ENTITY_TYPE_USER, userId);
    }

}








