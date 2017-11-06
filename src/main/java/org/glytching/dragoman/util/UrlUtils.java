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
package org.glytching.dragoman.util;

import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * Utility class for URL related code.
 */
public class UrlUtils {

    private final UrlValidator urlValidator;

    public UrlUtils() {
        this.urlValidator = new UrlValidator(new RegexValidator(".*"), 0L);
    }

    /**
     * Is the given {@code incoming} a valid URL?
     *
     * @param incoming a (putative) URL-as-string
     *
     * @return true if the {@code incoming} is a valid URL, false otherwise
     */
    public boolean isUrl(String incoming) {
        return urlValidator.isValid(incoming);
    }
}