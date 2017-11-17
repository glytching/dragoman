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
package io.github.glytching.dragoman.store.http.repository;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * An implementation of {@link ResponsePostProcessor} which insists on all HTTP responses being JSON lists.
 */
public class ListAwareResponsePostProcessor implements ResponsePostProcessor {

    private final ResponsePostProcessor delegate;

    public ListAwareResponsePostProcessor() {
        this(new NoOpResponsePostProcessor());
    }

    public ListAwareResponsePostProcessor(ResponsePostProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public String postProcess(String content) {
        String postProcessed = delegate.postProcess(content);

        if (isNotBlank(postProcessed)) {
            if (!postProcessed.startsWith("[")) {
                postProcessed = "[" + postProcessed + "]";
            }
        }

        return postProcessed;
    }
}
