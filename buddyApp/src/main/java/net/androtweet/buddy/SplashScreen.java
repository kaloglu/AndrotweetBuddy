package net.androtweet.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import net.androtweet.buddy.activities.LoginActivity;
import net.androtweet.buddy.activities.MainActivity;
import net.androtweet.buddy.services.FirebaseService;

public class SplashScreen extends Activity {
    private long duration_mSec = 0;

    // Splash screen timer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        duration_mSec = getResources().getInteger(R.integer.loading_time_sec) * 1000;
        setContentView(R.layout.activity_splash);
        simulateLoading(findViewById(R.id.loading), duration_mSec);
        new Handler().postDelayed(new Runnable() {

         /*
          * Showing splash screen with a timer. This will be useful when you
          * want to show case your app logo / company
          */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Class activityClass = MainActivity.class;
                if (FirebaseService.getInstance().getFirebaseUser() == null)
                    activityClass = LoginActivity.class;

                Intent i = new Intent(SplashScreen.this, activityClass);

                startActivity(i);

                // close this activity
                finish();
            }
        }, duration_mSec);
    }

    private void simulateLoading(View viewById, long mSec) {
        Animation anim = new ScaleAnimation(
                0f, 1f, // Start and end values for the X axis scaling
                1f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(mSec);
        anim.setInterpolator(new DecelerateInterpolator());
        viewById.startAnimation(anim);
    }
}

