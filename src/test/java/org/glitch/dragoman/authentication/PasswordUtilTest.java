package org.glitch.dragoman.authentication;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * The util provides a secure one-way hash function so we cannot reverse it therefore we assert that:
 * <ul>
 * <li>It produced something other than the given password</li>
 * <li>It always produces the same value for the same input</li>
 * </ul>
 */
public class PasswordUtilTest {

    private PasswordUtil passwordUtil;

    @Before
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
