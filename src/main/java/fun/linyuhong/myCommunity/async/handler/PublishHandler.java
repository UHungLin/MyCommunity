package fun.linyuhong.myCommunity.async.handler;

import fun.linyuhong.myCommunity.async.EventHandler;
import fun.linyuhong.myCommunity.async.EventModel;
import fun.linyuhong.myCommunity.async.EventProducer;
import fun.linyuhong.myCommunity.async.EventType;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.service.IElasticsearchService;
import fun.linyuhong.myCommunity.service.Impl.ElasticsearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author linyuhong
 * @date 2019/9/8
 */

/**
 * 消费发帖事件，把新增加或修改的帖子添加到 ES 服务器中
 */
@Component
public class PublishHandler implements EventHandler {

    @Autowired
    private IDiscussPostService iDiscussPostService;

    @Autowired
    private IElasticsearchService iElasticsearchService;

    @Override
    public void doHandler(EventModel eventModel) {
        // 事件 event 只是存放关键信息，触发消费活动，最终查询具体内容还是调用 mysql，查找帖子放到 ES 中
        DiscussPost post = iDiscussPostService.findDiscussPostById(eventModel.getEntityId());
        iElasticsearchService.saveDiscussPost(post);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.PUBLISH);
    }
}





