package matwes.zpi.events;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
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

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private AutoCompleteTextView autoCompleteTextView;
    private EditText etName, etDescription, etMembers, etDate;
    private LoadingDialog dialog;
    private String sDate, sTime;
    private double dLat, dLng;

    private ApiInterface api;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        api = RestService.getApiInstance();
        initGooglePlaceApi();

        dialog = new LoadingDialog(this);
        etName = findViewById(R.id.etEventName);
        etDescription = findViewById(R.id.etEventDescription);
        etMembers = findViewById(R.id.etEventMembers);
        etDate = findViewById(R.id.etEventDate);

        final TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String date = etDate.getText().toString();
                sTime = String.format("%02d:%02d", hourOfDay, minute);
                etDate.setText(String.format("%s %d:%d", date, hourOfDay, minute));
            }
        };

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                new TimePickerDialog(AddEventActivity.this, timePicker, hour, minute, true).show();
                sDate = String.format("%02d-%02d-%02d", year, month + 1, day);
                etDate.setText(String.format("%d-%d-%d", day, month + 1, year));
            }
        };

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    new DatePickerDialog(AddEventActivity.this, datePicker, year, month, day).show();
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sport_list, R.layout.spinner_item);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setKeyListener(null);
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                return false;
            }
        });

        Button addEvent = findViewById(R.id.btnAddEvent);
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
    }

    private void initGooglePlaceApi() {
        new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        Fragment placeFragment = getFragmentManager().findFragmentById(R.id.place_fragment);
        PlaceAutocompleteFragment placeAutocompleteFragment = (PlaceAutocompleteFragment) placeFragment;
        placeAutocompleteFragment.setHint(getString(R.string.place));
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                LatLng latLng = place.getLatLng();
                dLat = latLng.latitude;
                dLng = latLng.longitude;
            }

            @Override
            public void onError(Status status) {
                CustomDialog.showError(AddEventActivity.this, getString(R.string.error_message));
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        CustomDialog.showError(AddEventActivity.this, getString(R.string.error_message));
    }

    private void createEvent() {
        dialog.showLoadingDialog(getString(R.string.loading));

        String id = Common.getCurrentUserId(this);
        String date = sDate;
        String description = etDescription.getText().toString();
        String maxMembers = etMembers.getText().toString();
        String name = etName.getText().toString();
        String time = sTime;

        Call<Void> call = api.createEvent(id, date, description, maxMembers, name, dLat, dLng, time);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                dialog.hideLoadingDialog();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                dialog.hideLoadingDialog();
                CustomDialog.showError(AddEventActivity.this, getString(R.string.error_message));
            }
        });
    }
}
