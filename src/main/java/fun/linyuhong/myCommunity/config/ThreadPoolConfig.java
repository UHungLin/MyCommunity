package fun.linyuhong.myCommunity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring 线程池运行需要的配置类
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {

}
