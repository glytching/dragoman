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
package org.glitch.dragoman.transform;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;

public class JsonTransformer implements Transformer<String> {

    private final ObjectMapper objectMapper;

    @Inject
    public JsonTransformer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String transform(Object from) {
        try {
            return objectMapper.writeValueAsString(from);
        } catch (Exception ex) {
            throw new TransformerException("Failed to serialise content!", ex);
        }
    }

    @Override
    public <T> T transform(Class<T> clazz, String from) {
        try {
            return objectMapper.readValue(from, clazz);
        } catch (Exception ex) {
            throw new TransformerException("Failed to deserialise content!", ex);
        }
    }
}