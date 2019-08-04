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
package com.osci.ending.converter.threadpool.task;

import com.osci.ending.converter.threadpool.handler.ConverterTaskExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * Encoding 변환 task, XML Parsing task 등을 위한 기본 task로써 Runnable interface가 구현되어 있다.
 * BaseTask를 상속받고 taskRun() 메소드를 구현해야 한다.
 * </pre>
 *
 * @author Sang-cheon Park
 * @version 1.0
 */
public abstract class BaseTask implements Runnable {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTask.class);

    protected Long memberId;
    protected String taskName;

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName == null ? super.toString() : taskName;
    }

    /**
     * @param taskName the taskName to set
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * <pre>
     * Constructor
     * </pre>
     */
    public BaseTask() {
    }//end of Constructor()

    /**
     * <pre>
     * Constructor
     * </pre>
     */
    public BaseTask(Long memberId) {
        this.memberId = memberId;
    }//end of Constructor()

    /**
     * <pre>
     * Constructor
     * </pre>
     * @param taskName
     */
    public BaseTask(String taskName) {
        this.taskName = taskName;
    }//end of Constructor()

    /**
     * <pre>
     *
     * </pre>
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // Thread 실행 시 UncaughtException에 대해 catch 하기 위해 handler 등록.
        Thread.currentThread().setUncaughtExceptionHandler(new ConverterTaskExceptionHandler());

        //logger.debug("[{}] is started.", getTaskName());
        // 사전 작업이 필요하면 beforeRun() 메소드 구현 후 호출

        taskRun();

        // 사후 작업이 필요하면 afterRun() 메소드 구현 후 호출
        //logger.debug("[{}] is completed.", getTaskName());
    }

    @Override
    public String toString() {
        return getTaskName();
    }

    protected abstract void taskRun();

}
//end of BaseTask.java