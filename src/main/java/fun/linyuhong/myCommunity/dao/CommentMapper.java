package fun.linyuhong.myCommunity.dao;

import fun.linyuhong.myCommunity.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
//    int deleteByPrimaryKey(Integer id);
//
//    int insert(Comment record);
//
//    int insertSelective(Comment record);
//
//    Comment selectByPrimaryKey(Integer id);
//
//    int updateByPrimaryKeySelective(Comment record);
//
//    int updateByPrimaryKeyWithBLOBs(Comment record);
//
//    int updateByPrimaryKey(Comment record);

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}