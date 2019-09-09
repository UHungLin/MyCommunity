package fun.linyuhong.myCommunity.quartz;


import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.service.IElasticsearchService;
import fun.linyuhong.myCommunity.service.ILikeService;
import fun.linyuhong.myCommunity.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IDiscussPostService iDiscussPostService;

    @Autowired
    private ILikeService iLikeService;

    @Autowired
    private IElasticsearchService iElasticsearchService;

    // 溢出网纪元
    private static final Date epoch;

    static {
        try {
            // 把字符串转化为日期
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-09-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化溢出网纪元失败!", e);
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        // 例如半夜时间段没人访问网站帖子，就不用刷新了
        if (operations.size() == 0) {
            logger.info("[任务取消] 没有需要刷新的帖子!");
            return;
        }

        // 统计每次刷新的时间，将来如果出现很卡的现象可以分析
        logger.info("[任务开始] 正在刷新帖子分数: " + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕!");
    }

    private void refresh(int postId) {
        DiscussPost post = iDiscussPostService.findDiscussPostById(postId);
        // 可能在要计算的时候被删除了
        if (post == null) {
            logger.error("该帖子不存在: id = " + postId);
            return;
        }

        // 是否精华
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = iLikeService.findEntityLikeCount(Const.entityType.ENTITY_TYPE_POST, postId);

        // 计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数 = 帖子权重 + 距离天数
        // Math.log10(x)  x不能小于1，否则就是负数了
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24); // 得到毫秒值，换算成天
        // 更新帖子分数
        iDiscussPostService.updateScore(postId, score);
        // 同步搜索数据
        post.setScore(score);
        iElasticsearchService.saveDiscussPost(post);
    }

}
