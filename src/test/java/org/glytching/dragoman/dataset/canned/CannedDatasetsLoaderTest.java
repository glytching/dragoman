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
package org.glytching.dragoman.dataset.canned;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glytching.dragoman.dataset.Dataset;
import org.glytching.dragoman.transform.JsonTransformer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class CannedDatasetsLoaderTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @SuppressWarnings("FieldCanBeLocal")
    private final ObjectMapper mapper = new ObjectMapper();

    private CannedDatasetsLoaderImpl loader;

    @Before
    public void setUp() {
        loader = new CannedDatasetsLoaderImpl(new JsonTransformer(mapper));
    }

    @Test
    public void canLoadCannedDatasets() {
        List<CannedDataset> actual = loader.load("/cannedDatasets");

        assertThat(actual.size(), is(2));

        Map<String, CannedDataset> byName =
                actual.stream().collect(Collectors.toMap(cannedDataset -> cannedDataset.getDataset() != null ?
                                cannedDataset.getDataset().getName() : "UNKNOWN",
                        Function.identity()));

        assertThat(byName, hasKey("This"));
        Dataset thisDataset = byName.get("This").getDataset();
        assertThat(thisDataset.getOwner(), is("Me"));
        assertThat(thisDataset.getSource(), is("a:b"));
        assertThat(thisDataset.getSubscriptionControlField(), is("updatedAt"));
        List<Map<String, Object>> theseDocuments = byName.get("This").getDocuments();
        assertThat(theseDocuments, hasSize(2));
        for (Map<String, Object> d : theseDocuments) {
            assertThat(d, hasKey(thisDataset.getSubscriptionControlField()));
        }

        assertThat(byName, hasKey("That"));
        Dataset thatDataset = byName.get("That").getDataset();
        assertThat(thatDataset.getOwner(), is("You"));
        assertThat(thatDataset.getSource(), is("http://host:1234/some/end/point"));
        assertThat(thatDataset.getSubscriptionControlField(), is("updatedAt"));
        assertThat(thatDataset.getSubscriptionControlFieldPattern(), is("L"));
        assertThat(byName.get("That").getDocuments(), nullValue());
    }

    @Test
    public void willNotLoadAnythingIfTheCannedDatasetsDirectoryHasNoTableOfContents() {
        List<CannedDataset> actual = loader.load("/cannedDatasetWithNoTableOfContents");

        assertThat(actual.size(), is(0));
    }

    @Test
    public void willIgnoreIfTheTableOfContentsRefersToANonExistentCannedDataset() {
        List<CannedDataset> actual = loader.load("/cannedDatasetWithTableOfContentsButNoDataset");

        assertThat(actual.size(), is(0));
    }

    @Test
    public void willIgnoreAnUnlistedFileInACannedDatasetsDirectory() {
        List<CannedDataset> actual = loader.load("/cannedDatasetWithUnlistedFile");

        assertThat(actual.size(), is(1));

        Map<String, CannedDataset> byName =
                actual.stream().collect(Collectors.toMap(cannedDataset -> cannedDataset.getDataset() != null ?
                                cannedDataset.getDataset().getName() : "UNKNOWN",
                        Function.identity()));

        assertThat(byName, hasKey("This"));
        Dataset thisDataset = byName.get("This").getDataset();
        assertThat(thisDataset.getOwner(), is("Me"));
        assertThat(thisDataset.getSource(), is("http://host:1234/some/end/point"));
        assertThat(thisDataset.getSubscriptionControlField(), nullValue());
        assertThat(thisDataset.getSubscriptionControlFieldPattern(), nullValue());
    }

    @Test
    public void cannotReadDodgyDescriptor() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Failed to read files from");

        loader.load("/cannedDatasetWithDodgyContent");
    }

    @Test
    public void cannotReadADatasetWhichContainsDocumentsButNoDescriptor() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Failed to read files from");

        loader.load("/cannedDatasetWithDocumentsButNoDescriptor");
    }
}