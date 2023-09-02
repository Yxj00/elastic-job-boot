package com.csi.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyElasticJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("定时调度："+new Date());
    }
}
