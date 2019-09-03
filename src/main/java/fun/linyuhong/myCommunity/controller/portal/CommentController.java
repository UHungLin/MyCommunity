package fun.linyuhong.myCommunity.controller.portal;

import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.entity.Comment;
import fun.linyuhong.myCommunity.service.ICommentService;
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

        return "redirect:/discuss/detail/" + discussPostId;
    }

}






