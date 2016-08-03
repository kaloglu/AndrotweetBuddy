package net.androtweet.buddy;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kaloglu on 24/07/16.
 */
public class BuddyApp extends Application {

    private static BuddyApp instance;
    private static FirebaseAuth auth;

    public static DatabaseReference getDB() {
        return getDB("");
    }

    public static DatabaseReference getDB(String s) {
        return FirebaseDatabase.getInstance().getReference(s);
    }

    public static BuddyApp getInstance() {
        return instance;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if (auth==null)
            auth = FirebaseAuth.getInstance();

        return auth;
    }

    public static FirebaseUser getFirebaseUser() {
        return getFirebaseAuth().getCurrentUser();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
