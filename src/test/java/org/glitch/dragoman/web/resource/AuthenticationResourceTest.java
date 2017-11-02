package org.glitch.dragoman.web.resource;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.glitch.dragoman.authentication.AuthenticationDao;
import org.glitch.dragoman.http.HttpResponse;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.glitch.dragoman.web.WebServerUtils.withApplicationName;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(VertxUnitRunner.class)
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

    @Ignore
    @Test
    public void canLogout() {
        HttpResponse response = post("logout", null);

        assertThat(response.getStatusCode(), is(HttpResponseStatus.FORBIDDEN.code()));
        assertThat(response.getStatusMessage(), is(HttpResponseStatus.UNAUTHORIZED.reasonPhrase()));
    }
}