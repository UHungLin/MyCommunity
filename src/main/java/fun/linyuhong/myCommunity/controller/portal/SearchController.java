package fun.linyuhong.myCommunity.controller.portal;


import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.common.Page;
import fun.linyuhong.myCommunity.entity.DiscussPost;
import fun.linyuhong.myCommunity.service.IElasticsearchService;
import fun.linyuhong.myCommunity.service.ILikeService;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.util.XORUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    @Autowired
    private IElasticsearchService iElasticsearchService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ILikeService iLikeService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                iElasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                // id 加密
                post.setId(XORUtil.encryptId(post.getId(), Const.getIdEncodeKeys.postIdKeys));
                map.put("post", post);
                // 作者
                map.put("user", iUserService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", iLikeService.findEntityLikeCount(Const.entityType.ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "/site/search";
    }

}
