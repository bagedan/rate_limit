package com.agoda.controller.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Tkachi on 6/15/2016.
 */
public class ApiKeyRateLimitVerifier implements ApiKeyVerifier {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApiKeyRateLimitVerifier.class);

    public final static long DEFAULT_LIMIT_SEC = 10;

    //5 minutes by default
    public long suspendingKeyTimeSec = 5*60;
    Map<String, Long> apiKeysAndLimits = new TreeMap<>();
    Map<String, Long> apiKeyLastUsedTime = new TreeMap<>();
    Map<String, Long> suspendedKeyWithSuspendingStartTime = new TreeMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String apiKey = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(verify(apiKey)){
            return true;
        }else{
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    /**
     * Add key with default limit 1 request per 10 seconds
     * @param apiKey
     */
    public void addApiKey(String apiKey){
        apiKeysAndLimits.put(apiKey, DEFAULT_LIMIT_SEC);
    }

    /**
     * Add key with custom limit 1 request per limitTimeSec
     * @param key
     * @param limitTimeSec
     */
    public void addApiKeyWithLimit(String key, Long limitTimeSec){
        apiKeysAndLimits.put(key, limitTimeSec);
    }

    @Override
    public boolean verify(String apiKey) {
        if(isKeyExist(apiKey)){
            if(isKeySuspended(apiKey)){
                return false;
            }else{
                if(isKeyLimitExceeded(apiKey)){
                    suspendKey(apiKey);
                    LOGGER.info("Key limit exceeded for key [{}]", apiKey);
                    return false;
                }else{
                    LOGGER.debug("Key [{}] is valid", apiKey);
                    updateLastUsedTime(apiKey);
                    return true;
                }
            }
        }else {
            LOGGER.info("Key [{}] is not valid", apiKey);
            return false;
        }
    }

    private void updateLastUsedTime(String apiKey) {
        apiKeyLastUsedTime.put(apiKey, new Date().getTime());
    }

    private void suspendKey(String apiKey) {
        suspendedKeyWithSuspendingStartTime.put(apiKey, new Date().getTime());
    }

    private boolean isKeyExist(String apiKey) {
        return apiKeysAndLimits.containsKey(apiKey);
    }

    private boolean isKeyLimitExceeded(String apiKey) {
        if(apiKeyLastUsedTime.containsKey(apiKey)) {
            long now = new Date().getTime();
            long lastTimeUsed = apiKeyLastUsedTime.get(apiKey);
            long apiKeyLimitSec = apiKeysAndLimits.get(apiKey);

            if (now - lastTimeUsed < toMilisec(apiKeyLimitSec)) {
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }

    }

    private boolean isKeySuspended(String apiKey) {
        long now = new Date().getTime();
        if(suspendedKeyWithSuspendingStartTime.containsKey(apiKey)){
            long suspendingStartTime = suspendedKeyWithSuspendingStartTime.get(apiKey);
            if(now-suspendingStartTime>toMilisec(suspendingKeyTimeSec)){
                unSuspendKey(apiKey);
                return false;
            }else{
                LOGGER.info("Key [{}] is suspended from [{}]", apiKey, new Date(suspendingStartTime));
                return true;
            }
        }else{
            return false;
        }
    }

    private void unSuspendKey(String apiKey) {
        LOGGER.info("Unsuspend key [{}]", apiKey);
        suspendedKeyWithSuspendingStartTime.remove(apiKey);
    }

    private long toMilisec(long sec){
        return sec*1000l;
    }

    public void setSuspendingKeyTimeSec(long suspendingKeyTimeSec) {
        this.suspendingKeyTimeSec = suspendingKeyTimeSec;
    }
}
