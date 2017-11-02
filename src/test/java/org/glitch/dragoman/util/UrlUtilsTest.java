package org.glitch.dragoman.util;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UrlUtilsTest {

    private final UrlUtils urlUtils = new UrlUtils();

    @Test
    public void canTellWhetherAStringIsAUrl() {
        assertThat(urlUtils.isUrl("a:b"), is(false));

        assertThat(urlUtils.isUrl("http://aHost:12345/"), is(true));

        assertThat(urlUtils.isUrl("https://a:1234"), is(true));

        assertThat(urlUtils.isUrl("http://a:1234/some/end/point?a=b"), is(true));
    }
}