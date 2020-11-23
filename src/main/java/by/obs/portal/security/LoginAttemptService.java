package by.obs.portal.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private static final int MAX_NUMBER_OF_ATTEMPT = 3;
    private static final int BLOCK_TIME_MINUTES = 15;
    private LoadingCache<String, Integer> blockList;

    public LoginAttemptService() {
        super();
        blockList = CacheBuilder.newBuilder().expireAfterWrite(BLOCK_TIME_MINUTES, TimeUnit.MINUTES).build(new CacheLoader<>() {
            @Override
            public Integer load(final @Nullable String key) {
                return 0;
            }
        });
    }

    void loginSucceeded(final String key) {
        blockList.invalidate(key);
    }

    void loginFailed(final String key) {
        int attempts;
        try {
            attempts = blockList.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        blockList.put(key, attempts);
    }

    boolean isBlocked(final String key) {
        try {
            return blockList.get(key) >= MAX_NUMBER_OF_ATTEMPT;
        } catch (final ExecutionException e) {
            return false;
        }
    }
}