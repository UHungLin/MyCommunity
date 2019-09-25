package fun.linyuhong.myCommunity;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * 线上部署相关
 * 关闭启动类(因为tomcat里面也有main方法，而我们无法调用原有的main方法)，配置入口类，让tomcat通过它启动启动类
 * MyCommunityServletInitializer
 */
public class MyCommunityServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MyCommunityApplication.class);
    }
}
