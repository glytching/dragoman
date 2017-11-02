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
package org.glitch.dragoman.store.http.repository;

import org.glitch.dragoman.dataset.Dataset;
import org.glitch.dragoman.http.HttpClientAdapter;
import org.glitch.dragoman.ql.listener.groovy.Filter;
import org.glitch.dragoman.ql.listener.groovy.GroovyFactory;
import org.glitch.dragoman.ql.listener.groovy.GroovyFactoryException;
import org.glitch.dragoman.ql.listener.groovy.Mapper;
import org.glitch.dragoman.repository.Repository;
import org.glitch.dragoman.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;
import java.util.Map;

public class HttpRepository implements Repository<Map<String, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(HttpRepository.class);

    private final GroovyFactory groovyFactory;
    private final HttpClientAdapter httpClientAdapter;
    private final ResponsePostProcessorFactory responsePostProcessorFactory;
    private final UrlUtils urlUtils;

    @Inject
    public HttpRepository(GroovyFactory groovyFactory, HttpClientAdapter httpClientAdapter,
                          ResponsePostProcessorFactory responsePostProcessorFactory, UrlUtils urlUtils) {
        this.groovyFactory = groovyFactory;
        this.httpClientAdapter = httpClientAdapter;
        this.responsePostProcessorFactory = responsePostProcessorFactory;
        this.urlUtils = urlUtils;
    }

    @Override
    public Observable<Map<String, Object>> find(Dataset dataset, String select, String where, String orderBy,
                                                int maxResults) {
        try {
            Mapper mapper = groovyFactory.createProjector(select);
            Filter filter = groovyFactory.createFilter(where);

            Observable<Map<String, Object>> rawResponse = httpClientAdapter.read(dataset.getSource(),
                    responsePostProcessorFactory.create(dataset));

            logger.info("Start filter and map");
            Observable<Map<String, Object>> observable = rawResponse.filter(filter::filter);

            // we can only apply maxResults here because if we apply it before we filter we might have nothing to
            // filter!
            if (maxResults > 0) {
                observable = observable.limit(maxResults);
            }

            logger.info("Finish filter and map");
            return observable.map(mapper::map);
        } catch (GroovyFactoryException ex) {
            throw new RuntimeException("Failed to be groovy!", ex);
        }
    }

    @Override
    public boolean appliesTo(Dataset dataset) {
        return urlUtils.isUrl(dataset.getSource());
    }
}