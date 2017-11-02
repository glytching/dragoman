package org.glitch.dragoman.dataset.canned;

import com.google.common.collect.Lists;
import org.glitch.dragoman.dataset.Dataset;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        // populate each document with a subscriptionControlField if the dataset is defined with such a field
        if (dataset.getSubscriptionControlField() != null && documents != null) {
            String subscriptionControlField = dataset.getSubscriptionControlField();
            for (Map<String, Object> document : documents) {
                // populate if either (a) the document has no subscriptionControlField or (b) the document contains
                // a subscriptionControlField which is not populated with a date
                if (!document.containsKey(subscriptionControlField) || !(document.get(subscriptionControlField)
                        instanceof Date)) {
                    document.put(subscriptionControlField, new Date());
                }
            }
        }
        return this;
    }
}
