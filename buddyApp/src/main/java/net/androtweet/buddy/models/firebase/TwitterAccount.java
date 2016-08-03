package net.androtweet.buddy.models.firebase;

import java.io.Serializable;

/**
 * Created by kaloglu on 01/08/16.
 */
public class TwitterAccount implements Serializable {
    private String twştterUserId;
    private String screenName;
    private String secret;
    private String token;

    public TwitterAccount() {
    }

    public String getTwştterUserId() {
        return twştterUserId;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getSecret() {
        return secret;
    }

    public String getToken() {
        return token;
    }

}
