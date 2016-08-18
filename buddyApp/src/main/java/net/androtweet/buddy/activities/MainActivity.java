package net.androtweet.buddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import net.androtweet.buddy.R;
import net.androtweet.buddy.SplashScreen;
import net.androtweet.buddy.base.BaseActivity;
import net.androtweet.buddy.base.BaseFragment;
import net.androtweet.buddy.fragments.TwitterAccountsFragment;
import net.androtweet.buddy.fragments.TwitterAccountsFragment2;
import net.androtweet.buddy.listeners.ServiceListener;
import net.androtweet.buddy.models.AuthTokenModel;
import net.androtweet.buddy.models.firebase.TwitterAccount;
import net.androtweet.buddy.services.FirebaseService;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Buddy MainActivity";
    private MainActivity activity;
    private FloatingActionButton addAccount, fab1, fab2, fab3, fab4;
    private Animation fabOpen, fabClose, fabClockWise, fabAntiClockWise;
    private boolean isOpen;
    private TwitterAuthClient authClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_main);
        initNavigationDrawer();
        buddyApp.fillAccountList(new ServiceListener() {
            @Override
            public void onStarted() {
                showProgressDialog();
            }

            @Override
            public void onFinished() {
                buddyApp.defineOwner(new ServiceListener() {
                    @Override
                    public void onStarted() {
                    }

                    @Override
                    public void onFinished() {
                        initializeScreen();
                        hideProgressDialog();
                    }

                    @Override
                    public void onFailed() {
                        hideProgressDialog();
                    }
                });
            }

            @Override
            public void onFailed() {
                hideProgressDialog();
            }
        });


    }

    @Override
    protected void onBackStackEmpty() {

    }

    @Override
    protected void initializeScreen() {
        addAccount = (FloatingActionButton) findViewById(R.id.addAccount);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabClockWise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabAntiClockWise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        fab1.setOnClickListener(new fabOnClickListener());

        addAccount.setOnClickListener(new LoginClickListener());

        startFragment(new TwitterAccountsFragment());
    }

    private void initNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        BaseFragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = TwitterAccountsFragment.class;

        if (id == R.id.nav_camera) {
            fragmentClass = TwitterAccountsFragment2.class;
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            FirebaseService.getInstance().getFirebaseAuth().signOut();
            startActivity(new Intent(activity, SplashScreen.class));
            finish();
        }
        try {
            fragment = (BaseFragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startFragment(fragment);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class fabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Animation animOpenOrClose = isOpen ? fabClose : fabOpen;
            Animation animClockOrAntiwise = isOpen ? fabAntiClockWise : fabClockWise;
            isOpen = !isOpen;

            addAccount.startAnimation(animOpenOrClose);
            fab2.startAnimation(animOpenOrClose);
            fab3.startAnimation(animOpenOrClose);
            fab4.startAnimation(animOpenOrClose);

            fab1.startAnimation(animClockOrAntiwise);

            fab2.setClickable(isOpen);
            fab3.setClickable(isOpen);
            fab4.setClickable(isOpen);
        }
    }

    private class LoginClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Twitter.logIn(activity, new TwitterSessionCallback());
        }

    }

    private class TwitterSessionCallback extends Callback<TwitterSession> {
        @Override
        public void success(Result<TwitterSession> result) {
            Log.d(TAG, "success: " + result.data);
            TwitterSession session = result.data;
            TwitterAccount userAccount = new TwitterAccount();
            userAccount.setAccountId(String.valueOf(session.getUserId()));
            userAccount.setScreenName(session.getUserName());
            userAccount.setAuthToken(new AuthTokenModel(session.getAuthToken()));

            FirebaseService.updateAccountInfo(logonUser, userAccount);
            buddyApp.addTwitterAccountList(userAccount);

            TwitterAccountsFragment twitterAccountFragment = new TwitterAccountsFragment();
            startFragment(twitterAccountFragment);
        }

        @Override
        public void failure(TwitterException exception) {
            Snackbar.make(fab1, "Buddy Session Callback: " + exception.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void signOut() {
        Twitter.logOut();
    }

    TwitterAuthClient getTwitterAuthClient() {
        if (authClient == null) {
            synchronized (FloatingActionButton.class) {
                if (authClient == null) {
                    authClient = new TwitterAuthClient();
                }
            }
        }
        return authClient;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the Twitter login button.
//        addAccount.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getTwitterAuthClient().getRequestCode()) {
            getTwitterAuthClient().onActivityResult(requestCode, resultCode, data);
        }
    }
}
