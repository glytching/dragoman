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
package org.glytching.dragoman.web.resource;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import org.glytching.dragoman.authentication.AuthenticationDao;
import org.glytching.dragoman.http.HttpResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.glytching.dragoman.web.WebServerUtils.withApplicationName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class AuthenticationResourceTest extends AbstractResourceTest {

    @Inject
    private AuthenticationDao authenticationDao;

    @Test
    public void canLoginIfTheCredentialsAreValidForAnExistingUser() {
        String userName = "aName";
        String password = "aPassword";

        when(authenticationDao.exists(userName)).thenReturn(true);
        when(authenticationDao.isValid(userName, password)).thenReturn(true);

        String payload = "{ \"username\": \"" + userName + "\", \"password\": \"" + password + "\" }";

        HttpResponse response = post("login", payload);

        assertThat(response.getStatusCode(), is(HttpResponseStatus.OK.code()));
        assertThat(response.getStatusMessage(), is(HttpResponseStatus.OK.reasonPhrase()));
        // we expect the session cookie to have been set
        assertThat(response.getHeaders(), hasKey("set-cookie"));
        // we expect to be redirected to the about page
        assertThat(response.getUrl(), Matchers.endsWith("/dragoman/about.hbs"));

        // existing user so the create call should not have been invoked
        verify(authenticationDao, never()).createUser(anyString(), anyString());
    }

    @Test
    public void willCreateANewUserIfNoneExists() {
        String userName = "aName";
        String password = "aPassword";

        when(authenticationDao.exists(userName)).thenReturn(false);

        String payload = "{ \"username\": \"" + userName + "\", \"password\": \"" + password + "\" }";

        HttpResponse response = post("login", payload);

        assertThat(response.getStatusCode(), is(HttpResponseStatus.OK.code()));
        assertThat(response.getStatusMessage(), is(HttpResponseStatus.OK.reasonPhrase()));
        // we expect the session cookie to have been set
        assertThat(response.getHeaders(), hasKey("set-cookie"));
        // we expect to be redirected to the about page
        assertThat(response.getUrl(), Matchers.endsWith("/dragoman/about.hbs"));

        verify(authenticationDao, never()).isValid(userName, password);
        verify(authenticationDao, times(1)).createUser(userName, password);
    }

    @Test
    public void cannotLoginIfTheCredentialsAreInvalid() {
        String userName = "aName";
        String password = "aPassword";

        when(authenticationDao.exists(userName)).thenReturn(true);
        when(authenticationDao.isValid(userName, password)).thenReturn(false);

        String payload = "{ \"username\": \"" + userName + "\", \"password\": \"" + password + "\" }";

        String endpoint = "login";
        HttpResponse response = post(endpoint, payload);

        assertThat(response.getStatusCode(), is(HttpResponseStatus.UNAUTHORIZED.code()));
        assertThat(response.getStatusMessage(), is(HttpResponseStatus.UNAUTHORIZED.reasonPhrase()));

        JsonObject errorResponse = new JsonObject(response.getPayload());

        assertThat(errorResponse.getString("timestamp"), notNullValue());
        assertThat(errorResponse.getInteger("statusCode"), is(HttpResponseStatus.UNAUTHORIZED.code()));
        assertThat(errorResponse.getString("statusMessage"), is(HttpResponseStatus.UNAUTHORIZED.reasonPhrase()));
        assertThat(errorResponse.getString("path"), is(withApplicationName(endpoint)));
        assertThat(errorResponse.getString("message"), containsString("Invalid credentials!"));
        assertThat(errorResponse.getString("stackTrace"), notNullValue());

        // existing user so the create call should not have been invoked
        verify(authenticationDao, never()).createUser(anyString(), anyString());
    }

    @Disabled
    @Test
    public void canLogout() {
        HttpResponse response = post("logout", null);

        assertThat(response.getStatusCode(), is(HttpResponseStatus.FORBIDDEN.code()));
        assertThat(response.getStatusMessage(), is(HttpResponseStatus.UNAUTHORIZED.reasonPhrase()));
    }
}