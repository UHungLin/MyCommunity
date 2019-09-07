package fun.linyuhong.myCommunity.service.Impl;

import fun.linyuhong.myCommunity.service.IFollowService;
import fun.linyuhong.myCommunity.service.ILikeService;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.util.RedisKeyUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author linyuhong
 * @date 2019/9/5
 */
@Service
public class FollowServiceImpl implements IFollowService {

    @Autowired
    private ILikeService iLikeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IUserService iUserService;

    @Override
    public void follow(int userId, int entityType, int entityId) {
        // 自己的关注者key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        // 被关注对象的粉丝key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                redisOperations.multi();
                // 自己的关注列表
                redisTemplate.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                // 被关注对象的粉丝列表
                redisTemplate.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    @Override
    public void unfollow(int userId, int entityType, int entityId) {

        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                redisOperations.multi();
                // 自己的关注列表
                redisTemplate.opsForZSet().remove(followeeKey, entityId, System.currentTimeMillis());
                // 被关注对象的粉丝列表
                redisTemplate.opsForZSet().remove(followerKey, userId, System.currentTimeMillis());

                return redisOperations.exec();
            }
        });

    }

    @Override
    public Long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    @Override
    public Long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    @Override
    public List<Map<String, Object>> findFollowee(int userId, int entityType, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return findFollow(followeeKey, offset, limit);
    }

    @Override
    public List<Map<String, Object>> findFollowers(int entityType, int userId, int offset, int limit) {

        String followerKey = RedisKeyUtil.getFollowerKey(entityType, userId);
        return findFollow(followerKey, offset, limit);
    }

    /**
     * 获取 关注 or 粉丝列表
     * @param followKey
     * @param offset
     * @param limit
     * @return
     */
    private List<Map<String, Object>> findFollow(String followKey, int offset, int limit) {
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followKey, offset, (offset + limit - 1));
        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            UserVo user = iUserService.findUserById(targetId);
            map.put("user", user);
            // 查询关注时间
            Double score = redisTemplate.opsForZSet().score(followKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    /**
     *
     * @param userId   当前登录用户id
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }
}





