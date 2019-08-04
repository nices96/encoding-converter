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
package com.osci.ending.converter.threadpool.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <pre>
 * ThreadPool Monitoring을 위한 클래스로써 초단위 monitoringPeriod 값을 기준으로 주기적으로 ThreadPool 상태를 모니터링한다.
 * </pre>
 *
 * @author SangCheon Park
 * @version 1.0
 */
public class ConverterThreadPoolMonitor extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ConverterThreadPoolMonitor.class);

    private ThreadPoolExecutor executor;
    private long monitoringPeriod = 5;

    private MemoryMXBean memoryMXBean;
    private ThreadMXBean threadMXBean;
    private MonitorDefinition monitorDefinition;

    /**
     * Sets executor.
     *
     * @param executor the executor
     */
    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    /**
     * @param monitoringPeriod the monitoringPeriod to set
     */
    public void setMonitoringPeriod(long monitoringPeriod) {
        this.monitoringPeriod = monitoringPeriod;
    }

    /**
     * Run.
     */
    public void run() {
        if (memoryMXBean == null) {
            logger.debug("memoryMXBean is null.");
            this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        }
        if (threadMXBean == null) {
            logger.debug("threadMXBean is null.");
            this.threadMXBean = ManagementFactory.getThreadMXBean();
        }

        try {
            while (true) {
                monitoring();

                if (executor.isTerminated() || executor.getActiveCount() == 0) {
                    break;
                }

                Thread.sleep(monitoringPeriod * 1000);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }//end of run()

    /**
     * Monitoring.
     */
    private void monitoring() {
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();

        monitorDefinition = new MonitorDefinition();

        monitorDefinition.setCurrentPoolSize(executor.getPoolSize());
        monitorDefinition.setCorePoolSize(executor.getCorePoolSize());
        monitorDefinition.setMaximumPoolSize(executor.getMaximumPoolSize());
        monitorDefinition.setActiveTaskCount(executor.getActiveCount());
        monitorDefinition.setCompletedTaskCount(executor.getCompletedTaskCount());
        monitorDefinition.setTotalTaskCount(executor.getTaskCount());
        monitorDefinition.setTerminated(executor.isTerminated());

        monitorDefinition.setUsed(memoryUsage.getUsed());
        monitorDefinition.setCommitted(memoryUsage.getCommitted());
        monitorDefinition.setMax(memoryUsage.getMax());

        monitorDefinition.setLive(threadMXBean.getThreadCount());
        monitorDefinition.setPeak(threadMXBean.getPeakThreadCount());
        monitorDefinition.setTotal(threadMXBean.getTotalStartedThreadCount());

        logger.debug("{}", monitorDefinition);
    }//end of monitoring()
}
//end of ConverterThreadPoolMonitor.java