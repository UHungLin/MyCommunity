package fun.linyuhong.myCommunity.config;



import fun.linyuhong.myCommunity.quartz.AlphaJob;
import fun.linyuhong.myCommunity.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//@Configuration
public class QuartzConfig {

//    @Bean
    public JobDetailFactoryBean alphaJobDetail () {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);  // 管理的Job
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaGroup");
        factoryBean.setDurability(true);  // 任务是否持久保存
        factoryBean.setRequestsRecovery(true);  // 任务发生错误时是否可恢复

        return factoryBean;
    }

    /**
     *注意 参数 alphaJobDetail 的名字要上下一致，这样当有多个
     *  JobDetailFactoryBean 实例时，Spring会优先根据
     *  名字匹配进行注入
     *
     */

    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
//    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);  // 每 3 秒执行一次
        factoryBean.setJobDataMap(new JobDataMap());  // Trigger 底层以 JobDataMap() 数据类型保存Job的一些状态
        return factoryBean;
    }


    // 刷新帖子分数任务
//    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

//    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
//        factoryBean.setRepeatInterval(1000 * 60 * 5);  // 5分钟
        factoryBean.setRepeatInterval(1000 * 60 * 120);  // 2小时
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

}
