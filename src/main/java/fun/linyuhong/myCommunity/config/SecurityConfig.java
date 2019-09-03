//package fun.linyuhong.myCommunity.config;
//
//import fun.linyuhong.myCommunity.service.Impl.JwtUserDetailsServiceImpl;
//import fun.linyuhong.myCommunity.util.JSONUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.security.web.csrf.CsrfFilter;
//import org.springframework.web.filter.CharacterEncodingFilter;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//
//    // 需要授权才能访问的路径
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//            .antMatchers("/index", "/resources/**")
//            .permitAll()
////            .and()
////            .formLogin()
////                .loginPage("/site/login.html")
////                .loginProcessingUrl("/login")
////                .successForwardUrl("/index")
//            .and()
//            .csrf() // 由于使用的是JWT，我们这里不需要csrf
//            .disable()
//            .sessionManagement() // 定制我们自己的 session 策略
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 调整为让 Spring Security 不创建和使用 session
//
//        ;
//
//
//
//        /**
//         * 自定义未登陆、未授权结果的处理
//         *
//         * 分为 http 请求和 异步 请求
//         */
//        http.exceptionHandling()
//                .authenticationEntryPoint(new AuthenticationEntryPoint() {
//                    // 未登录的处理
//                    @Override
//                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//                        String header = request.getHeader("x-requested-with");
//                        // 异步
//                        if ("XMLHttpRequest".equals(header)) {
//                            response.setContentType("application/plain;charset=utf-8");
//                            PrintWriter writer = response.getWriter();
//                            writer.write(JSONUtil.getJSONString(403, "你还没有登录"));
//                        }else {
//                            response.sendRedirect(request.getContextPath() + "/login");
//                        }
//                    }
//                })
//                .accessDeniedHandler(new AccessDeniedHandler() {
//                    // 没用权限处理
//                    @Override
//                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
//                        String header = request.getHeader("x-requested-with");
//                        if ("XMLHttpRequest".equals(header)) {
//                            response.setContentType("application/plain;charset=utf-8");
//                            PrintWriter writer = response.getWriter();
//                            writer.write(JSONUtil.getJSONString(403, "你没有访问此功能的权限"));
//                        }else {
//                            response.sendRedirect(request.getContextPath() + "/denied");
//                        }
//                    }
//                });
//        //解决中文乱码问题
//        CharacterEncodingFilter filter = new CharacterEncodingFilter();
//        filter.setEncoding("UTF-8");
//        filter.setForceEncoding(true);
//        http.addFilterBefore(filter, CsrfFilter.class);
//
//    }
//
//
//}
//
//
//
//
//
