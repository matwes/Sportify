package matwes.zpi;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matwes.zpi.domain.Event;

public class Common {
    public static final String[] permission = {"email", "public_profile"};
    public static String URL = "http://10.0.2.2:8080";

    public static boolean isMocked() {
        return true;
    }

    public static boolean isEmailWrong(String email) {
        Pattern emailPattern =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);
        return !matcher.find();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLoginStatus(Context context, boolean logged) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("logged", logged);
        editor.apply();
    }

    public static String getCurrentUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
        return prefs.getString("USER_ID", "");
    }

    static String getBearer(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
        return prefs.getString("BEARER", "");
    }

    public static boolean getLoginStatus(Context context) {
        return getSharedPreferences(context).getBoolean("logged", false);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static int getEventPlaceholder() {
        return R.drawable.event_placeholder;
    }

    public static String getPolishSex(String sex) {
        String result = "Nie sprecyzowano";
        switch (sex) {
            case "FEMALE":
                result = "Kobieta";
                break;
            case "MALE":
                result = "Mężczyzna";
                break;
        }
        return result;
    }


    public static List<Event> getMockedEvents(Context context) {
        Gson gson = new GsonBuilder().create();
        String json = loadJSONFromAsset(context);
        Type type = new TypeToken<List<Event>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    private static String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("events.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return "[]";
        }
        return json;
    }
}
