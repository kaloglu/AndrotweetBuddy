package net.androtweet.buddy.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.androtweet.buddy.Constants;

/**
 * Created by kaloglu on 14/08/16.
 */
public class FirebaseService {
    private static DatabaseReference USERS;
    private static DatabaseReference TWITTER_ACCOUNTS;
    private static DatabaseReference STATUSES;
    private static DatabaseReference LIKES;
    private static DatabaseReference RETWEETS;
    private static DatabaseReference TWEET_DETAILS;
    private static FirebaseService instance;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    private static class FirebaseServiceHelper {

        private static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        private static final FirebaseService instance = new FirebaseService();
    }

    public static FirebaseService getInstance() {
        if (instance == null) {
            instance = FirebaseServiceHelper.instance;
        }
        return instance;
    }

    protected FirebaseService() {
        firebaseDatabase = FirebaseServiceHelper.firebaseDatabase;
        USERS = getDatabaseReference(Constants.USERS);
        TWITTER_ACCOUNTS = getDatabaseReference(Constants.TWITTER_ACCOUNTS);
        STATUSES = getDatabaseReference(Constants.STATUSES);
        LIKES = getDatabaseReference(Constants.LIKES);
        RETWEETS = getDatabaseReference(Constants.RETWEETS);
        TWEET_DETAILS = getDatabaseReference(Constants.TWEET_DETAILS);
    }

    public DatabaseReference getDatabaseReference() {
        return getDatabaseReference("");
    }

    public DatabaseReference getDatabaseReference(String s) {
        return firebaseDatabase.getReference(s);
    }

    public FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null)
            firebaseAuth = FirebaseAuth.getInstance();

        return firebaseAuth;
    }

    public FirebaseUser getFirebaseUser() {
        return getFirebaseAuth().getCurrentUser();
    }

    public DatabaseReference getTWITTER_ACCOUNTS() {
        return TWITTER_ACCOUNTS;
    }

    public DatabaseReference getLIKES() {
        return LIKES;
    }

    public DatabaseReference getRETWEETS() {
        return RETWEETS;
    }

    public DatabaseReference getSTATUSES() {
        return STATUSES;
    }

    public DatabaseReference getTWEET_DETAILS() {
        return TWEET_DETAILS;
    }

    public DatabaseReference getTwitterAccounts() {
        return TWITTER_ACCOUNTS;
    }

    public DatabaseReference getUSERS() {
        return USERS;
    }
}
