package fun.linyuhong.myCommunity.service.Impl;


import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.dao.DiscussPostMapper;
import fun.linyuhong.myCommunity.dao.UserMapper;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.service.ILikeService;
import fun.linyuhong.myCommunity.util.SensitiveFilter;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service("iDiscussPostService")
public class DiscussPostServiceImpl implements IDiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostServiceImpl.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Autowired
    private SensitiveFilter sensitiveFilter;


    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口: Cache, LoadingCache(同步缓存), AsyncLoadingCache(异步缓存)

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;


    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        // build 可以使参数生效，同时返回 postListCache 对象
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        // 二级缓存: Redis -> mysql

                        logger.debug("load post list from DB.");
                        // 第一次启动，初始化时去 mysql 数据库中查
                        return discussPostMapper.selectDiscussPosts(0, 1, offset, limit);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }


    @Override
    public List<DiscussPost> selectDiscussPosts(int userId, int orderMode, int offset, int limit) {

        // 从缓存里面取热门帖子
        // 只缓存 访问首页的 热门帖子 的数据
        // 一页的数据由 offset 和 limit 有关
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }

        logger.debug("load post list from DB.");

        return discussPostMapper.selectDiscussPosts(userId, orderMode, offset, limit);

    }



    // 查询一共有多少页
    public int findDiscussPostRows(int userId) {

        if (userId == 0) {
            return postRowsCache.get(userId);
        }

        logger.debug("load post rows from DB.");

        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost post) {

        // 转义html标签
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    @Override
    public DiscussPost getDiscussPost(Integer discussPostId) {
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(XORUtil.encryptId(discussPostId, Const.getIdEncodeKeys.postIdKeys));
        // 帖子 id 加密
        discussPost.setId(XORUtil.encryptId(discussPost.getId(), Const.getIdEncodeKeys.postIdKeys));
        // 帖子作者加密
        discussPost.setUserId(XORUtil.encryptId(discussPost.getUserId(), Const.getIdEncodeKeys.userIdKeys));
        return discussPost;
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}












