package net.androtweet.buddy.interfaces;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

public interface UsersService {
    /**
     * Customized via: @kaloglu
     * Returns a variety of information about the user specified by the required user_id or screen_name parameter.
     * The author’s most recent Tweet will be returned inline when possible.
     * <p/>
     * GET users / lookup is used to retrieve a bulk collection of user objects.
     * You must be following a protected user to be able to see their most recent Tweet.
     * <p/>
     * If you don’t follow a protected user, the users Tweet will be removed.
     * A Tweet will not always be returned in the current_status field.
     *
     * @param userId          (optional) The ID of the user for whom to return results for.
     * @param screenName      (optional) The screen name of the user for whom to return results for.
     * @param includeEntities (optional) The entities node will be omitted when set to false.
     * @param cb              The callback to invoke when the request completes.
     */
    @GET("/1.1/users/show.json?" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    void show(@Query("user_id") Long userId,
              @Query("screen_name") String screenName,
              @Query("include_entities") Boolean includeEntities,
              Callback<User> cb);

    @GET("/1.1/users/lookup.json?" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    void lookup(@Query("user_id") String userId,
                @Query("include_entities") Boolean includeEntities,
                Callback<List<User>> cb);

}
