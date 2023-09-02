package com.csi.config;


import com.csi.job.MyElasticJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {

    @Bean
    //注册中心配置
    public CoordinatorRegistryCenter createRegistryCenter(@Value("${zookeeper.url}") String url, @Value("${zookeeper.groupName}") String groupName) {

        ZookeeperConfiguration zookeeperConfiguration = new
                ZookeeperConfiguration(url, groupName);
        //设置节点超时时间
        zookeeperConfiguration.setSessionTimeoutMilliseconds(100);
        //        ZookeeperRegistryCenter("zookeeper地址","项目名")
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        regCenter.init();
        return regCenter;
    }
    //定时任务类配置  功能的方法
    private LiteJobConfiguration createJobConfiguration(Class clazz,String cron,int shardingCount) {
        // 定义作业核⼼配置newBuilder("任务名称","cron表达式","分片数量")
        JobCoreConfiguration simpleCoreConfig =
                JobCoreConfiguration.newBuilder(clazz.getSimpleName(), cron,shardingCount).build();
        // 定义SIMPLE类型配置
        SimpleJobConfiguration simpleJobConfig = new
                SimpleJobConfiguration(simpleCoreConfig,
                clazz.getCanonicalName());
        // 定义Lite作业根配置  overwrite可覆盖旧属性
        LiteJobConfiguration simpleJobRootConfig =
                LiteJobConfiguration.newBuilder(simpleJobConfig).overwrite(true).build();
        return simpleJobRootConfig;

    }
    @Bean(initMethod = "init")
    public SpringJobScheduler testScheduler(MyElasticJob job,CoordinatorRegistryCenter registryCenter){
        LiteJobConfiguration jobConfiguration=createJobConfiguration(job.getClass(),"0/5 * * * * ?",1);
        return new SpringJobScheduler(job, registryCenter, jobConfiguration);
    }
}
