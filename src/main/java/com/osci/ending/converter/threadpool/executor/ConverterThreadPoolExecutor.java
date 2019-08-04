/*
 * Copyright 2019 The Playce-WASUP Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * SangCheon Park   Aug 03, 2019		    First Draft.
 */
package com.osci.ending.converter.threadpool.executor;

import com.osci.ending.converter.threadpool.handler.ConverterRejectedExecutionHandler;
import com.osci.ending.converter.threadpool.monitor.ConverterThreadPoolMonitor;
import com.osci.ending.converter.threadpool.task.BaseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Runnable Task를 실행시키기 위한 Exceutor
 * Monitoring 및 확장성을 위해 별도로 구현되었으며, Spring ThreadPoolTaskExecutor과 같은 방식으로 동작
 * </pre>
 *
 * @author SangCheon Park
 * @version 1.0
 */
@Component
public class ConverterThreadPoolExecutor implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ConverterThreadPoolExecutor.class);

    @Value("${encoding.converter.monitoring.period:1}")
    private long monitoringPeriod;

    private int corePoolSize    = 50;
    private int maxPoolSize     = 1000;
    private long keepAliveTime  = 60;
    private int queueCapacity   = 10000;

    private ConverterThreadPoolMonitor monitor;
    private ThreadPoolExecutor executor;

    @Override
    public void afterPropertiesSet() throws Exception {
        initialize();
    }//end of afterPropertiesSet()

    /**
     * <pre>
     * executor 초기화
     * </pre>
     */
    public void initialize() {
        monitor = new ConverterThreadPoolMonitor();

        executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                keepAliveTime, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueCapacity),
                new ConverterRejectedExecutionHandler());

        monitor.setExecutor(executor);
    }//end of initialize()

    /**
     * <pre>
     *
     * </pre>
     * @param task
     */
    public synchronized void execute(Runnable task) {
        Assert.notNull(task, "task must not be null.");

        checkExecutor();
        executor.execute(task);
    }//end of execute()

    /**
     * <pre>
     *
     * </pre>
     * @param task
     */
    public synchronized void execute(BaseTask task) {
        Assert.notNull(task, "task must not be null.");

        checkExecutor();
        executor.execute(task);
    }//end of execute()

    /**
     * <pre>
     *
     * </pre>
     * @param taskList
     */
    public synchronized void execute(List<BaseTask> taskList) {
        Assert.notNull(taskList, "taskList must not be null.");

        checkExecutor();
        for (BaseTask task : taskList) {
            executor.execute(task);
        }
    }//end of execute()

    /**
     * <pre>
     * executor가 terminated 되었을 경우 executor 초기화 및 monitor가 중지 되었을 경우 monitor 초기화
     * </pre>
     *
     */
    private synchronized void checkExecutor() {
        try {
            if (executor.isTerminated()) {
                initialize();
            }

            if (!monitor.isAlive()) {
                if (monitor.getState().equals(Thread.State.TERMINATED)) {
                    monitor =  new ConverterThreadPoolMonitor();
                    monitor.setMonitoringPeriod(monitoringPeriod);
                    monitor.setExecutor(executor);
                }

                monitor.start();
            }
        } catch (Exception e) {
            if (e instanceof IllegalThreadStateException) {
                // ignore
                logger.error("Unhandled exception occurred while invoke checkExecutor().", e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }//end of checkExecutor()
}
//end of ConverterThreadPoolExecutor.java