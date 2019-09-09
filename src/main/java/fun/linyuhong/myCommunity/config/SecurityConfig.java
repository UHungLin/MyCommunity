package fun.linyuhong.myCommunity.config;


import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.util.JSONUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 过滤不用验证的路径
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }


    /**
     * 绕过登录验证，采用我们之前自己写好的验证逻辑
     *
     * 但需要注意的是，走自己的认证逻辑，最后还是需要把认证后
     * 的结果加到 SecurityContext 里面，因为Spring Security
     * 默认会调用里面的结果进行权限判断
     *
     * */


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 需要授权的才能访问的路径路径
        http.authorizeRequests().antMatchers(
                "/user/setting",
                "/user/upload",
                "/user/profile",
                "/discuss/add",
                "/comment/add/**",
                "/letter/**",
                "/notice/**",
                "/like",
                "/follow",
                "/unfollow"
        )
        .hasAnyAuthority(
                Const.Role.ROLE_USER.getRole(),
                Const.Role.ROLE_ADMIN.getRole()
        )
        .antMatchers(
                "/discuss/top",  // 置顶
                "/discuss/wonderful",  // 加精
                "/discuss/delete"  // 删除
                )
        .hasAnyAuthority(
                Const.Role.ROLE_ADMIN.getRole()  // 管理员
                )
        .anyRequest().permitAll() // 其他任何请求都允许通过
        .and().csrf().disable();  // 关闭 csrf 验证


        /**
         * 分为 没有登录 和 权限不够 的处理
         *
         * 请求又分为 异步请求 和 http请求
         * */
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // 没有登录
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            // 异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(JSONUtil.getJSONString(403, "你还没有登录"));
                        }else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // 权限不足
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(JSONUtil.getJSONString(403, "你没有访问此功能的权限!"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // Security底层默认会拦截/logout请求,进行退出处理.
        // 覆盖它默认的逻辑,才能执行我们自己的退出代码.
        http.logout().logoutUrl("/securitylogout");
    }
}








