package fun.linyuhong.myCommunity.interceptor;

import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.entity.LoginTicket;
import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.util.CookieUtil;
import fun.linyuhong.myCommunity.util.HostHolder;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 获取每次请求中的用户信息和验证是否有效
 * @author linyuhong
 * @date 2019/9/1
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // loginTicket.getTicket()
        String ticket = CookieUtil.getValue(request, Const.ticket.TICKET);
        if (ticket != null) {
            // ticket:UUID
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == Const.loginStatus.VALID && loginTicket.getExpired().after(new Date())) {
                // 对 userId 加密，只分装必要的信息，密码不泄漏
                UserVo userVo = userService.findUserById(XORUtil.encryptId(loginTicket.getUserId(), Const.getIdEncodeKeys.userIdKeys));
                // 在本次请求中持有的用户
                hostHolder.setUser(userVo);
            }
        }
        return true;
    }

    // 在Controller的方法调用之后执行，但是它会在DispatcherServlet进行视图的渲染之前执行，
    // 也就是说在这个方法中你可以对ModelAndView进行操作。
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserVo user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    // 该方法将在整个请求完成之后，也就是DispatcherServlet渲染了视图执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
