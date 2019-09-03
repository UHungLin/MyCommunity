package fun.linyuhong.myCommunity.config;

import fun.linyuhong.myCommunity.interceptor.LoginOnlyOneInterceptor;
import fun.linyuhong.myCommunity.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author linyuhong
 * @date 2019/9/1
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginOnlyOneInterceptor loginOnlyOneInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(loginOnlyOneInterceptor)
                .addPathPatterns("/login");

    }

}
