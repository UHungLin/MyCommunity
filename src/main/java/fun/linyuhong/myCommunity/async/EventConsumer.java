package fun.linyuhong.myCommunity.async;

import com.alibaba.fastjson.JSONObject;
import fun.linyuhong.myCommunity.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linyuhong
 * @date 2019/9/6
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private ApplicationContext applicationContext;
    private Map<EventType, List<EventHandler>> config = new HashMap<>();


    // ########################### 初始化加载 XXXEventHandler ################################
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> supportEventTypes = entry.getValue().getSupportEventTypes();

                for (EventType eventType : supportEventTypes) {
                    if (!config.containsKey(eventType)) {
                        config.put(eventType, new ArrayList<EventHandler>());
                    }
                    config.get(eventType).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String eventqueueKey = RedisKeyUtil.getEventqueueKey();

                    List<Object> lists =  redisTemplate.executePipelined(new RedisCallback<Object>() {
                        @Nullable
                        @Override
                        public Object doInRedis(RedisConnection connection) throws DataAccessException {
                            //队列没有元素会阻塞操作，直到队列获取新的元素或超时

                            return connection.bRPop(0, eventqueueKey.getBytes());
                        }
                    }, new StringRedisSerializer());


                    for (Object obj : lists) {
                        if (obj == null) {
                            continue;
                        }
                        List<String> events = (List) obj;
                        for (String message : events) {
                            // events = [eventqueueKey, {xxx: xxx, } ]
                            if (message.equals(eventqueueKey)) {
                                continue;
                            }

                            // com.alibaba.fastjson.JSONException: syntax error, expect {, actual string, pos 0
                            // "{\"actorId\":159,\"entityId\":285,\"entityType\":1,\"entityUserId\":16974750,\"eventType\":\"LIKE\"}"
                            // 去掉 \ 和 "，不然 JSONObject.parseObject 会失败
                            message = message.replace("\\", "").replace("\"{", "{").replace("}\"", "}");

                            EventModel eventModel = JSONObject.parseObject(message, EventModel.class);
                            if (!config.containsKey(eventModel.getEventType())) {
                                logger.error("不能识别的事件");
                                continue;
                            }
                            for (EventHandler eventHandler : config.get(eventModel.getEventType())) {
                                eventHandler.doHandler(eventModel);
                            }
                        }
                    }

                    }

// ################################### 轮询 ##################################
//                    if (message == null) {
//                        continue;
//                    }
//                    EventModel eventModel = JSONObject.parseObject(message, EventModel.class);
//                    if (!config.containsKey(eventModel.getEventType())) {
//                        logger.error("不能识别的事件");
//                        continue;
//                    }
//                    for (EventHandler eventHandler : config.get(eventModel.getEventType())) {
//                        eventHandler.doHandler(eventModel);
//                    }
                }
//            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
