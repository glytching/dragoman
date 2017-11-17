/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.glytching.dragoman.web.subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AsOf {
    private static final Logger logger = LoggerFactory.getLogger(AsOf.class);

    private final String asOfField;
    private final String asOfFieldPattern;
    private final AsOfFormatter asOfFormatter;
    private LocalDateTime lastRead;

    AsOf(
            String asOfField,
            String asOfFieldPattern,
            LocalDateTime lastRead,
            AsOfFormatter asOfFormatter) {
        this.asOfField = asOfField;
        this.asOfFormatter = asOfFormatter;
        this.asOfFieldPattern = asOfFormatter.validate(asOfFieldPattern);
        this.lastRead = lastRead;
    }

    public String applyAsOf(String where) {
        String asOfPredicate = asOfField + " > " + asOfFormatter.format(asOfFieldPattern, lastRead);

        // increment the asOf for the next time around
        this.lastRead = LocalDateTime.now();

        logger.info("Derived asOf predicate: {}", asOfPredicate);
        return isBlank(where) ? asOfPredicate : (where + " and " + asOfPredicate);
    }
}
