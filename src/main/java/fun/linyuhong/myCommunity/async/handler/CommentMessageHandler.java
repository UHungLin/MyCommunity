package fun.linyuhong.myCommunity.async.handler;

import com.alibaba.fastjson.JSONObject;
import fun.linyuhong.myCommunity.async.EventHandler;
import fun.linyuhong.myCommunity.async.EventModel;
import fun.linyuhong.myCommunity.async.EventType;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.entity.Message;
import fun.linyuhong.myCommunity.service.IMessageService;
import fun.linyuhong.myCommunity.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author linyuhong
 * @date 2019/9/6
 */
@Component
public class CommentMessageHandler implements EventHandler {

    @Autowired
    private IMessageService iMessageService;

    @Autowired
    private IUserService iUserService;

    @Override
    public void doHandler(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(Const.systemuser.SYSTEM_USER_ID);
        message.setToId(eventModel.getEntityUserId());
        message.setStatus(Const.status.UNREAD);
        message.setConversationId(eventModel.getEventType().getValue());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();  // Message 数据表里 content 字段的内容，用来拼成显示的 消息模板
        content.put("userId", eventModel.getActorId());  // 触发者
        content.put("entityType", eventModel.getEntityType());
        content.put("entityId", eventModel.getEntityId());

        // 把其他的额外消息也一起放进content里
        if (!eventModel.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : eventModel.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        iMessageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE, EventType.COMMENT, EventType.FOLLOW);
    }
}
