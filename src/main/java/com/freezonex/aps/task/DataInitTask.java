package com.freezonex.aps.task;

import com.freezonex.aps.modules.asset.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 数据初始化任务
 */
@Component
@Slf4j
public class DataInitTask {

    @Resource
    private DataService dataService;

    /**
     * 1.每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Shanghai")
    public void task1() {
        initData();
    }

    /**
     * 2.每天早上8点执行一次  看不到系统日志，系统时区问题 8点补偿执行一次
     */
    @Scheduled(cron = "0 0 8 * * ?", zone = "Asia/Shanghai")
    public void task2() {
        initData();
    }

    private void initData() {
        log.info("start task,current system time :{}", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        dataService.initData();
        log.info("end task");
    }

}