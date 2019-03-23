package matwes.zpi.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.callbackInterface;
import matwes.zpi.domain.Event;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mateu on 18.05.2017.
 */

public abstract class MainFragment extends Fragment {
    protected View parentView;
    protected LoadingDialog dialog;

    protected List<Event> events;
    protected Date maxDate, minDate, maxDateSelected, minDateSelected;
    protected String selectedSport, selectedCity;
    protected boolean filtered;
    public EventFragmentType type = EventFragmentType.unblocked;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentView = view;

        dialog = new LoadingDialog(getContext());

        maxDate = maxDateSelected = new Date(Long.MIN_VALUE);
        minDate = minDateSelected = new Date(Long.MAX_VALUE);

        selectedSport = "Wszystkie";
        selectedCity = "Wszystkie miasta";
        filtered = false;

        events = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.filter):
                filterDialog();
                return true;
            case (R.id.refresh):
                downloadEvents(true, true);
        }

        return super.onOptionsItemSelected(item);
    }

    void getEvents() {
        SharedPreferences prefs = getActivity().getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
        ArrayList<Event> e = Event.jsonEventsToList(prefs.getString("EVENTS_JSON", "[]"));
        if (e.isEmpty()) {
            downloadEvents(true, true);
        } else {
            updateList(e);
        }
    }

    void updateList(List<Event> e) {
        if (!filtered) {
            boolean minDateChanged = false;
            boolean maxDateChanged = false;

            minDate = new Date(Long.MAX_VALUE);
            maxDate = new Date(Long.MIN_VALUE);

            for (Event event : e) {
                if (event.getDate().before(minDate)) {
                    minDateChanged = true;
                    minDate = event.getDate();
                }
                if (event.getDate().after(maxDate)) {
                    maxDateChanged = true;
                    maxDate = event.getDate();
                }
            }

            if (!minDateChanged) {
                minDate = new Date();
            }
            if (!maxDateChanged) {
                maxDate = new Date();
            }

            minDateSelected = minDate;
            maxDateSelected = maxDate;
        }

        events.clear();
        if (filtered) {
            filterEvents(e);
        }
        events.addAll(e);
        Collections.sort(events);
    }

    void downloadEvents(final boolean connectionError, final boolean dialogLoading) {
        if (Common.isMocked()) {
            EventService.getInstance().downloadEvents(getContext(), connectionError, type, new callbackInterface() {
                @Override
                public <T> void onDownloadFinished(T data, Integer error) {
                    List<Event> events = (List<Event>) data;
                    if (events != null) {
                        updateList(events);
                    }
                    if (error != null) {
                        CustomDialog.showError(getContext(), getString(error));
                    }
                    onApiResponse();
                }
            });
        }else {
            if(Common.isOnline(getContext())) {

                if (dialogLoading) {
                    dialog.showLoadingDialog(getString(R.string.loading));
                }
                EventService.getInstance().downloadEvents(getContext(), connectionError, type, new callbackInterface() {
                    @Override
                    public <T> void onDownloadFinished(T data, Integer error) {
                        if (dialogLoading) {
                            dialog.hideLoadingDialog();
                        } else {
                            onApiResponse();
                        }

                        List<Event> events = (List<Event>) data;
                        if (events != null) {
                            updateList(events);
                        }
                        if (error != null) {
                            CustomDialog.showError(getContext(), getString(error));
                        }
                    }
                });
            }else if (connectionError) {
                Snackbar.make(parentView, R.string.noInternet, Snackbar.LENGTH_LONG).show();
            }
        }
    }
    void filterDialog() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.sport_list2, R.layout.spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.cities_list, R.layout.spinner_item);
        final FilterDialog filterDialog = new FilterDialog(getContext(), adapter, adapter2);

        if (filtered) {
            filterDialog.update(selectedSport, selectedCity, minDateSelected, maxDateSelected);
        } else {
            filterDialog.update(selectedSport, selectedCity, minDate, maxDate);
        }

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

    void filterEvents(List<Event> events) {
        Iterator<Event> i = events.iterator();
        while (i.hasNext()) {
            Event event = i.next();
            if ((!event.getSportName().equals(selectedSport) && !selectedSport.equals("Wszystkie")) ||
                    event.getDate().before(minDateSelected) ||
                    event.getDate().after(maxDateSelected) ||
                    (!event.getPlace().getCity().equals(selectedCity) && !selectedCity.equals("Wszystkie miasta")))
                i.remove();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadEvents(false, true);
    }

    void removeOldEvents(List<Event> events) {
    }

    void onApiResponse() {
    }
}
