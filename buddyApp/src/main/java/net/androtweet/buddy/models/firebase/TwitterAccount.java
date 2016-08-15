package net.androtweet.buddy.models.firebase;

import com.google.firebase.database.Exclude;

import net.androtweet.buddy.models.AuthTokenModel;

import java.io.Serializable;

/**
 * Created by kaloglu on 01/08/16.
 */
public class TwitterAccount implements Serializable {
    @Exclude
    private String accountId;
    private String screenName;
    private AuthTokenModel authToken;

    public AuthTokenModel getAuthToken() {
        return authToken;
    }

    public TwitterAccount() {
    }

    public String getAccountId() {
        return accountId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setAuthToken(AuthTokenModel authToken) {
        this.authToken = authToken;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

}
