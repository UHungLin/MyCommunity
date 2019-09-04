package fun.linyuhong.myCommunity.service.Impl;

import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.dao.MessageMapper;
import fun.linyuhong.myCommunity.entity.Message;
import fun.linyuhong.myCommunity.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author linyuhong
 * @date 2019/9/4
 */
@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public int findConversationCount(int id) {
        return messageMapper.selectConversationCount(id);
    }

    @Override
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    @Override
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    @Override
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, Const.status.READ);
    }

    @Override
    public int addMessage(Message message) {
        return messageMapper.insertMessage(message);
    }
}
