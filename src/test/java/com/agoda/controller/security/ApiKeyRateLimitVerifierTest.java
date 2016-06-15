package com.agoda.controller.security;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by Tkachi on 6/15/2016.
 */
public class ApiKeyRateLimitVerifierTest {

    private String apiKeyWithDefaultLimit = "default_limit_key";
    private String apiKeyWithCustomLimit = "custom_limit_key";
    private long customLimit = 10l;

    private ApiKeyRateLimitVerifier testee = new ApiKeyRateLimitVerifier();

    @Before
    public void init(){
        testee.addApiKey(apiKeyWithDefaultLimit);
        testee.addApiKeyWithLimit(apiKeyWithCustomLimit, customLimit);
    }

    @Test
    public void should_return_true_when_key_exists(){
        assertThat(testee.verify(apiKeyWithDefaultLimit), is(true));
    }

    @Test
    public void should_return_false_when_key_does_not_exist(){
        assertThat(testee.verify("some_random_key"), is(false));
    }

    @Test
    public void should_return_false_when_key_limit_exicided(){
        testee.verify(apiKeyWithDefaultLimit);
        assertThat(testee.verify(apiKeyWithDefaultLimit), is(false));
    }

    @Test
    public void should_return_true_when_respect_default_key_limit() throws InterruptedException {
        testee.verify(apiKeyWithDefaultLimit);
        Thread.sleep(ApiKeyRateLimitVerifier.DEFAULT_LIMIT_SEC*1000);
        assertThat(testee.verify(apiKeyWithDefaultLimit), is(true));
    }

    @Test
    public void should_return_true_when_respect_custom_key_limit() throws InterruptedException {
        testee.verify(apiKeyWithCustomLimit);
        Thread.sleep(customLimit*1000);
        assertThat(testee.verify(apiKeyWithDefaultLimit), is(true));
    }

    @Test
    public void should_return_true_when_suspending_time_past() throws InterruptedException {
        long suspendingTimeSec = customLimit*2;
        testee.setSuspendingKeyTimeSec(suspendingTimeSec);

        assertThat("First check - returns true", testee.verify(apiKeyWithCustomLimit), is(true));
        assertThat("Second check - returns false and suspend", testee.verify(apiKeyWithCustomLimit), is(false));

        Thread.sleep(customLimit*1000);
        assertThat("Third check - still false as it's suspended", testee.verify(apiKeyWithCustomLimit), is(false));
        Thread.sleep(customLimit*1000);

        assertThat("Suspending time should have elapsed - return true", testee.verify(apiKeyWithDefaultLimit), is(true));
    }

}