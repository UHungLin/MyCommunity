package fun.linyuhong.myCommunity.async;

import java.util.List;

/**
 * @author linyuhong
 * @date 2019/9/6
 */
public interface EventHandler {

    void doHandler(EventModel eventModel);

    List<EventType> getSupportEventTypes();

}
