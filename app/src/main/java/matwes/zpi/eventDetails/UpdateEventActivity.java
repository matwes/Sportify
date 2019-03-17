package matwes.zpi.eventDetails;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.GetMethodAPI;
import matwes.zpi.R;
import matwes.zpi.RequestAPI;
import matwes.zpi.domain.Event;
import matwes.zpi.utils.CustomDialog;

public class UpdateEventActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String UPDATE_EVENT = Common.URL + "/events/";
    private long eventId;
    private AutoCompleteTextView autoCompleteTextView;
    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private EditText name, description, members, date;
    private String placeId = "";
    private String sDate, sTime;
    private ArrayAdapter<CharSequence> adapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        initGooglePlaceApi();

        Intent intent = getIntent();
        eventId = intent.getLongExtra("eventId", -1);

        name = findViewById(R.id.etEventName);
        description = findViewById(R.id.etEventDescription);
        members = findViewById(R.id.etEventMembers);
        date = findViewById(R.id.etEventDate);

        getEvent();

        final TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                sTime = String.format("%02d:%02d", hourOfDay, minute);
                date.setText(date.getText().toString() + (" " + hourOfDay + ":" + minute));
            }
        };

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                new TimePickerDialog(UpdateEventActivity.this, timePicker, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                sDate = String.format("%02d-%02d-%02d", year, month + 1, day);
                date.setText(day + "-" + (month + 1) + "-" + year);
            }
        };


        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    new DatePickerDialog(UpdateEventActivity.this, datePicker, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        adapter = ArrayAdapter.createFromResource(this, R.array.sport_list, R.layout.spinner_item);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(null);
        autoCompleteTextView.setKeyListener(null);
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                return false;
            }
        });

        Button updateEvent = findViewById(R.id.btnAddEvent);
        updateEvent.setText(R.string.updateBtn);
        updateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvent();
            }
        });

        Button deleteEvent = findViewById(R.id.btnDelEvent);
        deleteEvent.setVisibility(View.VISIBLE);
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestAPI(v.getContext(), "DELETE", "", new AsyncTaskCompleteListener<String>() {
                    @Override
                    public void onTaskComplete(String result) {
                        finish();
                    }
                }, true).execute(UPDATE_EVENT + eventId);
            }
        });
    }

    private void getEvent() {
        new GetMethodAPI(this, new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                Gson gson = new GsonBuilder().create();
                Event event = gson.fromJson(result, Event.class);
                name.setText(event.getName());
                description.setText(event.getDescription());
                members.setText(event.getMaxMembers() + "");
                date.setText(event.getDateWithTimeString());
                placeAutocompleteFragment.setText(event.getPlace().getName());
                autoCompleteTextView.setText(event.getSport().getName());
                autoCompleteTextView.setAdapter(adapter);
                placeId = event.getPlace().getGoogleId();
                sTime = event.getTime();
                sDate = event.getDateString();
            }
        }, true).execute(String.format("%s/events/%d", Common.URL, eventId));
    }

    private void initGooglePlaceApi() {
        new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_fragment);
        placeAutocompleteFragment.setHint("Enter location");
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                placeId = place.getId();
            }

            @Override
            public void onError(Status status) {
                Log.i("PLACE_ERROR", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        CustomDialog.showError(this, getString(R.string.error_message));
    }

    private void updateEvent() {
        JSONObject json = new JSONObject();
        try {
            json.put("date", sDate);
            json.put("description", description.getText().toString());
            json.put("maxMembers", members.getText().toString());
            json.put("name", name.getText().toString());
            json.put("place_googleId", placeId);
            json.put("sport_id", Common.getSportId(autoCompleteTextView.getText().toString()));
            json.put("time", sTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestAPI(this, "PATCH", json.toString(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                if (result.equals("200")) {
                    finish();
                } else {
                    CustomDialog.showError(getApplicationContext(), getString(R.string.error_message));
                }
            }
        }, true).execute(UPDATE_EVENT + eventId);
    }
}
