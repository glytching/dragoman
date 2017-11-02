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
package org.glitch.dragoman.ql.listener.mongo;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.conversions.Bson;
import org.glitch.dragoman.ql.domain.OrderBy;
import org.glitch.dragoman.ql.listener.AbstractOrderByClauseListener;

import java.util.List;

public class MongoOrderByClauseListener extends AbstractOrderByClauseListener<Bson> {

    @Override
    public Bson get() {
        BsonDocument orderByObject = new BsonDocument();
        List<OrderBy> orderBys = getOrderBys();
        for (OrderBy orderBy : orderBys) {
            orderByObject.put(orderBy.getName(), new BsonInt32(orderBy.isAscending() ? 1 : -1));
        }
        return orderByObject;
    }
}