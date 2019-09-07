package fun.linyuhong.myCommunity.async;

import com.alibaba.fastjson.JSONObject;
import fun.linyuhong.myCommunity.util.JSONUtil;
import fun.linyuhong.myCommunity.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.xml.ws.Action;

/**
 * @author linyuhong
 * @date 2019/9/6
 */
@Service
public class EventProducer {

    @Autowired
    private RedisTemplate redisTemplate;

    public boolean fireEvent(EventModel eventModel) {
        try {
            String eventQueueKey = RedisKeyUtil.getEventqueueKey();
            redisTemplate.opsForList().leftPush(eventQueueKey, JSONObject.toJSONString(eventModel));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
