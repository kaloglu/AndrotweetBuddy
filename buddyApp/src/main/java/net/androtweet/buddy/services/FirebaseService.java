package net.androtweet.buddy.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.androtweet.buddy.Constants;
import net.androtweet.buddy.models.firebase.TwitterAccount;

/**
 * Created by kaloglu on 14/08/16.
 */
public class FirebaseService {
    private static DatabaseReference ref_USERS;
    private static DatabaseReference ref_TWITTER_ACCOUNTS;
    private static DatabaseReference ref_STATUSES;
    private static DatabaseReference ref_LIKES;
    private static DatabaseReference ref_RETWEETS;
    private static DatabaseReference ref_TWEET_DETAILS;
    private static FirebaseService instance;
    private static DatabaseReference ref_PREFERENCES;
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
        ref_USERS = getDatabaseReference(Constants.USERS);
        ref_TWITTER_ACCOUNTS = getDatabaseReference(Constants.TWITTER_ACCOUNTS);
        ref_PREFERENCES = getDatabaseReference(Constants.PREFERENCES);
        ref_STATUSES = getDatabaseReference(Constants.STATUSES);
        ref_LIKES = getDatabaseReference(Constants.LIKES);
        ref_RETWEETS = getDatabaseReference(Constants.RETWEETS);
        ref_TWEET_DETAILS = getDatabaseReference(Constants.TWEET_DETAILS);
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

    public DatabaseReference getRef_LIKES() {
        return ref_LIKES;
    }

    public DatabaseReference getRef_OWNER() {
        return ref_PREFERENCES.child(Constants.OWNER);
    }

    public DatabaseReference getRef_RETWEETS() {
        return ref_RETWEETS;
    }

    public DatabaseReference getRef_STATUSES() {
        return ref_STATUSES;
    }

    public DatabaseReference getRef_TWEET_DETAILS() {
        return ref_TWEET_DETAILS;
    }

    public static DatabaseReference getRef_TWITTER_ACCOUNTS() {
        return ref_TWITTER_ACCOUNTS;
    }

    public DatabaseReference getRef_USERS() {
        return ref_USERS;
    }

    // [START basic_write]
    public static void updateAccountInfo(FirebaseUser logonUser, TwitterAccount userAccount) {
        getRef_TWITTER_ACCOUNTS().child(logonUser.getUid()).child(userAccount.getAccountId()).setValue(userAccount);
    }
}
