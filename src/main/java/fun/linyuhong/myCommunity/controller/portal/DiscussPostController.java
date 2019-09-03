package fun.linyuhong.myCommunity.controller.portal;

import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.common.Page;
import fun.linyuhong.myCommunity.dao.DiscussPostMapper;
import fun.linyuhong.myCommunity.entity.Comment;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.service.ICommentService;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.service.Impl.DiscussPostServiceImpl;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.JSONUtil;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.hibernate.validator.constraints.EAN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.model.IComment;

import java.util.*;

/**
 * @author linyuhong
 * @date 2019/9/3
 */
@Controller
@RequestMapping(value = "/discuss")
public class DiscussPostController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IDiscussPostService iDiscussPostService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICommentService iCommentService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(@RequestParam(value = "title") String title, @RequestParam(value = "content") String content) {
        // 是否登录
        UserVo user = hostHolder.getUser();
        if (user == null) {
            return JSONUtil.getJSONString(403, "你还没有登录");
        }

        // 构建post帖子对象
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys));
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        iDiscussPostService.addDiscussPost(discussPost);

        return JSONUtil.getJSONString(0, "发帖成功");
    }

    @RequestMapping(value = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") Integer discussPostId, Page page, Model model) {

        // 获取帖子
        DiscussPost discussPost = iDiscussPostService.getDiscussPost(discussPostId);
        model.addAttribute("post", discussPost);
        // 获取帖子作者
        UserVo user = iUserService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        // 设置分页
        page.setPath("/discuss/detail/" + discussPostId);
        page.setLimit(5);
        page.setRows(discussPost.getCommentCount());

        // 显示评论
        // postId 解密，写在这里而不写在service层是因为 评论的评论的id 我不想加密
        int postId = XORUtil.encryptId(discussPost.getId(), Const.getIdEncodeKeys.postIdKeys);
        List<Comment> commentList = iCommentService.selectCommentByEntity(Const.entityType.ENTITY_TYPE_POST, postId,
                                                page.getOffset(), page.getLimit());

        // 评论Vo列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                UserVo userVo = iUserService.findUserById(comment.getUserId());
                commentVo.put("user", userVo);
                comment.setUserId(XORUtil.encryptId(comment.getUserId(), Const.getIdEncodeKeys.userIdKeys));
                comment.setEntityId(XORUtil.encryptId(comment.getEntityId(), Const.getIdEncodeKeys.postIdKeys));
                commentVo.put("comment", comment);

                // 查找评论的评论 不分页
                List<Comment> replyList = iCommentService.selectCommentByEntity(Const.entityType.ENTITY_TYPE_COMMENT,
                                comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 此条评论的作者
                        replyVo.put("user", iUserService.findUserById(reply.getUserId()));
                        // 回复目标
                        // 注意 UserVo 中的id已加密
                        UserVo target = reply.getTargetId() == 0 ? null : iUserService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        // 注意 replyVo.put("reply", reply) 放到最后操作，先查出需要的对象，在对id加密
                        reply.setUserId(XORUtil.encryptId(reply.getUserId(), Const.getIdEncodeKeys.userIdKeys));
                        reply.setTargetId(reply.getTargetId() == 0 ? 0 : XORUtil.encryptId(reply.getTargetId(), Const.getIdEncodeKeys.userIdKeys));
                        replyVo.put("reply", reply);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = iCommentService.findCommentCount(Const.entityType.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }


}










