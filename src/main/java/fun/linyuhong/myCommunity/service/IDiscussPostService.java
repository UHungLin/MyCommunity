package fun.linyuhong.myCommunity.service;

import com.github.pagehelper.PageInfo;
import fun.linyuhong.myCommunity.entity.DiscussPost;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public interface IDiscussPostService {

    List<Map<String, Object>> selectDiscussPosts(int userId, int orderModel, int offset, int limit);

    int findDiscussPostRows(int userId);

    int addDiscussPost(DiscussPost post);

    DiscussPost getDiscussPost(Integer discussPostId);

}
