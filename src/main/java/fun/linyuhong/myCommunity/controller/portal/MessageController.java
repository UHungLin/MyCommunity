package fun.linyuhong.myCommunity.controller.portal;

import com.alibaba.fastjson.JSONObject;
import fun.linyuhong.myCommunity.async.EventType;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.common.Page;
import fun.linyuhong.myCommunity.entity.Message;
import fun.linyuhong.myCommunity.service.IMessageService;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.JSONUtil;
import fun.linyuhong.myCommunity.util.SensitiveFilter;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author linyuhong
 * @date 2019/9/4
 */
@Controller
public class MessageController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IMessageService iMessageService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    // 私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {

        UserVo user = hostHolder.getUser();
        // 解密
        int userId = XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys);

        // 设置分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        // 查询有多少条会话
        page.setRows(iMessageService.findConversationCount(userId));

        List<Message> conversationList = iMessageService.findConversations(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letterCount", iMessageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", iMessageService.findLetterUnreadCount(userId, message.getConversationId()));
                int targetId = userId == message.getFromId() ? message.getToId() : message.getFromId();
                // 永远只显示对话双方的另一方
                map.put("target", iUserService.findUserById(targetId));
                // 加密 id
                message.setFromId(XORUtil.encryptId(message.getFromId(), Const.getIdEncodeKeys.userIdKeys));
                message.setToId(XORUtil.encryptId(message.getToId(), Const.getIdEncodeKeys.userIdKeys));
                // todo 这里还差对 conversationId 加密，需要判断是会话还是系统通知，嫌麻烦略过
                map.put("conversation", message);

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询总的未读消息数量
        // 私信总未读数量
        int letterUnreadCount = iMessageService.findLetterUnreadCount(userId, null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        // 系统通知未读数量
        int noticeUnreadCount = iMessageService.findNoticeUnreadCount(userId, null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);


        return "/site/letter";
    }


    // 会话详情页展示
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {

        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(iMessageService.findLetterCount(conversationId));


        // 私信列表
        List<Message> letterList = iMessageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("fromUser", iUserService.findUserById(message.getFromId()));
                message.setFromId(XORUtil.encryptId(message.getFromId(), Const.getIdEncodeKeys.userIdKeys));
                message.setToId(XORUtil.encryptId(message.getToId(), Const.getIdEncodeKeys.userIdKeys));
                map.put("letter", message);
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));
        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            iMessageService.readMessage(ids);
        }

        // 查询未读消息数量
        // 未读会话数量
        UserVo user = hostHolder.getUser();
        int userId = XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys);
        int letterUnreadCount = iMessageService.findLetterUnreadCount(userId, null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        // 未读系统通知数量
        int noticeUnreadCount = iMessageService.findNoticeUnreadCount(userId, null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);


        return "/site/letter-detail";
    }

    // 拿到会话的对方
    private UserVo getLetterTarget(String conversationId) {
        UserVo user = hostHolder.getUser();
        // 解密
        int userId = XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys);

        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (userId == id0) {
            return iUserService.findUserById(id1);
        } else {
            return iUserService.findUserById(id0);
        }
    }

    // 拿到所有未读私信的id
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null){
            for (Message message : letterList) {
                if (message.getToId().equals(hostHolder.getUser().getId()) && message.getStatus() == Const.status.UNREAD) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {

        if (StringUtils.isBlank(toName) || StringUtils.isBlank(content)) {
            return JSONUtil.getJSONString(2, "用户名或内容不能为空");
        }

        UserVo user = hostHolder.getUser();
        // 解密
        int userId = XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys);


        UserVo target = iUserService.findUserByName(toName);
        if (target == null) {
            return JSONUtil.getJSONString(1, "目标用户不存在!");
        }

        Message message = new Message();
        message.setFromId(userId);
        message.setToId(XORUtil.encryptId(target.getId(), Const.getIdEncodeKeys.userIdKeys));
        message.setStatus(Const.status.UNREAD);
        message.setContent(sensitiveFilter.filter(HtmlUtils.htmlEscape(content)));
        message.setCreateTime(new Date());
        // 规定 id 小的在前面
        if (message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        iMessageService.addMessage(message);

        return JSONUtil.getJSONString(0);
    }


    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {

        UserVo user = hostHolder.getUser();
        // 解密
        int userId = XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys);

        // 查询最新 的评论通知
        Message message = iMessageService.findLatestNotice(userId, EventType.COMMENT.getValue());
        Map<String, Object> messageVo = new HashMap<>();

        messageVo.put("message", null);

        messageVo = assembleMessageVo(message, messageVo, EventType.COMMENT, userId);
        model.addAttribute("commentNotice", messageVo);


        // 查询点赞类通知
        message = iMessageService.findLatestNotice(userId, EventType.LIKE.getValue());
        messageVo = new HashMap<>();

        messageVo.put("message",null);

        messageVo = assembleMessageVo(message, messageVo, EventType.LIKE, userId);
        model.addAttribute("likeNotice", messageVo);


        // 查询关注类通知
        message = iMessageService.findLatestNotice(userId, EventType.FOLLOW.getValue());
        messageVo = new HashMap<>();

        messageVo.put("message",null);

        messageVo = assembleMessageVo(message, messageVo, EventType.FOLLOW, userId);
        model.addAttribute("followNotice", messageVo);


        // 查询系统通知
        message = iMessageService.findLatestNotice(userId, EventType.SYSTEM.getValue());
        messageVo = new HashMap<>();

        messageVo.put("message",null);

        messageVo = assembleMessageVo(message, messageVo, EventType.SYSTEM, userId);
        model.addAttribute("systemNotice", messageVo);


        // 查询未读消息数量
        // 未读会话总的数量
        int letterUnreadCount = iMessageService.findLetterUnreadCount(userId, null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        // 未读系统总的通知数量(包括点赞、评论、关注、注册等通知)
        int noticeUnreadCount = iMessageService.findNoticeUnreadCount(userId, null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);


        return "/site/notice";

    }

    private Map<String, Object> assembleMessageVo(Message message, Map<String, Object> messageVo, EventType type, int userId) {

        if (message != null) {
            // 用户id 加密
            message.setToId(XORUtil.encryptId(message.getToId(), Const.getIdEncodeKeys.userIdKeys));
            messageVo.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            // 事件触发者 actor
            messageVo.put("user", iUserService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            // 如果是帖子id，则加密  评论id不用
            int entityId = data.get("entityType").equals(Const.entityType.ENTITY_TYPE_POST) ?
            XORUtil.encryptId((Integer) data.get("entityId"), Const.getIdEncodeKeys.postIdKeys) : (Integer) data.get("entityId");
            messageVo.put("entityId", entityId);

            /**
             * 注意：关注、注册事件没有 postId
             */
            if (data.get("postId") != null) {
                // 帖子id加密
                messageVo.put("postId", XORUtil.encryptId((Integer) data.get("postId"), Const.getIdEncodeKeys.postIdKeys));
            }

            /**
             * 取出注册时写的msg
             */
            if (data.get("msg") != null) {
                messageVo.put("msg", data.get("msg"));
            }

            int count = iMessageService.findNoticeCount(userId, type.getValue());
            messageVo.put("count", count);

            int unread = iMessageService.findNoticeUnreadCount(userId, type.getValue());
            messageVo.put("unread", unread);
        }

        return messageVo;
    }


    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        UserVo user = hostHolder.getUser();
        // 解密
        int userId = XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys);

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(iMessageService.findNoticeCount(userId, topic));


        List<Message> noticeList = iMessageService.findNotices(userId, topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                // 事件触发者 actor
                map.put("user", iUserService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                // 如果是帖子id，则加密  评论id不用
                int entityId = data.get("entityType").equals(Const.entityType.ENTITY_TYPE_POST) ?
                        XORUtil.encryptId((Integer) data.get("entityId"), Const.getIdEncodeKeys.postIdKeys) : (Integer) data.get("entityId");
                map.put("entityId", entityId);
                /**
                 * 注意：关注、注册等系统事件没有 postId
                 */
                if (!topic.equals(EventType.FOLLOW.getValue()) && !topic.equals(EventType.SYSTEM.getValue())) {
                    // 帖子id加密
                    map.put("postId", XORUtil.encryptId((Integer) data.get("postId"), Const.getIdEncodeKeys.postIdKeys));
                }

                /**
                 * 类似注册事件还有 msg
                 */
                if (data.get("msg") != null) {
                    map.put("msg", data.get("msg"));
                }
                // 通知作者
                map.put("fromUser", iUserService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

        // 先加密  再传进 getLetterIds 中
        for (Message message : noticeList) {
            message.setToId(XORUtil.encryptId(message.getToId(), Const.getIdEncodeKeys.userIdKeys));
        }
        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            iMessageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }

}








