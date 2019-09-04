package fun.linyuhong.myCommunity.service;

import fun.linyuhong.myCommunity.entity.Message;

import java.util.List;

/**
 * @author linyuhong
 * @date 2019/9/4
 */
public interface IMessageService {

    int findConversationCount(int id);

    List<Message> findConversations(int userId, int offset, int limit);

    int findLetterCount(String conversationId);

    int findLetterUnreadCount(int userId, String conversationId);

    List<Message> findLetters(String conversationId, int offset, int limit);

    int readMessage(List<Integer> ids);

    int addMessage(Message message);

}
