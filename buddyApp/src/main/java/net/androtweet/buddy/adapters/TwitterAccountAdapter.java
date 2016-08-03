package net.androtweet.buddy.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import net.androtweet.buddy.BuddyApp;
import net.androtweet.buddy.R;
import net.androtweet.buddy.models.firebase.TwitterAccount;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitterAccountAdapter extends RecyclerView.Adapter<TwitterAccountAdapter.ViewHolder> {
    private final Context context;
    List<TwitterAccount> mItems;

    public TwitterAccountAdapter(Context context) {
        super();
        this.context = context;
        mItems = new ArrayList<>();

        FirebaseUser loginUser = BuddyApp.getFirebaseAuth().getCurrentUser();

        BuddyApp.getDB().child("twitterAccounts").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                    TwitterAccount twitterAccount=dataSnapshot.getValue(TwitterAccount.class);
//                    twitterAccount.getScreenName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.twitter_accounts_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        TwitterAccount userAccount = mItems.get(i);
//        ImageDownloadMessageHandler imageDownloadMessageHandler1 = new ImageDownloadMessageHandler(null, viewHolder.profileImg);
//        ImageDownlaodThread imageDownlaodThread = new ImageDownlaodThread(imageDownloadMessageHandler1, userAccount.getProfileImageURL());
//        imageDownlaodThread.start();
//        ImageDownloadMessageHandler imageDownloadMessageHandler2 = new ImageDownloadMessageHandler(null, viewHolder.profileBanner);
//        ImageDownlaodThread imageDownlaodThread2 = new ImageDownlaodThread(imageDownloadMessageHandler2, userAccount.getProfileBannerURL());
//        imageDownlaodThread2.start();
//        viewHolder.usernameTxt.setText(userAccount.getScreenName());
//        viewHolder.biographyTxt.setText(userAccount.getDescription());
//        viewHolder.tweetCount.setText(userAccount.getStatusesCount() + "");
//        viewHolder.likeCount.setText(userAccount.getFavouritesCount() + "");
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profileImg;
        public TextView usernameTxt, biographyTxt, tweetCount, likeCount;
        public ImageView profileBanner;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImg = (ImageView) itemView.findViewById(R.id.profileImg);
            profileBanner = (ImageView) itemView.findViewById(R.id.profileBanner);
//            usernameTxt = (TextView) itemView.findViewById(R.id.usernameTxt);
//            biographyTxt = (TextView) itemView.findViewById(R.id.biograhpyTxt);
//            tweetCount = (TextView) itemView.findViewById(R.id.tweetCount);
//            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
        }
    }

    private class UserSample {
        private String screenName;
        private String description;
        private int statusesCount;
        private int favouritesCount;
        private String profileImageURL;
        private String profileBannerURL;

        public void setScreenName(String screenName) {
            this.screenName = screenName;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setStatusesCount(int statusesCount) {
            this.statusesCount = statusesCount;
        }

        public void setFavouritesCount(int favouritesCount) {
            this.favouritesCount = favouritesCount;
        }

        public void setProfileImageURL(String profileImageURL) {
            this.profileImageURL = profileImageURL;
        }

        public String getProfileImageURL() {
            return profileImageURL;
        }

        public String getScreenName() {
            return screenName;
        }

        public String getDescription() {
            return description;
        }

        public int getStatusesCount() {
            return statusesCount;
        }

        public int getFavouritesCount() {
            return favouritesCount;
        }

        public void setProfileBannerURL(String profileBannerURL) {
            this.profileBannerURL = profileBannerURL;
        }

        public String getProfileBannerURL() {
            return profileBannerURL;
        }
    }
    public class ImageDownlaodThread extends Thread {
        ImageDownloadMessageHandler imageDownloadMessageHandler;
        String imageUrl;

        public ImageDownlaodThread(ImageDownloadMessageHandler imageDownloadMessageHandler, String imageUrl) {
            this.imageDownloadMessageHandler = imageDownloadMessageHandler;
            this.imageUrl = imageUrl;
        }

        @Override
        public void run() {
            Drawable drawable = LoadImageFromWebOperations(imageUrl);
            Message message = imageDownloadMessageHandler.obtainMessage(1, drawable);
            imageDownloadMessageHandler.sendMessage(message);
            System.out.println("Message sent");
        }

    }

    private class ImageDownloadMessageHandler extends Handler {
        ProgressBar progressBar;
        ImageView imageTextView;

        public ImageDownloadMessageHandler(ProgressBar progressBar, ImageView imageTextView) {
//            this.progressBar = progressBar;
            this.imageTextView = imageTextView;
        }

        @Override
        public void handleMessage(Message message) {
//            progressBar.setVisibility(View.GONE);
            imageTextView.setImageDrawable((Drawable) message.obj);
            imageTextView.setVisibility(View.VISIBLE);
        }

    }

    Drawable LoadImageFromWebOperations(String url) {
        Drawable d = null;
        InputStream is = null;
        try {
            is = (InputStream) new URL(url).getContent();
            d = Drawable.createFromStream(is, "src name");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return d;
    }
}