package matwes.zpi.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.callbackInterface;
import matwes.zpi.domain.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class EventService {

    private static final EventService ourInstance = new EventService();
    List<Event> originalListOfEvents;
    private ApiInterface api;

    private EventService() {

        this.originalListOfEvents = new ArrayList<>();

        api = RestService.getApiInstance();
    }

    static EventService getInstance() {
        return ourInstance;
    }

    void downloadEvents(Context context, EventFragmentType pageType, callbackInterface callback) {
        if (Common.isMocked()) {
            getMockedEvents(context, callback);
        } else {
            switch (pageType) {
                case blocked:
                    downloadBlockedEvents(context, callback);
                    break;
                case unblocked:
                    downloadUnblockedEvents(context, callback);
                    break;
                case interesting:
                    downloadInterestingEvents(context, callback);
                    break;
            }
        }
    }

    private void getMockedEvents(Context context, callbackInterface callback) {
        List<Event> events = Common.getMockedEvents(context);
        String json;
        try {
            json = new Gson().toJson(events);
            SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
            prefs.edit().putString("EVENTS_JSON", json).apply();
            originalListOfEvents = events;
            callback.onDownloadFinished(events, null);
        } catch (Exception ex) {
            callback.onDownloadFinished(null, context.getString(R.string.error_message) + ": " + ex);
        }
    }

    private void downloadUnblockedEvents(final Context context, final callbackInterface callback) {

        api.getEvents(Common.getToken(context)).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                List<Event> events = response.body();

                try {
                    String json = new Gson().toJson(events);
                    SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
                    prefs.edit().putString("EVENTS_JSON", json).apply();
                    originalListOfEvents = events;

                    callback.onDownloadFinished(events, null);
                } catch (Exception ex) {
                    callback.onDownloadFinished(null, context.getString(R.string.error_message) + ": " + ex);

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                //getMockedEvents(context, callback);
                callback.onDownloadFinished(null, context.getString(R.string.error_message) + ": " + t);
            }
        });
    }

    private void downloadBlockedEvents(final Context context, final callbackInterface callback) {
        api.getBlockedEvents(Common.getToken(context)).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                List<Event> events = response.body();
                String json;

                try {
                    json = new Gson().toJson(events);
                    SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
                    prefs.edit().putString("EVENTS_JSON", json).apply();
                    originalListOfEvents = events;

                    callback.onDownloadFinished(events, null);
                } catch (Exception ex) {
                    callback.onDownloadFinished(null, context.getString(R.string.error_message) + ": " + ex);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                //getMockedEvents(context, callback);

                callback.onDownloadFinished(null, context.getString(R.string.error_message) + ": " + t);
            }
        });
    }

    private void downloadInterestingEvents(final Context context, final callbackInterface callback) {
        api.getInterestingEvents(Common.getToken(context)).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                List<Event> events = response.body();
                String json;

                try {
                    json = new Gson().toJson(events);
                    SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
                    prefs.edit().putString("EVENTS_JSON", json).apply();
                    originalListOfEvents = events;

                    callback.onDownloadFinished(events, null);
                } catch (Exception ex) {
                    callback.onDownloadFinished(null, context.getString(R.string.error_message) + ": " + ex);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                //getMockedEvents(context, callback);

                callback.onDownloadFinished(null, context.getString(R.string.error_message) + ": " + t);
            }
        });
    }
}
