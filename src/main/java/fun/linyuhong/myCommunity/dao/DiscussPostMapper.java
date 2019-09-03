package fun.linyuhong.myCommunity.dao;

import fun.linyuhong.myCommunity.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface DiscussPostMapper {

    // orderMode 默认值是0 按原先模式排，为1表示按热度排
    List<DiscussPost> selectDiscussPosts(int userId, int orderMode, int offset, int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    int updateCommentCount(int id, int commentCount);

    DiscussPost selectDiscussPostById(int id);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);

}