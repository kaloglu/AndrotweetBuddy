package net.androtweet.buddy.listeners;

/**
 * Created by kaloglu on 18/08/16.
 */
public interface ServiceListener {
    void onStarted();

    void onFinished();

    void onFailed();
}
