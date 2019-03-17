package matwes.zpi.api;

import java.util.List;

import matwes.zpi.domain.Event;
import matwes.zpi.domain.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Mateusz Wesołowski
 */
public interface ApiInterface {
    @POST("/login")
    Call<User> login(
            @Field("email") String email,
            @Field("password") String code);

    @POST("/login/facebook")
    Call<User> loginByFacebook(
            @Field("token") String token);

    @POST("/login/code")
    Call<User> loginByCode(
            @Field("email") String email,
            @Field("code") String code);

    @POST("/login/code")
    Call<User> resetPassword(
            @Field("email") String email);

    @POST("/register")
    Call<User> register(
            @Field("email") String email,
            @Field("password") String password,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName);

    @FormUrlEncoded
    @POST("/events")
    Call<Void> createEvent(
            @Field("creator_id") long creatorId,
            @Field("date") String date,
            @Field("description") String description,
            @Field("maxMembers") String maxMembers,
            @Field("name") String name,
            @Field("place_googleId") String googlePlaceId,
            @Field("sport_id") int sportId,
            @Field("time") String time);

    @GET("/events?size=99")
    Call<List<Event>> getEvents();
}
