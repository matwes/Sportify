package matwes.zpi;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matwes.zpi.domain.Event;
import matwes.zpi.domain.Member;
import matwes.zpi.domain.Message;
import matwes.zpi.domain.Place;
import matwes.zpi.domain.Sport;

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

    public static long getCurrentUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
        return prefs.getInt("USER_ID", 0);
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

    public static int getIcon(String sport) {
        switch (sport) {
            case "Piłka nożna":
                return R.drawable.sport_soccer;
            case "Siatkówka":
                return R.drawable.sport_volleyball;
            case "Polowanie na pokemony":
                return R.drawable.sport_pokeball;
            case "Siłownia":
                return R.drawable.sport_gym;
            case "Koszykówka":
                return R.drawable.sport_basketball;
            case "Badminton":
                return R.drawable.sport_badminton;
            case "Squash":
                return R.drawable.sport_squash;
            case "Golf":
                return R.drawable.sport_golf;
            case "Bieganie":
                return R.drawable.sport_running;
            case "Pływanie":
                return R.drawable.sport_swim;
            case "Piłka ręczna":
                return R.drawable.sport_handball;
            case "Tenis":
                return R.drawable.sport_tennis;
            default:
                return R.drawable.sport_soccer;
        }
    }

    public static int getEventPlaceholder() {
        return R.drawable.event_placeholder;
    }

    public static int getSportId(String sport) {
        int result;
        switch (sport) {
            case "Piłka nożna":
                result = 1;
                break;
            case "Koszykówka":
                result = 2;
                break;
            case "Siatkówka":
                result = 3;
                break;
            case "Siłownia":
                result = 4;
                break;
            case "Polowanie na pokemony":
                result = 5;
                break;
            case "Badminton":
                result = 6;
                break;
            case "Squash":
                result = 7;
                break;
            case "Golf":
                result = 8;
                break;
            case "Bieganie":
                result = 9;
                break;
            case "Pływanie":
                result = 10;
                break;
            case "Piłka ręczna":
                result = 11;
                break;
            case "Tenis":
                result = 12;
                break;
            default:
                result = 1;
        }
        return result;
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


    public static List<Event> getMockedEvents() {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Sport sport = new Sport(1, "Piłka nożna");
            Place place = new Place("", "Miejsce imprezy", "Wrocław", "", 51.1078852, 17.0385376);
            Event event = new Event(
                    i,
                    "Event nr " + i,
                    null,
                    sport,
                    i * 7,
                    "2018-04-0" + i,
                    place,
                    "opis dotyczący eventu",
                    "1" + i + ":2" + i,
                    "",
                    new ArrayList<Message>(),
                    new ArrayList<Member>());
            events.add(event);
        }

        return events;
    }
}
