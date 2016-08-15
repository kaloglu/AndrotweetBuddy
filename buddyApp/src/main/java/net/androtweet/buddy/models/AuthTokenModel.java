package net.androtweet.buddy.models;

import com.twitter.sdk.android.core.TwitterAuthToken;

/**
 * Created by kaloglu on 06/08/16.
 */
public class AuthTokenModel {
    private final String token;
    private final String secret;

    public AuthTokenModel() {
        token = "";
        secret = "";
    }

    public AuthTokenModel(TwitterAuthToken authToken) {
        this.token=authToken.token;
        this.secret=authToken.secret;
    }

    public String getSecret() {
        return secret;
    }

    public String getToken() {
        return token;
    }
}
