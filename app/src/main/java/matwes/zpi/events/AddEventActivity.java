package matwes.zpi.events;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.Event;
import matwes.zpi.domain.Location;
import matwes.zpi.domain.Place;
import matwes.zpi.domain.Price;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.etEventName) EditText etName;
    @BindView(R.id.addEventMainActivity) LinearLayout mainLinearLayout;
    @BindView(R.id.etEventType) EditText etType;
    @BindView(R.id.etEventDate) EditText etDate;
    @BindView(R.id.minPrice) EditText minPrice;
    @BindView(R.id.maxPrice) EditText maxPrice;
    @BindView(R.id.addEventCardView) CardView cardView;
    @BindView(R.id.promotorTextView) EditText promotorTextView;
    PlaceAutocompleteFragment placeAutocompleteFragment;

    private LoadingDialog dialog;
    private String sDate, sTime;
    private String address;
    private double dLat, dLng;
    boolean placeWasSet = false;

    private ApiInterface api;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);

        api = RestService.getApiInstance();
        initGooglePlaceApi();

        dialog = new LoadingDialog(this);

        mainLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                return false;
            }
        });

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
        placeAutocompleteFragment = (PlaceAutocompleteFragment) placeFragment;
        placeAutocompleteFragment.setHint(getString(R.string.place));
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                LatLng latLng = place.getLatLng();
                placeWasSet = true;
                address = place.getAddress().toString();
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

        Event newEvent = new Event();
        String eventName =  etName.getText().toString().trim();
        String minPriceData = minPrice.getText().toString();
        String maxPriceData = maxPrice.getText().toString();
        Price eventPrice = null;
        if (!minPriceData.equals("") && !maxPriceData.equals("")) {
            eventPrice = new Price("PLN", Integer.parseInt(minPriceData), Integer.parseInt(maxPriceData));
        }
        String eventType = etType.getText().toString().trim();
        String[] eventDateTime = etDate.getText().toString().trim().split(" ");
        Location location = new Location(dLat,dLng);
        Place place = new Place("","", address ,"", "", location);
        String promotor = promotorTextView.getText().toString().trim();


        if (eventName.equals("") && eventPrice == null && eventType.equals("") && eventDateTime.length != 2 && address == null && !placeWasSet && promotor.equals("")) {
            CustomDialog.showError(AddEventActivity.this, "Wszystkie pola są wymagane");
            return;
        }

        dialog.showLoadingDialog(getString(R.string.loading));

        newEvent.setName(eventName);
        newEvent.setPrice(eventPrice);
        newEvent.setType(eventType);
        newEvent.setDate(eventDateTime[0]);
        newEvent.setTime(eventDateTime[1]);
        newEvent.setPlace(place);
        newEvent.setPromoter(promotor);
        Gson gson = new Gson();
        String json = gson.toJson(newEvent);
        System.out.println(json);
//        Call<Void> call = api.createEvent(id, date, description, maxMembers, name, dLat, dLng, time);
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                dialog.hideLoadingDialog();
//                finish();
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                dialog.hideLoadingDialog();
//                CustomDialog.showError(AddEventActivity.this, getString(R.string.error_message));
//            }
//        });
    }
}
