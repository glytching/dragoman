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
package io.github.glytching.dragoman.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * The util provides a secure one-way hash function so we cannot reverse it therefore we assert
 * that:
 *
 * <ul>
 *   <li>It produced something other than the given password
 *   <li>It always produces the same value for the same input
 * </ul>
 */
public class PasswordUtilTest {

    private PasswordUtil passwordUtil;

    @BeforeEach
    public void setUp() {
        passwordUtil = new PasswordUtil();
    }

    @Test
    public void willProduceSomethingOtherThanTheGivenPassword() {
        String password = "foo";

        String hash = passwordUtil.toHash(password);

        assertThat(hash, not(is(password)));
    }

    @Test
    public void willAlwaysProduceTheSameHashForAGivenPassword() {
        String password = "foo";

        String hash = passwordUtil.toHash(password);

        assertThat(hash, is(passwordUtil.toHash(password)));
        assertThat(hash, is(passwordUtil.toHash(password)));
    }
}
