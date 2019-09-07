package fun.linyuhong.myCommunity.service;

import fun.linyuhong.myCommunity.vo.UserVo;

import java.util.List;
import java.util.Map;

/**
 * @author linyuhong
 * @date 2019/9/5
 */
public interface IFollowService {

    void follow(int userId, int entityType, int entityId);

    void unfollow(int userId, int entityType, int entityId);

    Long findFolloweeCount(int userId, int entityType);

    Long findFollowerCount(int entityType, int entityId);

    List<Map<String, Object>> findFollowee(int userId, int entityType, int offset, int limit);

    List<Map<String, Object>> findFollowers(int entityType, int userId, int offset, int limit);

    boolean hasFollowed(int userId, int entityType, int entityId);

}
