package org.glitch.dragoman.store.mongo.authentication;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.glitch.dragoman.authentication.AuthenticationDao;
import org.glitch.dragoman.authentication.PasswordUtil;
import org.glitch.dragoman.authentication.User;
import org.glitch.dragoman.configuration.guice.AuthenticationModule;
import org.glitch.dragoman.configuration.guice.ConfigurationModule;
import org.glitch.dragoman.store.mongo.AbstractMongoDBTest;
import org.glitch.dragoman.store.mongo.MongoProvider;
import org.glitch.dragoman.store.mongo.repository.MongoOverrideModule;
import org.glitch.dragoman.util.RandomValues;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class MongoAuthenticationDaoTest extends AbstractMongoDBTest {

    @Inject
    private AuthenticationDao authenticationDao;
    @Inject
    private MongoProvider mongoProvider;
    @Inject
    private PasswordUtil passwordUtil;

    @Before
    public void setUp() {
        Injector injector =
                Guice.createInjector(Modules.override(new AuthenticationModule(), new ConfigurationModule()).with(new MongoOverrideModule()));
        injector.injectMembers(this);

        when(mongoProvider.provide()).thenReturn(getMongoClient());
    }

    @Test
    public void canCreateAndGetAUser() {
        String name = RandomValues.aString();
        String password = "aPassword";

        authenticationDao.createUser(name, password);

        User user = authenticationDao.getUser(name, password);

        assertThat(user.getName(), is(name));
        assertThat(user.getHashedPassword(), is(passwordUtil.toHash(password)));
    }

    @Test
    public void isValidIfUsernameAndPasswordMatch() {
        String name = RandomValues.aString();
        String password = "aPassword";

        authenticationDao.createUser(name, password);

        assertThat(authenticationDao.isValid(name, password), is(true));
    }

    @Test
    public void isInvalidIfUsernameAndPasswordDoNotMatch() {
        String name = RandomValues.aString();
        String password = "aPassword";

        authenticationDao.createUser(name, password);

        assertThat(authenticationDao.isValid(name, "..."), is(false));
    }

    @Test
    public void cannotCreateAUserIfTheUserNameIsAlreadyTaken() {
        String name = RandomValues.aString();
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