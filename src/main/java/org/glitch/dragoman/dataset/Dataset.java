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
package org.glitch.dragoman.dataset;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Dataset {

    private String id;
    private String name;
    private String owner;
    private String source;
    private String subscriptionControlField;
    private String subscriptionControlFieldPattern;

    public Dataset() {

    }

    public Dataset(String owner, String name, String source) {
        this(owner, name, source, null, null);
    }

    public Dataset(String owner, String name, String source, String subscriptionControlField) {
        this(owner, name, source, subscriptionControlField, null);
    }

    public Dataset(String owner, String name, String source, String subscriptionControlField,
                   String subscriptionControlFieldPattern) {
        this.owner = owner;
        this.name = name;
        this.source = source;
        this.subscriptionControlField = subscriptionControlField;
        this.subscriptionControlFieldPattern = subscriptionControlFieldPattern;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSubscriptionControlField() {
        return subscriptionControlField;
    }

    public void setSubscriptionControlField(String subscriptionControlField) {
        this.subscriptionControlField = subscriptionControlField;
    }

    public String getSubscriptionControlFieldPattern() {
        return subscriptionControlFieldPattern;
    }

    public void setSubscriptionControlFieldPattern(String subscriptionControlFieldPattern) {
        this.subscriptionControlFieldPattern = subscriptionControlFieldPattern;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}