package fun.linyuhong.myCommunity.service.Impl;


import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.dao.DiscussPostMapper;
import fun.linyuhong.myCommunity.dao.UserMapper;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.service.IDiscussPostService;
import fun.linyuhong.myCommunity.util.SensitiveFilter;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("iDiscussPostService")
public class DiscussPostServiceImpl implements IDiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Map<String, Object>> selectDiscussPosts(int userId, int orderMode, int offset, int limit) {

        List<DiscussPost> discussPostList = discussPostMapper.selectDiscussPosts(userId, orderMode, offset, limit);
        List<Map<String, Object>> list = new ArrayList<>();
        // id 加密
        for (DiscussPost post : discussPostList) {
            Map<String, Object> map = new HashMap();
            // 注意先获取 userId，因为 post 那里就要加密了
            UserVo userVo = assembleUserVo(userMapper.selectByPrimaryKey(post.getUserId()));
            map.put("user", userVo);
            post.setId(XORUtil.encryptId(post.getId(), Const.getIdEncodeKeys.postIdKeys));
            post.setUserId(XORUtil.encryptId(post.getUserId(), Const.getIdEncodeKeys.userIdKeys));
            map.put("post", post);
            list.add(map);
        }

        return list;
    }

    private UserVo assembleUserVo(User user) {
        UserVo userVo = new UserVo();
        userVo.setId(XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys));
        userVo.setUsername(user.getUsername());
        userVo.setEmail(user.getEmail());
        userVo.setHeaderUrl(user.getHeaderUrl());
        userVo.setType(user.getType());
        userVo.setCreateTime(user.getCreateTime());
        return userVo;
    }

    // 查询一共有多少页
    public int findDiscussPostRows(int userId) {
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
        return discussPost;
    }
}












