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
package com.osci.ending.converter.threadpool.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * Runnable Task 실행 시 발생되는 UncaughtException을 처리하기 위한 핸들러
 * 예상되는 Exception은 task 내의 run() 내에서 구현.
 * </pre>
 *
 * @author SangCheon Park
 * @version 1.0
 */
public class ConverterTaskExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConverterTaskExceptionHandler.class);

    /**
     * <pre>
     *
     * </pre>
     * @param t
     * @param e
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.info("Exception occurred during [{}] thread was running.", t.getName());
        logger.error("[" + t.getName() + "]'s Exception detail => ", e);

        // TODO task 실행시 발생된 UncaughtException에 대하여 공통적으로 처리해야할 부분이 있을 경우 구현한다.
    }

}
//end of ConverterTaskExceptionHandler.java