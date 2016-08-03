package net.androtweet.buddy.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by kaloglu on 24/07/16.
 */
@IgnoreExtraProperties
public class User {
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.email = email;
    }


}
