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

import io.github.glytching.dragoman.dataset.Dataset;
import io.github.glytching.dragoman.http.HttpClientAdapter;
import io.github.glytching.dragoman.ql.listener.groovy.Filter;
import io.github.glytching.dragoman.ql.listener.groovy.GroovyFactory;
import io.github.glytching.dragoman.ql.listener.groovy.Mapper;
import io.github.glytching.dragoman.repository.Repository;
import io.github.glytching.dragoman.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.inject.Inject;
import java.util.Map;

/**
 * An implementation of {@link Repository} for HTTP data sources.
 */
public class HttpRepository implements Repository<Map<String, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(HttpRepository.class);

    private final GroovyFactory groovyFactory;
    private final HttpClientAdapter httpClientAdapter;
    private final ResponsePostProcessorFactory responsePostProcessorFactory;
    private final UrlUtils urlUtils;

    @Inject
    public HttpRepository(
            GroovyFactory groovyFactory,
            HttpClientAdapter httpClientAdapter,
            ResponsePostProcessorFactory responsePostProcessorFactory,
            UrlUtils urlUtils) {
        this.groovyFactory = groovyFactory;
        this.httpClientAdapter = httpClientAdapter;
        this.responsePostProcessorFactory = responsePostProcessorFactory;
        this.urlUtils = urlUtils;
    }

    /**
     * Reads data from the source identified by {@link Dataset#source} and applies the projections and
     * predicates defined by the given {@code select} and {@code where}. This repository cannot apply
     * these projections and predicates at source since all it knows about the data source is a URL so
     * instead it reads all the data referenced by that URL and then applies projections and
     * predicates on the client side. Clearly this may present performance and scaleability issues so
     * it is advisable that {@link Dataset#source} be declared in such a way as to limit these issues.
     * In time this approach may be revisited to declare more information about the source in the
     * {@link Dataset} so as to allow smarter usage of the HTTP sources.
     *
     * @param dataset the {@link Dataset} to be queried
     * @param select the projections (if any) to be applied to the data in the requested dataset
     * @param where the predicates (if any) to be used when filtering the requested dataset
     * @param orderBy the ordering (if any) to be applied to the data read from the requested dataset
     * @param maxResults a limit on the number of entries to be read from the requested dataset
     *
     * @return
     */
    @Override
    public Observable<Map<String, Object>> find(
            Dataset dataset, String select, String where, String orderBy, int maxResults) {
        Mapper mapper = groovyFactory.createProjector(select);

        Filter filter = groovyFactory.createFilter(where);

        Observable<Map<String, Object>> rawResponse =
                httpClientAdapter.read(dataset.getSource(), responsePostProcessorFactory.create(dataset));

        logger.info("Start filter and map");
        Observable<Map<String, Object>> observable = rawResponse.filter(filter::filter);

        // we can only apply maxResults here because if we apply it before we filter we might have
        // nothing to
        // filter!
        if (maxResults > 0) {
            observable = observable.limit(maxResults);
        }

        logger.info("Finish filter and map");
        return observable.map(mapper::map);
    }

    @Override
    public boolean appliesTo(Dataset dataset) {
        return urlUtils.isUrl(dataset.getSource());
    }
}
