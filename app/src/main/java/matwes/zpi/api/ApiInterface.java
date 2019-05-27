package matwes.zpi.api;

import java.util.List;

import matwes.zpi.domain.AuthToken;
import matwes.zpi.domain.Event;
import matwes.zpi.domain.NewEvent;
import matwes.zpi.domain.RegisterData;
import matwes.zpi.domain.ReturnEvent;
import matwes.zpi.domain.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Mateusz Wesołowski
 */
public interface ApiInterface {

    /*******************************
     ****** LOGIN AND REGISTER ******
     *********************************/

    @FormUrlEncoded
    @POST("/auth/authenticate")
    Call<AuthToken> login(
            @Field("email") String email,
            @Field("password") String code);

    @FormUrlEncoded
    @POST("/auth/facebook/token")
    Call<AuthToken> loginByFacebook(
            @Field("access_token") String token);

    @POST("/login/code")
    Call<User> loginByCode(
            @Field("email") String email,
            @Field("code") String code);

    @POST("/login/code")
    Call<User> resetPassword(
            @Field("email") String email);


    @POST("/registration")
    @Headers({"Content-Type: application/json"})
    Call<ResponseBody> register(@Body RegisterData body);

    /*******************
     ****** EVENTS ******
     *********************/

    @POST("/events")
    @Headers({"Content-Type: application/json"})
    Call<ReturnEvent> createEvent(
            @Header("authorization") String token,
            @Body NewEvent newEvent);

    @GET("/events/{eventId}")
    Call<Event> getEvent(
            @Header("authorization") String token,
            @Path("eventId") String eventId
    );

    @GET("/events")
    Call<List<Event>> getEvents(
            @Header("authorization") String token
    );

    @GET("/events/type/notInteresting")
    Call<List<Event>> getBlockedEvents(
            @Header("authorization") String token
    );

    @GET("/events/type/interesting")
    Call<List<Event>> getInterestingEvents(
            @Header("authorization") String token
    );

    @GET("/events/type/created")
    Call<List<Event>> getMyEvents(
            @Header("authorization") String token
    );

    @POST("/events/event/interesting/{eventId}")
    Call<ResponseBody> interested(
            @Header("authorization") String token,
            @Path("eventId") String eventId
    );

    @POST("/events/event/resetInterest/{eventId}")
    Call<ResponseBody> cancelInterested(
            @Header("authorization") String token,
            @Path("eventId") String eventId
    );

    @POST("/events/event/notInteresting/{eventId}")
    Call<ResponseBody> notInterested(
            @Header("authorization") String token,
            @Path("eventId") String eventId
    );

    @DELETE("/events/{eventId}")
    Call<Void> deleteEvent(
            @Header("authorization") String token,
            @Path("eventId") String eventId
    );

    @POST("/registration/resend?email={email}")
    Call<ResponseBody> resendEmail(
            @Path("email") String eventId
    );
}
