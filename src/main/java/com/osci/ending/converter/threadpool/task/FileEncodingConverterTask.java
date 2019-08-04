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

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;

/**
 * <pre>
 *
 * </pre>
 *
 * @author SangCheon Park
 * @version 1.0
 */
public class FileEncodingConverterTask extends BaseTask {

    private static final Logger logger = LoggerFactory.getLogger(FileEncodingConverterTask.class);

    private static String[] SEARCH_CHAR_SET;

    static {
        // to add charset as a lowercase
        String[] temp = CharsetDetector.getAllDetectableCharsets();
        temp = ArrayUtils.add(temp, "MS949");
        temp = ArrayUtils.add(temp, "KSC5601");

        SEARCH_CHAR_SET = CharsetDetector.getAllDetectableCharsets();
        SEARCH_CHAR_SET = ArrayUtils.add(SEARCH_CHAR_SET, "MS949");
        SEARCH_CHAR_SET = ArrayUtils.add(SEARCH_CHAR_SET, "KSC5601");

        for (String charSet : temp) {
            SEARCH_CHAR_SET = ArrayUtils.add(SEARCH_CHAR_SET, charSet.toLowerCase());
        }
    }

    /**
     * The Default encoding.
     */
    private String defaultEncoding = "UTF-8";

    /**
     * The Source.
     */
    private File source;

    /**
     * The Source dir.
     */
    private String sourceDir;

    /**
     * The Target dir.
     */
    private String targetDir;

    /**
     * Instantiates a new File encoding converter task.
     *
     * @param source    the source
     * @param sourceDir the source dir
     * @param targetDir the target dir
     */
    public FileEncodingConverterTask(File source, String sourceDir, String targetDir) {
        this.source = source;
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
    }

    /**
     * Task run.
     */
    @Override
    protected void taskRun() {
        try {
            byte[] data = IOUtils.toByteArray(source.toURI());

            // 파일 내용을 defaultEncoding 타입으로 변경 후 문자열로 반환
            // new CharsetDetector().getString(data, defaultEncoding); 시 지원되지 않은 인코딩 타입일 경우 null을 리턴

            CharsetDetector detector = new CharsetDetector();
            detector.setDeclaredEncoding(defaultEncoding);
            detector.setText(data);

            CharsetMatch cm = detector.detect();

            logger.debug("[{}]'s encoding : [{}]", source.getAbsolutePath(), cm.getName());

            String fileContents = cm.getString();
            if (fileContents != null) {
                String fileName = source.getAbsolutePath();
                String fqfn = fileName.replaceAll(sourceDir, targetDir);

                File target = new File(fqfn);

                if (!target.getParentFile().exists()) {
                    target.getParentFile().mkdirs();
                }

                FileUtils.writeStringToFile(target, fileContents, defaultEncoding);
            }
        } catch (Exception e) {
            logger.error("Unhandled exception has occurred while convert encoding.", e);
            throw new RuntimeException(e);
        }
    }
}
//end of FileEncodingConverterTask.java