package fun.linyuhong.myCommunity.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linyuhong
 * @date 2019/9/6
 */
public class EventModel {

    EventType eventType;
    private int actorId;
    private int entityType;
    private int entityId;
    private int entityUserId;
    // 存放多余信息
    private Map<String, Object> data = new HashMap<>();


    public EventModel() {

    }

    public EventModel(EventType type) {
        this.eventType = type;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public EventModel setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public EventModel setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}





