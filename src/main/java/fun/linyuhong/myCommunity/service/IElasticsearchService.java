package fun.linyuhong.myCommunity.service;

import fun.linyuhong.myCommunity.entity.DiscussPost;
import org.springframework.data.domain.Page;

/**
 * @author linyuhong
 * @date 2019/9/8
 */
public interface IElasticsearchService {

    void saveDiscussPost(DiscussPost post);

    void deleteDiscussPost(int id);

    Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit);

}
