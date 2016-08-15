package net.androtweet.buddy.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import net.androtweet.buddy.BuddyApp;
import net.androtweet.buddy.R;
import net.androtweet.buddy.adapters.TwitterAccountAdapter;
import net.androtweet.buddy.base.BaseFragment;
import net.androtweet.buddy.models.firebase.TwitterAccount;

import java.util.ArrayList;

/**
 * Created by kaloglu on 11/08/16.
 */
public class TwitterAccountsFragment2 extends BaseFragment {

    private static final String TAG = "TwitterAccountsFragment";
    private GridView mGridView;
    private TwitterAccountAdapter mAdapter;
    private FirebaseUser logonUser;
    private TwitterAuthClient authClient;
    private Activity activity;
    private DatabaseReference TWITTER_ACCOUNTS;

    @Override
    protected int setResourceID() {
        return R.layout.fragment_twitter_accounts2;
    }

    @Override
    protected void fragmentCreated(Bundle savedInstanceState) {

    }

    @Override
    protected void onFragmentBeingDestroed() {

    }

    @Override
    protected void onResumeFragment() {

    }

    @Override
    protected void initializeScreen() {
        BuddyApp buddyApp = BuddyApp.getInstance();
        logonUser = buddyApp.getFirebaseService().getFirebaseUser();
//        TWITTER_ACCOUNTS = ((BaseActivity) activity).TWITTER_ACCOUNTS;
//        mGridView = (GridView) getRootView().findViewById(R.id.account_list);

//        updateUI();

    }

    private void updateUI() {
//        hideProgressDialog();
        if (logonUser != null) {
            final ArrayList<User> mItemlist = new ArrayList<>();
            TWITTER_ACCOUNTS.child(logonUser.getUid()).getRef().addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mItemlist.clear();
                    for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                        TwitterAccount twitterAccount = dataSnap.getValue(TwitterAccount.class);
                        twitterAccount.setAccountId(dataSnap.getKey());
                        TwitterAuthToken twitterAuthToken = new TwitterAuthToken(twitterAccount.getAuthToken().getToken(), twitterAccount.getAuthToken().getSecret());
                        TwitterSession session = new TwitterSession(twitterAuthToken, Long.parseLong(twitterAccount.getAccountId()), twitterAccount.getScreenName());
                        new TwitterApiClient(session).getAccountService().verifyCredentials(false, false, new Callback<User>() {
                            @Override
                            public void success(Result<User> result) {
                                mItemlist.add(result.data);
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void failure(TwitterException exception) {
                                Snackbar snackBar = Snackbar.make(mGridView, "Error: " + exception.getMessage(), Snackbar.LENGTH_LONG);
                                snackBar.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                            }
                        });
//                        mItemlist.add(twitterAccount);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: " + databaseError.getMessage(), databaseError.toException());
                }

            });
            mAdapter = new TwitterAccountAdapter(activity, mItemlist);
            mGridView.setAdapter(mAdapter);

        } else {
            Log.d(TAG, "updateUI: UnAuthorized");
        }
    }


}
