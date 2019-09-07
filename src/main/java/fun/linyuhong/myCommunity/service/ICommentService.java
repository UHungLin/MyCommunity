package fun.linyuhong.myCommunity.service;

import fun.linyuhong.myCommunity.entity.Comment;

import java.util.List;

/**
 * @author linyuhong
 * @date 2019/9/3
 */
public interface ICommentService {

    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    int findCommentCount(int entityType, int entityId);

    int addComment(Comment comment);

    Comment findCommentById(int id);

}
