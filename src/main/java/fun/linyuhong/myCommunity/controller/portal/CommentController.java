package fun.linyuhong.myCommunity.controller.portal;

import fun.linyuhong.myCommunity.async.EventModel;
import fun.linyuhong.myCommunity.async.EventProducer;
import fun.linyuhong.myCommunity.async.EventType;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.entity.Comment;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.service.ICommentService;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.XORUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * @author linyuhong
 * @date 2019/9/3
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private ICommentService iCommentService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private IDiscussPostService iDiscussPostService;


    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") Integer discussPostId, Comment comment) {

        if (StringUtils.isBlank(comment.getContent())) {
            throw new IllegalArgumentException("评论内容不能为空!");
        }
        // 解密 id
        comment.setUserId(XORUtil.encryptId(hostHolder.getUser().getId(), Const.getIdEncodeKeys.userIdKeys));
        // 根据传过来的 entityType 判断评论的类型，进行相应的 id 解密
        Integer entityType = comment.getEntityType();
        if (entityType == Const.entityType.ENTITY_TYPE_POST) {
            comment.setEntityId(XORUtil.encryptId(comment.getEntityId(), Const.getIdEncodeKeys.postIdKeys));
        } else {
//            comment.setEntityId(XORUtil.encryptId(comment.getEntityId(), Const.getIdEncodeKeys.userIdKeys));
            comment.setEntityId(comment.getEntityId());
            // 判断是否是 回复 某人，是的话解密其 id
            comment.setTargetId(comment.getTargetId() == 0 ? 0 : XORUtil.encryptId(comment.getTargetId(), Const.getIdEncodeKeys.userIdKeys));
        }

        iCommentService.addComment(comment);

        /**
         * 触发评论事件
         */
        EventModel eventModel = new EventModel(EventType.COMMENT)
                .setActorId(XORUtil.encryptId(hostHolder.getUser().getId(), Const.getIdEncodeKeys.userIdKeys))
                .setEntityType(entityType)
                .setEntityId(comment.getEntityId())
                .setData("postId", XORUtil.encryptId(discussPostId, Const.getIdEncodeKeys.postIdKeys));  // postId 为帖子Id，点击查看详情时用到 对于点赞对象为 comment 时有用
        if (comment.getEntityType() == Const.entityType.ENTITY_TYPE_POST) {
            DiscussPost target = iDiscussPostService.findDiscussPostById(comment.getEntityId());
            eventModel.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == Const.entityType.ENTITY_TYPE_COMMENT) {
            Comment target = iCommentService.findCommentById(comment.getEntityId());
            eventModel.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(eventModel);

        return "redirect:/discuss/detail/" + discussPostId;
    }

}






