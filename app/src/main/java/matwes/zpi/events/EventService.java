package matwes.zpi.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

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

    static EventService getInstance() {
        return ourInstance;
    }

    private ApiInterface api;

    private EventService() {
        api = RestService.getApiInstance();
    }

    void downloadEvents(Context context, final boolean connectionError, EventFragmentType pageType, callbackInterface callback) {
        if (Common.isMocked()) {
            getMockedEvents(context, callback);
        } else {
            switch (pageType) {
                case blocked:
                    downloadBlockedEvents(context, connectionError, callback);
                    break;
                case unblocked:
                    downloadUnblockedEvents(context, connectionError, callback);
                    break;
            }
        }
    }

    private void getMockedEvents(Context context, callbackInterface callback) {
        List<Event> events = Common.getMockedEvents();
        String json;
        try {
            json = new Gson().toJson(events);
            SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
            prefs.edit().putString("EVENTS_JSON", json).apply();

            callback.onDownloadFinished(events, null);
        } catch (Exception ignored) {
            callback.onDownloadFinished(null, R.string.error_message);
        }
    }

    private void downloadUnblockedEvents(final Context context, final boolean connectionError, final callbackInterface callback) {

            api.getEvents().enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                    List<Event> events = response.body();
                    String json;

                    try {
                        json = new Gson().toJson(events);
                        SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
                        prefs.edit().putString("EVENTS_JSON", json).apply();

                        callback.onDownloadFinished(events, null);
                    } catch (Exception ignored) {
                        callback.onDownloadFinished(null, R.string.error_message);

                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                    getMockedEvents(context, callback);
                   // callback.onDownloadFinished(null, R.string.error_message);
                }
            });
    }

    private void downloadBlockedEvents(final Context context, final boolean connectionError, final callbackInterface callback) {
            api.getBlockedEvents().enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                    List<Event> events = response.body();
                    String json;

                    try {
                        json = new Gson().toJson(events);
                        SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
                        prefs.edit().putString("EVENTS_JSON", json).apply();

                        callback.onDownloadFinished(events, null);
                    } catch (Exception ignored) {
                        callback.onDownloadFinished(null, R.string.error_message);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                    getMockedEvents(context, callback);

//                    callback.onDownloadFinished(null, R.string.error_message);
                }
            });
    }
}
