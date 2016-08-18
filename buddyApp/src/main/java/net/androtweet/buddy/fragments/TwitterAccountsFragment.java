package net.androtweet.buddy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import net.androtweet.buddy.BuddyApp;
import net.androtweet.buddy.R;
import net.androtweet.buddy.adapters.TwitterAccountAdapter;
import net.androtweet.buddy.base.BaseFragment;
import net.androtweet.buddy.customized.CustomTwitterApiClient;
import net.androtweet.buddy.services.FirebaseService;
import net.androtweet.buddy.services.TwitterApiService;

import java.util.List;

/**
 * Created by kaloglu on 11/08/16.
 */
public class TwitterAccountsFragment extends BaseFragment {

    private static final String TAG = "TwitterAccountsFragment";
    private GridView mGridView;
    private TwitterAccountAdapter mAdapter;
    private FirebaseUser logonUser;
    private TwitterAuthClient authClient;
    private Context context;
    private DatabaseReference ref_TwitterAccounts;

    @Override
    protected int setResourceID() {
        return R.layout.fragment_twitter_accounts;
    }

    @Override
    protected void fragmentCreated(Bundle savedInstanceState) {
        BuddyApp buddyApp = BuddyApp.getInstance();
        logonUser = buddyApp.getFirebaseService().getFirebaseUser();
        ref_TwitterAccounts = FirebaseService.getRef_TWITTER_ACCOUNTS();
    }

    @Override
    protected void onFragmentBeingDestroed() {

    }

    @Override
    protected void onResumeFragment() {

    }

    @Override
    protected void initializeScreen() {
        assert getView() != null;
        mGridView = (GridView) getView().findViewById(R.id.account_list);
        updateUI();

    }

    private void updateUI() {
//        hideProgressDialog();
        if (logonUser != null) {
            CustomTwitterApiClient twitterApiClient = new TwitterApiService(BuddyApp.getInstance().getOwnerAccount()).getApiClient();
            twitterApiClient.getUserService().lookup(BuddyApp.getInstance().getTwitterAccountIdList(), false, new Callback<List<User>>() {

                @Override
                public void success(Result<List<User>> result) {
                    mAdapter = new TwitterAccountAdapter(getActivity(), result.data);
                    mGridView.setAdapter(mAdapter);
                }

                @Override
                public void failure(TwitterException exception) {
                    Snackbar.make(mGridView, "Error: " + exception.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });

        } else {
            Log.d(TAG, "updateUI: UnAuthorized");
        }
    }


}
