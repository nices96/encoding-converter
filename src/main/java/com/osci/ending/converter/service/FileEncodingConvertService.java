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
package com.osci.ending.converter.service;

import com.osci.ending.converter.threadpool.executor.ConverterThreadPoolExecutor;
import com.osci.ending.converter.threadpool.task.FileEncodingConverterTask;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author SangCheon Park
 * @version 1.0
 */
@Component
public class FileEncodingConvertService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(FileEncodingConvertService.class);

    @Value("${encoding.converter.source.dir}")
    private String sourceDir;

    @Value("${encoding.converter.target.dir}")
    private String targetDir;

    @Value("#{'${encoding.converter.file.extensions}'.split(',')}")
    private List<String> extensions;

    @Autowired
    private ConverterThreadPoolExecutor executor;

    /**
     * After properties set.
     *
     * @throws Exception the exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        convert();
    }

    /**
     * Convert.
     */
    private void convert() {
        Assert.notNull(sourceDir, "encoding.converter.source.dir must not be null");
        Assert.notNull(targetDir, "encoding.converter.target.dir must not be null");

        File source = new File(sourceDir);
        Assert.isTrue(source.exists(), source + " does not exist.");

        logger.debug("Ready to encoding convert [{}] to [{}]", sourceDir, targetDir);

        convert(source);

        System.exit(0);
    }

    /**
     * Convert.
     *
     * @param source the source
     */
    private void convert(File source) {
        if (source.isDirectory()) {
            for (File f : source.listFiles()) {
                // call convert() recursively
                convert(f);
            }
        } else {
            String extension = FilenameUtils.getExtension(source.getName());

            // ignore case
            if (extensions.stream().anyMatch(extension::equalsIgnoreCase)) {
                executor.execute(new FileEncodingConverterTask(source, sourceDir, targetDir));
            }
        }
    }
}
//end of FileEncodingConvertService.java