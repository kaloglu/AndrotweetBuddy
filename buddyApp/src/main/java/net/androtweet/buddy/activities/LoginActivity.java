package net.androtweet.buddy.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.androtweet.buddy.BuddyApp;
import net.androtweet.buddy.R;
import net.androtweet.buddy.models.User;
import net.androtweet.buddy.base.BaseActivity;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor>, View.OnClickListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private AutoCompleteTextView inputEmail;
    private EditText inputPassword;
    private View progressBar;
    private View mLoginFormView;
    private Button signActionButton;
    private Button switchFormActionButton;
    private Button setForgotPassButton;
    private Resources mResources;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        mResources = getResources();
        setupActionBar();
        // Set up the login form.
        inputEmail = (AutoCompleteTextView) findViewById(R.id.email);
        if (auth.getCurrentUser()!=null)
            startActivity(new Intent(LoginActivity.this,MainActivity.class));

        inputEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() { // Click <DONE> on keyboard.
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    inputPassword.requestFocus();
                    return true;
                }
                return false;
            }
        });
        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !isEmailValid(inputEmail.getText().toString().trim())) {
                    inputEmail.setError(getString(R.string.err_invalid_email));

                    Snackbar.make(inputEmail,R.string.err_invalid_email,Snackbar.LENGTH_LONG).show();
                }
            }
        });
        populateAutoComplete();

        signActionButton = (Button) findViewById(R.id.signActionButton);
        setForgotPassButton = (Button) findViewById(R.id.setForgotPassButton);
        switchFormActionButton = (Button) findViewById(R.id.switchFormActionButton);

        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() { // Click <DONE> on keyboard.
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    signActionButtonClick();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        progressBar = findViewById(R.id.progressBar);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signActionButton: // Sign Action Button
                signActionButtonClick();
                break;
            case R.id.switchFormActionButton: // Switch Sign IN or UP button.s
            case R.id.setForgotPassButton: // Reset Password
                switchINorUPScreen(v.getId());
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signActionButtonClick() {
        if (signActionButton.getText().equals(mResources.getString(R.string.lbl_registerButton)))
            signUpClickAction();

        if (signActionButton.getText().equals(mResources.getString(R.string.lbl_loginButton)))
            signInClickAction();

        if (signActionButton.getText().equals(mResources.getString(R.string.lbl_resetPasswordButton)))
            resetPasswordClickAction();
    }

    private void switchINorUPScreen(int id) {
        inputEmail.requestFocus();
        inputEmail.setError(null);
        inputPassword.setError(null);
        setForgotPassButton.setVisibility(View.VISIBLE);
        inputPassword.setVisibility(View.VISIBLE);
        switch (id) {
            case R.id.switchFormActionButton:
                if (signActionButton.getText().equals(mResources.getString(R.string.lbl_registerButton))) {
                    signActionButton.setText(R.string.lbl_loginButton);
                    signActionButton.setBackgroundColor(getResources().getColor(R.color.bg_login));
                    inputPassword.setImeActionLabel(mResources.getString(R.string.lbl_loginButton), 0);
                    switchFormActionButton.setText(R.string.lbl_link2RegisterButton);
                } else {
                    signActionButton.setText(R.string.lbl_registerButton);
                    signActionButton.setBackgroundColor(mResources.getColor(R.color.bg_register));
                    inputPassword.setImeActionLabel(mResources.getString(R.string.lbl_registerButton), 0);
                    switchFormActionButton.setText(R.string.lbl_link2LoginButton);
                }
                break;
            case R.id.setForgotPassButton:
                signActionButton.setText(R.string.lbl_resetPasswordButton);
                signActionButton.setBackgroundColor(mResources.getColor(R.color.bg_forgotPassword));
                switchFormActionButton.setText(R.string.lbl_link2LoginButton);
                setForgotPassButton.setVisibility(View.GONE);
                inputPassword.setVisibility(View.GONE);
                break;
        }
    }

    private void signInClickAction() {
        final String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Reset errors.
        inputEmail.setError(null);
        inputPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.err_field_required));
            focusView = inputEmail;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            inputPassword.setError(getString(R.string.err_field_required));
            focusView = inputPassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            //create user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            showProgress(false);
                            if (!task.isSuccessful()) {

                                Snackbar.make(inputPassword, mResources.getString(R.string.err_auth_failed), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            } else {
                                Snackbar.make(inputPassword, mResources.getString(R.string.user_logon), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        }
                    });
        }
    }
    private void writeNewUser(String userId, String email) {
        User user = new User(email);

        BuddyApp.getDB().child("users").child(userId).setValue(user);
    }

    private void signUpClickAction() {
        final String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Reset errors.
        inputEmail.setError(null);
        inputPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            inputPassword.setError(getString(R.string.err_minimum_password));
            focusView = inputPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.err_field_required));
            focusView = inputEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            inputEmail.setError(getString(R.string.err_invalid_email));
            focusView = inputEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            //create user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            showProgress(false);
                            if (!task.isSuccessful()) {
                                
                                Snackbar.make(inputPassword, mResources.getString(R.string.err_user_registeration_failed) + task.getException().getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            } else {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                if (firebaseUser != null) {
                                    writeNewUser(firebaseUser.getUid(),firebaseUser.getEmail());
                                }
                                Snackbar.make(inputPassword, MessageFormat.format(mResources.getString(R.string.user_registered), email), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        }
                    });
        }
    }

    private void resetPasswordClickAction() {
        final String email = inputEmail.getText().toString().trim();

        // Reset errors.
        inputEmail.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.err_field_required));
            focusView = inputEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            inputEmail.setError(getString(R.string.err_invalid_email));
            focusView = inputEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            //create user
            BuddyApp.getFirebaseAuth().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showProgress(false);
                            if (!task.isSuccessful()) {
                                Snackbar.make(inputPassword, mResources.getString(R.string.err_send_password), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            } else {

                                Snackbar.make(inputPassword, MessageFormat.format(mResources.getString(R.string.password_reset_email_sent), email), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        }
                    });
        }
    }

    protected void onResume() {
        super.onResume();
        showProgress(false);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(inputEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        inputEmail.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}

