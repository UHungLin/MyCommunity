package fun.linyuhong.myCommunity.dao.elasticsearch;


import fun.linyuhong.myCommunity.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

// 操作ES数据库
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {

}
