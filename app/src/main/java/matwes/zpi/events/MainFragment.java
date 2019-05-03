package matwes.zpi.events;

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

import java.lang.reflect.Array;
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
import matwes.zpi.callbackInterface;
import matwes.zpi.domain.Event;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;

/**
 * Created by mateu on 18.05.2017.
 */

public abstract class MainFragment extends Fragment {
    public EventFragmentType type = EventFragmentType.unblocked;
    protected View parentView;
    protected LoadingDialog dialog;
    protected List<Event> events;
    protected Date maxDateSelected, minDateSelected;
    protected String selectedName, selectedMaxPrice;
    protected List<Boolean> selectedCheckboxes;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentView = view;

        dialog = new LoadingDialog(getContext());

        maxDateSelected = null;
        minDateSelected = null;
        selectedCheckboxes = new ArrayList<>();
        selectedCheckboxes.add(false);
        selectedCheckboxes.add(false);
        selectedCheckboxes.add(false);

        selectedName = "";
        selectedMaxPrice = "";

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
            updateList(filterEvents(e));
        }
    }

    void updateList(List<Event> e) {

//        if (!filtered) {
//            boolean minDateChanged = false;
//            boolean maxDateChanged = false;
//
//            minDate = new Date(Long.MAX_VALUE);
//            maxDate = new Date(Long.MIN_VALUE);
//
//            for (Event event : e) {
//                if (event.getDateObject().before(minDate)) {
//                    minDateChanged = true;
//                    minDate = event.getDateObject();
//                }
//                if (event.getDateObject().after(maxDate)) {
//                    maxDateChanged = true;
//                    maxDate = event.getDateObject();
//                }
//            }
//
//            if (!minDateChanged) {
//                minDate = new Date();
//            }
//            if (!maxDateChanged) {
//                maxDate = new Date();
//            }
//
//            minDateSelected = minDate;
//            maxDateSelected = maxDate;
//        }

        events.clear();
//        if (filtered) {
//            List<Event> dest= new ArrayList<Event>();
//            dest.addAll(e);
//
//            filterEvents(dest);
//        }else {
        events.addAll(e);
//        }

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
        } else {
            if (Common.isOnline(getContext())) {

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
            } else if (connectionError) {
                Snackbar.make(parentView, R.string.noInternet, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    void filterDialog() {

        List<String> eventsNames = new ArrayList<String>();

        for (Event ev: EventService.getInstance().originalListOfEvents) {
            eventsNames.add(ev.getName());
        }

        ArrayAdapter<String> eventAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, eventsNames);


        final FilterDialog filterDialog = new FilterDialog(getContext(), eventAdapter);

//        if (filtered) {
            filterDialog.update(selectedName, selectedMaxPrice, minDateSelected, maxDateSelected, selectedCheckboxes);
//        } else {
//            filterDialog.update(selectedName, selectedMaxPrice, minDate, maxDate);
//        }

        filterDialog.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedName = filterDialog.getSelectedEventName();
                selectedMaxPrice = filterDialog.getSelectedPrice();
                selectedCheckboxes = filterDialog.getSelectedCheckboxes();

                filterDialog.dismiss();
//                filtered = true;

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

    List<Event> filterEvents(List<Event> events) {

        if (!this.selectedCheckboxes.get(0) || !this.selectedCheckboxes.get(1) || !this.selectedCheckboxes.get(2)) {

            Iterator<Event> i = events.iterator();

            while (i.hasNext()) {
                Event event = i.next();
                Date act = event.getDateObject();
                if (minDateSelected != null && maxDateSelected != null) {
                    if (this.selectedCheckboxes.get(2) &&
                            (event.getDateObject().before(minDateSelected) ||
                            event.getDateObject().after(maxDateSelected))
                    ) {
                        i.remove();
                    }
                }

                if ((this.selectedCheckboxes.get(0) && !event.getName().contains(selectedName))) {
                    i.remove();
                }

                if (this.selectedCheckboxes.get(1) && (event.getPrice().getMin() >= Double.parseDouble((selectedMaxPrice)))) {
                    i.remove();
                }
            }
        }

        return events;
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
