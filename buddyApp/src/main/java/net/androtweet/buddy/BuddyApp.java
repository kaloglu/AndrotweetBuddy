package net.androtweet.buddy;

import android.app.Application;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import net.androtweet.buddy.base.BaseActivity;
import net.androtweet.buddy.listeners.ServiceListener;
import net.androtweet.buddy.models.firebase.TwitterAccount;
import net.androtweet.buddy.services.FirebaseService;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by kaloglu on 24/07/16.
 */
public class BuddyApp extends Application {

    private static final String TAG = "Buddy Application";
    private static BuddyApp instance;
    private DeepLink deepLink;
    private TwitterAccount ownerAccount;
    private Session twitterSession;
    private List<TwitterAccount> twitterAccountList;
    private String twitterAccountIdList = "";

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

    public void defineOwner(final ServiceListener listener) {
        listener.onStarted();
        getFirebaseService().getRef_OWNER().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ownerAccount = dataSnapshot.getValue(TwitterAccount.class);
                listener.onFinished();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(((BaseActivity) instance.getBaseContext()).findViewById(R.id.toolbar), "Error: " + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                listener.onFinished();
            }
        });
    }

    public void fillAccountList(final ServiceListener listener) {
        listener.onStarted();
        FirebaseService.getRef_TWITTER_ACCOUNTS().child(getFirebaseService().getFirebaseUser().getUid()).getRef()
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        twitterAccountList = new ArrayList<>();
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            TwitterAccount twitterAccount = dataSnap.getValue(TwitterAccount.class);
                            twitterAccount.setAccountId(dataSnap.getKey());

                            addTwitterAccountList(twitterAccount);
                        }
                        listener.onFinished();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailed();
                        Log.e(TAG, "fillAccountlist() onCancelled: " + databaseError.getMessage(), databaseError.toException());
                    }

                });
    }

    public void addTwitterAccountList(TwitterAccount twitterAccount) {
        twitterAccountList.add(twitterAccount);
        twitterAccountIdList += ((getTwitterAccountIdList().length() > 0) ? "," : "") + twitterAccount.getAccountId();
    }

    public List<TwitterAccount> getTwitterAccountList() {
        return twitterAccountList;
    }

    public TwitterAccount getOwnerAccount() {

        return ownerAccount;
    }

    public String getTwitterAccountIdList() {
        if (twitterAccountIdList == null)
            twitterAccountIdList = "";
        return twitterAccountIdList;
    }
}
