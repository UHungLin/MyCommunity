package fun.linyuhong.myCommunity.interceptor;

import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.entity.LoginTicket;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.util.CookieUtil;
import fun.linyuhong.myCommunity.util.XORUtil;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 防止重复登录
 * @author linyuhong
 * @date 2019/9/1
 */
@Component
public class LoginOnlyOneInterceptor implements HandlerInterceptor {

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
                response.sendRedirect("/index");
                return false;
            }
        }
        return true;
    }

}
