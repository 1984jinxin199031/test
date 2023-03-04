package com.boot.service.quartz;

import com.boot.dao.BookDao;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class DownloadJob extends QuartzJobBean {
    @Autowired
    private BookDao bookDao;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("哈哈哈红红火火恍恍惚惚");
//        System.out.println(bookDao.getById(2));
    }
}
