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
package org.glytching.dragoman.ql.listener.groovy;

import java.util.Map;

/**
 * Interface which defines the map feature for dynamically generated Groovy classes.
 */
public interface Mapper {

    /**
     * Applies a selection function to the given {@code object}.
     *
     * @param incoming the object to be transformed
     *
     * @return the transformed object
     */
    Map<String, Object> map(Object incoming);
}
