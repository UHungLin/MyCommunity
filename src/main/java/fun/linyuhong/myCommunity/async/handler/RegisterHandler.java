package fun.linyuhong.myCommunity.async.handler;

import com.alibaba.fastjson.JSONObject;
import fun.linyuhong.myCommunity.async.EventHandler;
import fun.linyuhong.myCommunity.async.EventModel;
import fun.linyuhong.myCommunity.async.EventType;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.dao.UserMapper;
import fun.linyuhong.myCommunity.entity.Message;
import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.service.IMessageService;
import fun.linyuhong.myCommunity.util.MailClient;
import fun.linyuhong.myCommunity.util.XORUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

/**
 * @author linyuhong
 * @date 2019/9/7
 */
@Component
public class RegisterHandler implements EventHandler {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private IMessageService iMessageService;

    // 域名
    @Value("${domain}")
    private String domain;

    // 项目名
    @Value("${server.servlet.context-path}")
    private String contextPath;


    @Override
    public void doHandler(EventModel eventModel) {

        User user = userMapper.selectByPrimaryKey(eventModel.getEntityUserId());

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/activation/10110110/code
        String url = domain + contextPath + "/activation/" + XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys)
                + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);


        Message message = new Message();
        message.setFromId(Const.systemuser.SYSTEM_USER_ID);
        message.setToId(eventModel.getEntityUserId());
        message.setStatus(Const.status.UNREAD);
//        message.setConversationId(eventModel.getEventType().getValue());
        /**
         * 为了统一方便，这里 ConversationId 不用 register，而是用 system 代表所有跟注册、登录相关的系统通知事件
         * 这样在展示详细信息时方便，因为像 注册这种，一般就一次，不用单独用页面展示这条信息
         */
        message.setConversationId(EventType.SYSTEM.getValue());
        message.setCreateTime(new Date());

        Map<String, Object> messageContent = new HashMap<>();
        messageContent.put("userId", eventModel.getActorId());  // 触发者
        messageContent.put("entityType", eventModel.getEntityType());
        messageContent.put("entityId", eventModel.getEntityId());

        // 把其他的额外消息也一起放进content里
        if (!eventModel.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : eventModel.getData().entrySet()) {
                messageContent.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(messageContent));

        iMessageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.REGISTER);
    }
}
