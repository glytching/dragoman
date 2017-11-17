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
package io.github.glytching.dragoman.store.mongo.authentication;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import io.github.glytching.dragoman.authentication.AuthenticationDao;
import io.github.glytching.dragoman.authentication.PasswordUtil;
import io.github.glytching.dragoman.authentication.User;
import io.github.glytching.dragoman.configuration.guice.AuthenticationModule;
import io.github.glytching.dragoman.configuration.guice.ConfigurationModule;
import io.github.glytching.dragoman.store.mongo.AbstractMongoDBTest;
import io.github.glytching.dragoman.store.mongo.MongoProvider;
import io.github.glytching.dragoman.store.mongo.repository.MongoOverrideModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.github.glytching.dragoman.util.TestFixture.aString;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

public class MongoAuthenticationDaoTest extends AbstractMongoDBTest {

    @Inject
    private AuthenticationDao authenticationDao;
    @Inject
    private MongoProvider mongoProvider;
    @Inject
    private PasswordUtil passwordUtil;

    @BeforeEach
    public void setUp() {
        Injector injector =
                Guice.createInjector(Modules.override(new AuthenticationModule(), new ConfigurationModule()).with(new MongoOverrideModule()));
        injector.injectMembers(this);

        when(mongoProvider.provide()).thenReturn(getMongoClient());
    }

    @Test
    public void canCreateAndGetAUser() {
        String name = aString();
        String password = "aPassword";

        authenticationDao.createUser(name, password);

        User user = authenticationDao.getUser(name, password);

        assertThat(user.getName(), is(name));
        assertThat(user.getHashedPassword(), is(passwordUtil.toHash(password)));
    }

    @Test
    public void willReturnNullIfTheRequestedUserDoesNotExist() {
        String name = aString();
        String password = "aPassword";

        User user = authenticationDao.getUser(name, password);

        assertThat(user, nullValue());
    }

    @Test
    public void existsIfTheAuthenticationStoreContainsARecordForTheGivenUserName() {
        String name = aString();
        String password = "aPassword";

        authenticationDao.createUser(name, password);

        assertThat(authenticationDao.exists(name), is(true));
    }

    @Test
    public void doesNotExistIfTheAuthenticationStoreDoesNotContainARecordForTheGivenUserName() {
        String name = aString();

        assertThat(authenticationDao.exists(name), is(false));
    }

    @Test
    public void isValidIfUsernameAndPasswordMatch() {
        String name = aString();
        String password = "aPassword";

        authenticationDao.createUser(name, password);

        assertThat(authenticationDao.isValid(name, password), is(true));
    }

    @Test
    public void isInvalidIfUsernameAndPasswordDoNotMatch() {
        String name = aString();
        String password = "aPassword";

        authenticationDao.createUser(name, password);

        assertThat(authenticationDao.isValid(name, "..."), is(false));
    }

    @Test
    public void cannotCreateAUserIfTheUserNameIsAlreadyTaken() {
        String name = aString();
        String password = "aPassword";

        authenticationDao.createUser(name, password);

        try {
            authenticationDao.createUser(name, password);
            fail("Expected user creation to fail because the given user already exists!");
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage(), is(format("A user already exists for: %s!", name)));
        }
    }
}