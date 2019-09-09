package fun.linyuhong.myCommunity.async.handler;

import fun.linyuhong.myCommunity.async.EventHandler;
import fun.linyuhong.myCommunity.async.EventModel;
import fun.linyuhong.myCommunity.async.EventType;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.service.IElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author linyuhong
 * @date 2019/9/8
 */
public class DeleteHandler implements EventHandler {

    @Autowired
    private IDiscussPostService iDiscussPostService;

    @Autowired
    private IElasticsearchService iElasticsearchService;


    @Override
    public void doHandler(EventModel eventModel) {
        iElasticsearchService.deleteDiscussPost(eventModel.getEntityId());
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.DELETE);
    }
}
