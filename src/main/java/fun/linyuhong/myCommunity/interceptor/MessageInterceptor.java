package fun.linyuhong.myCommunity.interceptor;


import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.service.IMessageService;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 展示标题头总的未读信息数量
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IMessageService iMessageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserVo user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int userId = XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys);
            int letterUnreadCount = iMessageService.findLetterUnreadCount(userId, null);
            int noticeUnreadCount = iMessageService.findNoticeUnreadCount(userId, null);
            modelAndView.addObject("allUnreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }
}
