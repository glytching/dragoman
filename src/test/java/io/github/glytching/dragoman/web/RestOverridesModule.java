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
package io.github.glytching.dragoman.web;

import com.google.inject.AbstractModule;
import io.github.glytching.dragoman.authentication.AuthenticationDao;
import io.github.glytching.dragoman.dataset.DatasetDao;
import io.github.glytching.dragoman.reader.Reader;
import io.github.glytching.dragoman.repository.router.RepositoryRouter;

import static org.mockito.Mockito.mock;

public class RestOverridesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RepositoryRouter.class).toInstance(mock(RepositoryRouter.class));
        bind(DatasetDao.class).toInstance(mock(DatasetDao.class));
        bind(AuthenticationDao.class).toInstance(mock(AuthenticationDao.class));
        bind(Reader.class).toInstance(mock(Reader.class));
    }
}