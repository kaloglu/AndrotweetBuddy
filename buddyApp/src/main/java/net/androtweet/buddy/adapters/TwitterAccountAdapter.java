package net.androtweet.buddy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twitter.sdk.android.core.models.User;

import net.androtweet.buddy.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

public class TwitterAccountAdapter extends BaseAdapter {
    private final Context context;
    List<User> mItems;

    public TwitterAccountAdapter(Context context, List<User> mItemlist) {
        super();
        this.context = context;
        mItems = mItemlist;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        System.out.println("getView " + pos + " " + convertView);
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(context);
            convertView = vi.inflate(R.layout.account_item, null);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        User twitterUser = getItem(pos);

        setRemoteImage(viewHolder.profileImg, twitterUser.profileImageUrl, "_bigger");
        if (twitterUser.profileLinkColor != null && !"".equals(twitterUser.profileLinkColor))
            viewHolder.profileBanner.setBackgroundColor(Color.parseColor("#" + twitterUser.profileLinkColor));
        viewHolder.usernameTxt.setText(twitterUser.screenName);
        viewHolder.tweetCount.setText(MessageFormat.format("{0}", twitterUser.statusesCount));
        viewHolder.likeCount.setText(MessageFormat.format("{0}", twitterUser.favouritesCount));
        viewHolder.friendsCount.setText(MessageFormat.format("{0}", twitterUser.friendsCount));
        viewHolder.followersCount.setText(MessageFormat.format("{0}", twitterUser.followersCount));

        return convertView;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profileImg, profileBanner;
        public TextView usernameTxt, tweetCount, likeCount, friendsCount, followersCount;

        public ViewHolder(View itemView) {
            super(itemView);

            profileImg = (ImageView) itemView.findViewById(R.id.profileImg);
            profileBanner = (ImageView) itemView.findViewById(R.id.profileBanner);
            usernameTxt = (TextView) itemView.findViewById(R.id.userNameTxt);
            tweetCount = (TextView) itemView.findViewById(R.id.statusesCount);
            likeCount = (TextView) itemView.findViewById(R.id.likesCount);
            friendsCount = (TextView) itemView.findViewById(R.id.followingCount);
            followersCount = (TextView) itemView.findViewById(R.id.followersCount);

        }

    }

    public void setRemoteImage(ImageView profileImg, String profileImageUrl, String size) {
        if (profileImageUrl != null && !"".equals(profileImageUrl)) {
            ImageDownloadMessageHandler imageDownloadMessageHandler1 = new ImageDownloadMessageHandler(null, profileImg);
            ImageDownlaodThread imageDownlaodThread = new ImageDownlaodThread(imageDownloadMessageHandler1, profileImageUrl.replace("_normal", size));
            imageDownlaodThread.start();
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

    private static class ImageDownloadMessageHandler extends Handler {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return d;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public User getItem(int pos) {
        return mItems.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

}