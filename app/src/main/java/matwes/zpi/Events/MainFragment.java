package matwes.zpi.Events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.Classes.Event;
import matwes.zpi.GetMethodAPI;
import matwes.zpi.R;

/**
 * Created by mateu on 18.05.2017.
 */

public abstract class MainFragment extends Fragment implements AsyncTaskCompleteListener<String> {
    View parentView;

    public ArrayList<Event> events;

    private Date maxDate, minDate;
    private Date maxDateSelected, minDateSelected;

    private String selectedSport;
    String selectedCity;
    public boolean filtered;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentView = view;

        maxDate = maxDateSelected = new Date(Long.MIN_VALUE);
        minDate = minDateSelected = new Date(Long.MAX_VALUE);

        selectedSport = "Wszystkie";
        selectedCity = "Wszystkie miasta";
        filtered = false;

        events = new ArrayList<>();
    }

    @Override
    public void onTaskComplete(String result) {
        String json;
        try {
            json = new JSONObject(result).getJSONArray("content").toString();
            SharedPreferences prefs = getActivity().getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
            prefs.edit().putString("EVENTS_JSON", json).apply();

            updateList(Event.jsonEventsToList(json));
        } catch (Exception ignored) {

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.filter):
                filterDialog();
                return true;
            case (R.id.refresh):
                downloadEvents();
        }

        return super.onOptionsItemSelected(item);
    }

    void getEvents() {
        SharedPreferences prefs = getActivity().getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
        ArrayList<Event> e = Event.jsonEventsToList(prefs.getString("EVENTS_JSON", "[]"));
        if (e.isEmpty())
            downloadEvents();
        else
            updateList(e);
    }

    void updateList(ArrayList<Event> e) {
        if (!filtered) {
            minDate = new Date(Long.MAX_VALUE);
            maxDate = new Date(Long.MIN_VALUE);

            for (Event event : e) {
                if (event.getDate().before(minDate))
                    minDate = event.getDate();
                if (event.getDate().after(maxDate))
                    maxDate = event.getDate();
            }
            minDateSelected = minDate;
            maxDateSelected = maxDate;
        }

        events.clear();
        if (filtered)
            filterEvents(e);
        removeOldEvents(e);
        events.addAll(e);
    }

    void downloadEvents() {
        if (Common.isOnline(getContext()))
            new GetMethodAPI(getContext(), this, true).execute("https://zpiapi.herokuapp.com/events?size=99");
        else
            Snackbar.make(parentView, R.string.noInternet, Snackbar.LENGTH_LONG).show();
    }

    void filterDialog() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.sport_list2, R.layout.spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.cities_list, R.layout.spinner_item);
        final FilterDialog filterDialog = new FilterDialog(getContext(), adapter, adapter2);

        if (filtered) {
            filterDialog.update(selectedSport, selectedCity, minDateSelected, maxDateSelected);
        } else
            filterDialog.update(selectedSport, selectedCity, minDate, maxDate);

        filterDialog.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSport = filterDialog.getSelectedSport();
                selectedCity = filterDialog.getSelectedCity();
                filterDialog.dismiss();
                filtered = true;

                getEvents();

            }
        });

        filterDialog.filterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmoothDateRangePickerFragment.newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                    @Override
                    public void onDateRangeSet(SmoothDateRangePickerFragment view, int yearStart, int monthStart, int dayStart, int yearEnd, int monthEnd, int dayEnd) {
                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                        try {
                            minDateSelected = df.parse(dayStart + "." + (monthStart + 1) + "." + yearStart);
                            maxDateSelected = df.parse(dayEnd + "." + (monthEnd + 1) + "." + yearEnd);
                            filterDialog.setSelectedDates(minDateSelected, maxDateSelected);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).show(getActivity().getFragmentManager(), "picker");
            }
        });

        filterDialog.show();
    }

    void filterEvents(ArrayList<Event> events) {
        Iterator<Event> i = events.iterator();
        while (i.hasNext()) {
            Event event = i.next();
            if ((!event.getSportName().equals(selectedSport) && !selectedSport.equals("Wszystkie")) ||
                    event.getDate().before(minDateSelected) ||
                    event.getDate().after(maxDateSelected) ||
                    (!event.getPlace().getCity().equals(selectedCity)&&!selectedCity.equals("Wszystkie miasta")))
                i.remove();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.isOnline(getContext()))
            new GetMethodAPI(getContext(), this, false).execute("https://zpiapi.herokuapp.com/events?size=99");
    }

    void removeOldEvents(ArrayList<Event> events) {
    }
}
