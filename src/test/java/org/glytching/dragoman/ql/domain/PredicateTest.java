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
package org.glytching.dragoman.ql.domain;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PredicateTest {

    @Test
    public void canHandleSimpleEquals() {
        Predicate predicate = new Predicate("a", "=", "b");

        assertThat(predicate.isQuoted(), is(false));
        assertThat(predicate.isBetween(), is(false));
        assertThat(predicate.isNotBetween(), is(false));
        assertThat(predicate.isIn(), is(false));
        assertThat(predicate.isNotIn(), is(false));
        assertThat(predicate.isLike(), is(false));
        assertThat(predicate.isNotLike(), is(false));
        assertThat(predicate.isNull(), is(false));
        assertThat(predicate.isNotNull(), is(false));
        assertThat(predicate.isGreaterThan(), is(false));
        assertThat(predicate.isGreaterThanOrEqualTo(), is(false));
        assertThat(predicate.isLessThan(), is(false));
        assertThat(predicate.isLessThanOrEqualTo(), is(false));
        assertThat(predicate.isNegated(), is(false));
        assertThat(predicate.isNotEquals(), is(false));
        assertThat(predicate.isEquals(), is(true));
        assertThat(predicate.toString(), is("a = b"));
    }

    @Test
    public void canHandleQuotedIn() {
        Predicate predicate = new Predicate("a", "in", true, false, "b", "c");

        assertThat(predicate.isQuoted(), is(true));
        assertThat(predicate.isBetween(), is(false));
        assertThat(predicate.isNotBetween(), is(false));
        assertThat(predicate.isIn(), is(true));
        assertThat(predicate.isNotIn(), is(false));
        assertThat(predicate.isLike(), is(false));
        assertThat(predicate.isNotLike(), is(false));
        assertThat(predicate.isNull(), is(false));
        assertThat(predicate.isNotNull(), is(false));
        assertThat(predicate.isGreaterThan(), is(false));
        assertThat(predicate.isGreaterThanOrEqualTo(), is(false));
        assertThat(predicate.isLessThan(), is(false));
        assertThat(predicate.isLessThanOrEqualTo(), is(false));
        assertThat(predicate.isNegated(), is(false));
        assertThat(predicate.isNotEquals(), is(false));
        assertThat(predicate.isEquals(), is(false));
        assertThat(predicate.toString(), is("a in ('b', 'c')"));
    }

    @Test
    public void canHandleNumeric() {
        Predicate predicate = new Predicate("a", ">=", "5");

        assertThat(predicate.isQuoted(), is(false));
        assertThat(predicate.isBetween(), is(false));
        assertThat(predicate.isNotBetween(), is(false));
        assertThat(predicate.isIn(), is(false));
        assertThat(predicate.isNotIn(), is(false));
        assertThat(predicate.isLike(), is(false));
        assertThat(predicate.isNotLike(), is(false));
        assertThat(predicate.isNull(), is(false));
        assertThat(predicate.isNotNull(), is(false));
        assertThat(predicate.isGreaterThan(), is(false));
        assertThat(predicate.isGreaterThanOrEqualTo(), is(true));
        assertThat(predicate.isLessThan(), is(false));
        assertThat(predicate.isLessThanOrEqualTo(), is(false));
        assertThat(predicate.isNegated(), is(false));
        assertThat(predicate.isNotEquals(), is(false));
        assertThat(predicate.isEquals(), is(false));
        assertThat(predicate.toString(), is("a >= 5"));
    }

    @Test
    public void canHandleBetween() {
        Predicate predicate = new Predicate("a", "between", "5", "10");

        assertThat(predicate.isQuoted(), is(false));
        assertThat(predicate.isBetween(), is(true));
        assertThat(predicate.isNotBetween(), is(false));
        assertThat(predicate.isIn(), is(false));
        assertThat(predicate.isNotIn(), is(false));
        assertThat(predicate.isLike(), is(false));
        assertThat(predicate.isNotLike(), is(false));
        assertThat(predicate.isNull(), is(false));
        assertThat(predicate.isNotNull(), is(false));
        assertThat(predicate.isGreaterThan(), is(false));
        assertThat(predicate.isGreaterThanOrEqualTo(), is(false));
        assertThat(predicate.isLessThan(), is(false));
        assertThat(predicate.isLessThanOrEqualTo(), is(false));
        assertThat(predicate.isNegated(), is(false));
        assertThat(predicate.isNotEquals(), is(false));
        assertThat(predicate.isEquals(), is(false));
        assertThat(predicate.toString(), is("a between 5 and 10"));
    }

    @Test
    public void canHandleLike() {
        Predicate predicate = new Predicate("a", "like", true, false, "foo%");

        assertThat(predicate.isQuoted(), is(true));
        assertThat(predicate.isBetween(), is(false));
        assertThat(predicate.isNotBetween(), is(false));
        assertThat(predicate.isIn(), is(false));
        assertThat(predicate.isNotIn(), is(false));
        assertThat(predicate.isLike(), is(true));
        assertThat(predicate.isNotLike(), is(false));
        assertThat(predicate.isNull(), is(false));
        assertThat(predicate.isNotNull(), is(false));
        assertThat(predicate.isGreaterThan(), is(false));
        assertThat(predicate.isGreaterThanOrEqualTo(), is(false));
        assertThat(predicate.isLessThan(), is(false));
        assertThat(predicate.isLessThanOrEqualTo(), is(false));
        assertThat(predicate.isNegated(), is(false));
        assertThat(predicate.isNotEquals(), is(false));
        assertThat(predicate.isEquals(), is(false));
        assertThat(predicate.toString(), is("a like 'foo%'"));
    }

    @Test
    public void canHandleIsNull() {
        Predicate predicate = new Predicate("a", "is null");

        assertThat(predicate.isQuoted(), is(false));
        assertThat(predicate.isBetween(), is(false));
        assertThat(predicate.isNotBetween(), is(false));
        assertThat(predicate.isIn(), is(false));
        assertThat(predicate.isNotIn(), is(false));
        assertThat(predicate.isLike(), is(false));
        assertThat(predicate.isNotLike(), is(false));
        assertThat(predicate.isNull(), is(true));
        assertThat(predicate.isNotNull(), is(false));
        assertThat(predicate.isGreaterThan(), is(false));
        assertThat(predicate.isGreaterThanOrEqualTo(), is(false));
        assertThat(predicate.isLessThan(), is(false));
        assertThat(predicate.isLessThanOrEqualTo(), is(false));
        assertThat(predicate.isNegated(), is(false));
        assertThat(predicate.isNotEquals(), is(false));
        assertThat(predicate.isEquals(), is(false));
        assertThat(predicate.toString(), is("a is null"));
    }
}