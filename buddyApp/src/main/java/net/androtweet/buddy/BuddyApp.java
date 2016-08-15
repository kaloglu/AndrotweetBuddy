package net.androtweet.buddy;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import net.androtweet.buddy.services.FirebaseService;

import io.fabric.sdk.android.Fabric;

/**
 * Created by kaloglu on 24/07/16.
 */
public class BuddyApp extends Application {

    private static BuddyApp instance;
    private DeepLink deepLink;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Configure Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig("hHFUMp2BBfBZ38ck6K6Grrv7C", "U1RqnR3Zng4tdmvdFBwYqnEzvrNYfYbwKwmOeen7Xu90uWacne");
        Fabric.with(instance, new Twitter(authConfig));

    }

    public static BuddyApp getInstance() {
        return instance;
    }

    public DeepLink getDeepLink() {
        return deepLink;
    }

    public FirebaseService getFirebaseService() {
        return FirebaseService.getInstance();
    }
}
