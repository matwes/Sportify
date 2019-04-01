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
import com.google.android.gms.maps.model.LatLng;

import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.Event;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateEventActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private EditText name, description, members, date;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<CharSequence> adapter;
    private LoadingDialog dialog;

    private long eventId;
    private String sDate, sTime;
    private double dLat, dLng;

    private ApiInterface api;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        api = RestService.getApiInstance();

        initGooglePlaceApi();

        Intent intent = getIntent();
        eventId = intent.getLongExtra("eventId", -1);

        dialog = new LoadingDialog(this);
        name = findViewById(R.id.etEventName);
        description = findViewById(R.id.etEventDescription);
        members = findViewById(R.id.etEventMembers);
        date = findViewById(R.id.etEventDate);

        getEvent();

        final TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                sTime = String.format("%02d:%02d", hourOfDay, minute);
                date.setText(String.format("%s %d:%d", date.getText().toString(), hourOfDay, minute));
            }
        };

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                new TimePickerDialog(UpdateEventActivity.this, timePicker, hour, minute, true).show();
                sDate = String.format("%02d-%02d-%02d", year, month + 1, day);
                date.setText(String.format("%d-%d-%d", day, month + 1, year));
            }
        };

        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    new DatePickerDialog(UpdateEventActivity.this, datePicker, year, month, day).show();
                }
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
                dialog.showLoadingDialog(getString(R.string.loading));
                Call<Void> call = api.deleteEvent(eventId);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        dialog.hideLoadingDialog();
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        dialog.hideLoadingDialog();
                        CustomDialog.showError(UpdateEventActivity.this, getString(R.string.error_message));
                    }
                });
            }
        });
    }

    private void getEvent() {
        dialog.showLoadingDialog(getString(R.string.loading));
        Call<Event> eventCall = api.getEvent(eventId);
        eventCall.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                Event event = response.body();

                name.setText(event.getName());
                //description.setText(event.getDescription());
                members.setText(event.getInterested() + "");
                date.setText(event.getDateWithTimeString());
                placeAutocompleteFragment.setText(event.getPlace().getName());
                //autoCompleteTextView.setText(event.getSport().getName());
                autoCompleteTextView.setAdapter(adapter);
                //placeId = event.getPlace().getGoogleId();
                sTime = event.getTime();
                sDate = event.getDate();

                dialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                dialog.hideLoadingDialog();
                CustomDialog.showError(UpdateEventActivity.this, getString(R.string.error_message));
            }
        });
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
                LatLng latLng = place.getLatLng();
                dLat = latLng.latitude;
                dLng = latLng.longitude;
            }

            @Override
            public void onError(Status status) {
                Log.i("PLACE_ERROR", getString(R.string.error_message) + "\n" + status);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        CustomDialog.showError(this, getString(R.string.error_message));
    }

    private void updateEvent() {
        dialog.showLoadingDialog(getString(R.string.loading));
        Call<Void> call = api.updateEvent(eventId, sDate, description.getText().toString(),
                members.getText().toString(), name.getText().toString(), dLat, dLng, sTime);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                dialog.hideLoadingDialog();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                dialog.hideLoadingDialog();
                CustomDialog.showError(UpdateEventActivity.this, getString(R.string.error_message));
            }
        });
    }
}