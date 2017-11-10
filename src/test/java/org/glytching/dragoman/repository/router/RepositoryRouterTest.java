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
package org.glytching.dragoman.repository.router;

import com.google.common.collect.Sets;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.glytching.dragoman.util.TestFixture.anyDataset;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class RepositoryRouterTest {

    @Mock
    private Repository<Map<String, Object>> repositoryA;
    @Mock
    private Repository<Map<String, Object>> repositoryB;

    private RepositoryRouter router;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        router = new RepositoryRouterImpl(Sets.newHashSet(repositoryA, repositoryB));
    }

    @Test
    public void canRouteADatasetToARepository() {
        Dataset dataset = anyDataset();
        when(repositoryA.appliesTo(dataset)).thenReturn(true);
        when(repositoryB.appliesTo(dataset)).thenReturn(false);

        Repository<Map<String, Object>> actual = router.get(dataset);

        assertThat(actual, is(repositoryA));
    }

    @Test
    public void willFailIfNoRepositoryExistsForTheGivenDataset() {
        Dataset dataset = anyDataset();

        when(repositoryA.appliesTo(dataset)).thenReturn(false);
        when(repositoryB.appliesTo(dataset)).thenReturn(false);

        NoRepositoryAvailableException actual = assertThrows(NoRepositoryAvailableException.class,
                () -> router.get(dataset));
        assertThat(actual.getMessage(), is("No repository exists for dataset: " + dataset.getId()));
    }
}