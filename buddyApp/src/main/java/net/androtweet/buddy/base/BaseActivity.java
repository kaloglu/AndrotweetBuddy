package net.androtweet.buddy.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import net.androtweet.buddy.BuddyApp;
import net.androtweet.buddy.R;
import net.androtweet.buddy.services.FirebaseService;

import java.util.Stack;
import java.util.UUID;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int NETWORK_CONNECTION_LOST_ACTIIVITY_REQUEST_CODE = 999;
    public static Activity mActivity;
    public BroadcastReceiver networkStateReceiver;
    public boolean isActivityVisible = false;
    public BuddyApp buddyApp;
    public FirebaseUser logonUser;
    public DatabaseReference TWITTER_ACCOUNTS;
    private Fragment fragment;
    private Stack<String> fragmentStack = new Stack<>();
    private FirebaseService firebaseService;

    protected abstract void onBackStackEmpty();

    protected abstract void initializeScreen();

    public Stack<String> getStack() {
        return fragmentStack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mActivity = this;
        buddyApp = BuddyApp.getInstance();
        firebaseService = buddyApp.getFirebaseService();
        logonUser = firebaseService.getFirebaseUser();

        TWITTER_ACCOUNTS = firebaseService.getTWITTER_ACCOUNTS();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clearBackStack() {

        FragmentManager fm = this.getFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            try {
                fm.popBackStack();
            } catch (Exception ignored) {

            }

        }

    }

    public void startFragment(BaseFragment fragment) {
        this.fragment = fragment;

        if (!fragmentStack.isEmpty()) {
            String fragmentClassName = fragmentStack.peek();
            BaseFragment fragmentBase = (BaseFragment) getFragmentManager().findFragmentByTag(fragmentClassName);
            fragmentBase.setDeepLink(buddyApp.getDeepLink());
        }
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        UUID uniqueKey = UUID.randomUUID();
        fragmentStack.push(uniqueKey.toString());

        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_right);
        transaction.replace(R.id.flContent, fragment, uniqueKey.toString());

        transaction.addToBackStack(uniqueKey.toString());
        try {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);

        } catch (Exception ignored) {
        }

        try {
            transaction.commit();
        } catch (IllegalStateException ignored) {
        }

    }


    @Override
    public void onBackPressed() {

        if (fragmentStack != null && !fragmentStack.isEmpty() && fragmentStack.size() > 1) {

            BaseFragment currentFragment = (BaseFragment) getFragmentManager().findFragmentByTag(fragmentStack.peek());
            if (fragmentStack.size() == 1) {

                onBackStackEmpty();

            } else {

                if (fragmentStack != null && !fragmentStack.isEmpty()) {
                    if (currentFragment != null)
                        currentFragment.onFragmentBeingClose();

                    if (fragmentStack.size() > 0) {

                        String fragmentClassName = fragmentStack.peek();
                        BaseFragment fragmentBase = (BaseFragment) getFragmentManager().findFragmentByTag(fragmentClassName);
                        fragment = fragmentBase;
                        if (fragmentBase != null)
                            fragmentBase.onResume();

                    }

                } else {

                    super.onBackPressed();

                }

            }

        } else {

            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:

                return true;

            default:
                return super.onKeyDown(keyCode, event);

        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    public void startNetworkConnectionListener() {

        networkStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getExtras() != null) {

                    final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
                    if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {

                        finishActivity(NETWORK_CONNECTION_LOST_ACTIIVITY_REQUEST_CODE);

                    } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {

//                        Intent i = new Intent(BaseFragmentActivity.this, NetworkConnectionLostActivity.class);
//                        startActivityForResult(i, NETWORK_CONNECTION_LOST_ACTIIVITY_REQUEST_CODE);

                    }

                }

            }

        };

        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkStateReceiver, filters);

    }

    public void unRegisterNetworkReceivers() {

        try {

            unregisterReceiver(networkStateReceiver);
            networkStateReceiver = null;

        } catch (Exception e) {
        }

    }

    public void callResumeFragment() {

        if (fragmentStack.size() > 0) {

            String fragmentClassName = fragmentStack.peek();
            BaseFragment fragmentBase = (BaseFragment) getFragmentManager().findFragmentByTag(fragmentClassName);
            fragment = fragmentBase;
            fragmentBase.onResume();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityVisible = false;
    }

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
