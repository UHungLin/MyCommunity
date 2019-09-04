package fun.linyuhong.myCommunity.service.Impl;

import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.dao.CommentMapper;
import fun.linyuhong.myCommunity.dao.DiscussPostMapper;
import fun.linyuhong.myCommunity.entity.Comment;
import fun.linyuhong.myCommunity.service.ICommentService;
import fun.linyuhong.myCommunity.util.SensitiveFilter;
import fun.linyuhong.myCommunity.util.XORUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

/**
 * @author linyuhong
 * @date 2019/9/3
 */
@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Override
    public List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit) {
        // 帖子的评论
        List<Comment> commentList = commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
        return commentList;
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        int rows = commentMapper.insertComment(comment);

        // 更新帖子的评论数
        if (comment.getEntityType() == Const.entityType.ENTITY_TYPE_POST) {
            int count  = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }
}










