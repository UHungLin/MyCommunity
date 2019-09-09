package fun.linyuhong.myCommunity.controller.portal;

import fun.linyuhong.myCommunity.async.EventModel;
import fun.linyuhong.myCommunity.async.EventProducer;
import fun.linyuhong.myCommunity.async.EventType;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.service.ILikeService;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.JSONUtil;
import fun.linyuhong.myCommunity.util.RedisKeyUtil;
import fun.linyuhong.myCommunity.util.XORUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linyuhong
 * @date 2019/9/5
 */
@Controller
public class LikeController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private ILikeService iLikeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     *
     * @param entityType  实体类型
     * @param entityId  实体Id
     * @param entityUserId  被点赞的用户Id
     * @return
     */
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        // 解密Id
        Integer userId = XORUtil.encryptId(hostHolder.getUser().getId(), Const.getIdEncodeKeys.userIdKeys);
        entityUserId = XORUtil.encryptId(entityUserId, Const.getIdEncodeKeys.userIdKeys);
        // 判断是 帖子id 或者 用户id，进行解密    评论id则不理会，因为我没有加密
        if (entityType == Const.like.ENTITY_TYPE_POST) {
            entityId = XORUtil.encryptId(entityId, Const.getIdEncodeKeys.postIdKeys);
        } else if (entityType == Const.like.ENTITY_TYPE_USER) {
            entityId = XORUtil.encryptId(entityId, Const.getIdEncodeKeys.userIdKeys);
        }

        iLikeService.like(userId, entityType, entityId, entityUserId);


        // 数量
        long likeCount = iLikeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = iLikeService.findEntityLikeStatus(userId, entityType, entityId);
        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        /**
         * 触发点赞事件
         * 自己给自己点赞不触发
         */
        if (likeStatus == 1 && userId != entityUserId) {
            EventModel eventModel = new EventModel(EventType.LIKE)
                    .setActorId(userId)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", XORUtil.encryptId(postId, Const.getIdEncodeKeys.postIdKeys));  // postId 为帖子Id，点击查看详情时用到 对于点赞对象为 comment 时有用
            eventProducer.fireEvent(eventModel);
        }

        if(entityType == Const.entityType.ENTITY_TYPE_POST) {  // 只计算对帖子点赞的分
            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, XORUtil.encryptId(postId, Const.getIdEncodeKeys.postIdKeys));
        }

        return JSONUtil.getJSONString(0, null, map);
    }

}






