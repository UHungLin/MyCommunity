package fun.linyuhong.myCommunity.dao;

import fun.linyuhong.myCommunity.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
//    int deleteByPrimaryKey(Integer id);
//
//    int insert(Message record);
//
//    int insertSelective(Message record);
//
//    Message selectByPrimaryKey(Integer id);
//
//    int updateByPrimaryKeySelective(Message record);
//
//    int updateByPrimaryKeyWithBLOBs(Message record);
//
//    int updateByPrimaryKey(Message record);

    // 查询当前用户的会话数量.   分页用
    int selectConversationCount(int userId);

    // 查询当前用户的会话列表,针对每个会话只返回一条最新的私信.
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询某个会话所包含的私信数量.
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量   通过拼接 conversationId 决定是查询所有未读数量还是某条会话的未读数量
    int selectLetterUnreadCount(int userId, String conversationId);

    // 查询某个会话所包含的私信列表.    某条会话的详细信息
    List<Message> selectLetters(String conversationId, int offset, int limit);

    int updateStatus(List<Integer> ids, int status);

    // 新增消息
    int insertMessage(Message message);

}