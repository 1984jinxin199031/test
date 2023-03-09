package com.boot.config;

import com.boot.service.quartz.DownloadJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class QuartzConfig {
    //构建 JobDetail
//    @Bean
    public JobDetail downloadDetail() {
        return JobBuilder.newJob(DownloadJob.class) //具体任务类
                //给 JobDetail 起一个 id, 不写也会自动生成唯一的 TriggerKey
                .withIdentity("test")
                //JobDetail 内部的一个 map, 可以存储有关 Job 的数据, 这里的数据
                // 可通过 Job 类中executeInternal方法的参数进行获取
                .usingJobData("name","greatMan")
                .storeDurably()  //即使没有Trigger关联时也不删除该Jobdetail
                .build();
    }
    //构建 Trigger 及 Scheduler
//    @Bean
    public Trigger downloadTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(downloadDetail())  //关联上面的 jobDetail
                .withIdentity("test")
                .usingJobData("name","greatMan2")
                //cron 表达式设置每隔 5 秒执行一次
                .withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ? *"))
                .build();
    }
}

