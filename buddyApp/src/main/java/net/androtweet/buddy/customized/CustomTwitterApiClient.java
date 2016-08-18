package net.androtweet.buddy.customized;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

import net.androtweet.buddy.interfaces.UsersService;

/**
 * Created by kaloglu on 17/08/16.
 */
public class CustomTwitterApiClient extends TwitterApiClient {
    public CustomTwitterApiClient(TwitterSession session) {
        super(session);
    }

    /**
     * Provide CustomService with defined endpoints
     */
    public UsersService getUserService() {
        return getService(UsersService.class);
    }
}

