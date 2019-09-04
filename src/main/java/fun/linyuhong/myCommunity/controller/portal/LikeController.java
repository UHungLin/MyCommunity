package fun.linyuhong.myCommunity.controller.portal;

import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.service.ILikeService;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.JSONUtil;
import fun.linyuhong.myCommunity.util.XORUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     *
     * @param entityType  实体类型
     * @param entityId  实体Id
     * @param entityUserId  被点赞的用户Id
     * @return
     */
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        // 解密Id
        Integer userId = XORUtil.encryptId(hostHolder.getUser().getId(), Const.getIdEncodeKeys.userIdKeys);
        entityUserId = XORUtil.encryptId(entityUserId, Const.getIdEncodeKeys.userIdKeys);
        // 判断是 帖子id 或者 用户id，进行解密
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

        return JSONUtil.getJSONString(0, null, map);
    }

}






