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
package io.github.glytching.dragoman.dataset.canned;

import com.google.common.collect.Lists;
import io.github.glytching.dragoman.dataset.Dataset;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple wrapper over a {@link Dataset} and (optionally) dataset contents.
 */
public class CannedDataset {
    private Dataset dataset;
    private List<Map<String, Object>> documents;

    public CannedDataset() {
    }

    public CannedDataset(Dataset dataset, List<Map<String, Object>> documents) {
        this.dataset = dataset;
        this.documents = documents;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public List<Map<String, Object>> getDocuments() {
        return documents;
    }

    public void add(Map<String, Object> document) {
        if (documents == null) {
            documents = Lists.newArrayList();
        }
        documents.add(document);
    }

    public CannedDataset finish() {
        // validate the canned dataset
        Objects.requireNonNull(dataset, "A canned dataset must contain a dataset!");

        // populate each document with a subscriptionControlField if the dataset is defined with such a
        // field
        if (dataset.getSubscriptionControlField() != null && documents != null) {
            String subscriptionControlField = dataset.getSubscriptionControlField();
            for (Map<String, Object> document : documents) {
                // populate if either (a) the document has no subscriptionControlField or (b) the document
                // contains
                // a subscriptionControlField which is not populated with a date
                if (!document.containsKey(subscriptionControlField)
                        || !(document.get(subscriptionControlField) instanceof Date)) {
                    document.put(subscriptionControlField, new Date());
                }
            }
        }
        return this;
  }
}
