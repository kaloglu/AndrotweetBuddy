package net.androtweet.buddy.services;

import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

import net.androtweet.buddy.customized.CustomTwitterApiClient;
import net.androtweet.buddy.models.firebase.TwitterAccount;

/**
 * Created by kaloglu on 17/08/16.
 */
public class TwitterApiService {
    private CustomTwitterApiClient apiClient;
    private static TwitterAccount account;

    public TwitterApiService(TwitterAccount account) {
        TwitterApiService.account = account;
    }

    public CustomTwitterApiClient getApiClient() {
        return new CustomTwitterApiClient(getTwitterSession(account));
    }

    private TwitterSession getTwitterSession(TwitterAccount account) {
        TwitterAuthToken twitterAuthToken = new TwitterAuthToken(account.getAuthToken().getToken(), account.getAuthToken().getSecret());
        return new TwitterSession(twitterAuthToken, Long.parseLong(account.getAccountId()), account.getScreenName());
    }
}
