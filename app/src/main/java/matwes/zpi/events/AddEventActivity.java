package matwes.zpi.events;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.Event;
import matwes.zpi.domain.Location;
import matwes.zpi.domain.NewEvent;
import matwes.zpi.domain.Place;
import matwes.zpi.domain.Price;
import matwes.zpi.domain.ReturnEvent;
import matwes.zpi.eventDetails.EventDetailsActivity;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.etEventName)
    EditText etName;
    @BindView(R.id.addEventMainActivity)
    LinearLayout mainLinearLayout;
    @BindView(R.id.etEventType)
    EditText etType;
    @BindView(R.id.etEventDate)
    EditText etDate;
    @BindView(R.id.minPrice)
    EditText minPrice;
    @BindView(R.id.maxPrice)
    EditText maxPrice;
    @BindView(R.id.addEventCardView)
    CardView cardView;
    @BindView(R.id.promotorTextView)
    EditText promoterTextView;
    @BindView(R.id.btnDeleteEvent)
    Button buttonDeleteEvent;

    PlaceAutocompleteFragment placeAutocompleteFragment;
    Event event = null;
    boolean placeWasSet = false;
    private LoadingDialog dialog;
    private String placeAddress, placeName;
    private double dLat, dLng;
    private ApiInterface api;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);

        Button addEvent = findViewById(R.id.btnAddEvent);

        event = (Event) getIntent().getSerializableExtra("event");

        Fragment placeFragment = getFragmentManager().findFragmentById(R.id.place_fragment);
        placeAutocompleteFragment = (PlaceAutocompleteFragment) placeFragment;
        buttonDeleteEvent.setVisibility(View.GONE);

        if (event != null) {
            etName.setText(event.getName());
            etType.setText(event.getType());
            String date = event.getDate().replace("T", " ");
            date = date.replace(".000Z", "");
            etDate.setText(date);

            if (event.getPrice() != null) {
                minPrice.setText(Double.toString(event.getPrice().getMin()));
                maxPrice.setText(Double.toString(event.getPrice().getMax()));
            }
            placeAutocompleteFragment.setText(event.getPlace().getAddress());
            promoterTextView.setText(event.getPromoter());
            addEvent.setText(R.string.updateBtn);
            buttonDeleteEvent.setVisibility(View.VISIBLE);
        }

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
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String date = etDate.getText().toString();
                etDate.setText(String.format("%s %02d:%02d", date, hourOfDay, minute));
            }
        };

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                new TimePickerDialog(AddEventActivity.this, timePicker, hour, minute, true).show();
                etDate.setText(String.format("%d-%02d-%02d", year, month + 1, day));
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
    }

    @OnClick(R.id.btnAddEvent)
    public void addEventTap() {
        createEvent();
    }

    @OnClick(R.id.btnDeleteEvent)
    public void deleteEventTap() {
        if (event != null) {
            dialog.showLoadingDialog(getString(R.string.loading));
            Call<Void> call = api.deleteEvent(Common.getToken(getApplicationContext()), event.get_id());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    dialog.hideLoadingDialog();
                    Intent data = new Intent();
                    data.putExtra("deleted", "");
                    setResult(RESULT_OK, data);
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


    private void initGooglePlaceApi() {
        new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                LatLng latLng = place.getLatLng();
                placeWasSet = true;
                if (place.getAddress() != null) {
                    placeAddress = place.getAddress().toString();
                }
                placeName = place.getName().toString();
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
        String name = etName.getText().toString().trim();
        String minPriceData = minPrice.getText().toString();
        String maxPriceData = maxPrice.getText().toString();
        Price price = null;
        if (!minPriceData.equals("") && !maxPriceData.equals("")) {
            price = new Price("PLN", Double.parseDouble(minPriceData), Double.parseDouble(maxPriceData));
        }
        String type = etType.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        Location location = new Location(dLat, dLng);
        Place place;
        if (placeWasSet) {
            place = new Place(placeName, "", placeAddress, location);
        } else if (event != null) {
            place = event.getPlace();
            placeWasSet = true;
        } else {
            place = null;
        }
        String promoter = promoterTextView.getText().toString().trim();

        if (name.equals("") || date.equals("") || !placeWasSet) {
            CustomDialog.showError(AddEventActivity.this, "Nazwa, data oraz miejsce musza zostac podane ");
            return;
        }

        DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        DateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());

        Date shortDate;
        try {
            shortDate = shortFormat.parse(date);
        } catch (ParseException e) {
            CustomDialog.showError(AddEventActivity.this, "Wprowadzona data jest niepoprawnie sformatowana");
            return;
        }
        String longDate = longFormat.format(shortDate);

        dialog.showLoadingDialog(getString(R.string.loading));

        NewEvent newEvent = new NewEvent(null, name, null, longDate, type, promoter, price, place);

        if (event != null) {
            newEvent.set_id(event.get_id());
        }

        Call<ReturnEvent> call = api.createEvent(Common.getToken(this), newEvent);
        call.enqueue(new Callback<ReturnEvent>() {
            @Override
            public void onResponse(@NonNull Call<ReturnEvent> call, @NonNull Response<ReturnEvent> response) {

                dialog.hideLoadingDialog();

                if (response.errorBody() != null) {
                    try {
                        String errorMessage = response.errorBody().string();
                        CustomDialog.showError(AddEventActivity.this, errorMessage);
                    } catch (IOException e) {
                        CustomDialog.showError(AddEventActivity.this, getString(R.string.error_message));
                        e.printStackTrace();
                    }
                    dialog.hideLoadingDialog();
                } else if (response.body() == null) {
                    CustomDialog.showError(AddEventActivity.this, getString(R.string.error_message));
                } else {
                    dialog.hideLoadingDialog();
                    if (response.body() != null && response.body().getEventId() != null && event == null) {
                        String eventId = response.body().getEventId();
                        Intent intent = new Intent(AddEventActivity.this, EventDetailsActivity.class);
                        intent.putExtra("eventId", eventId);
                        finish();
                        startActivity(intent);
                        return;
                    }
                }

                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ReturnEvent> call, @NonNull Throwable t) {
                dialog.hideLoadingDialog();
                CustomDialog.showError(AddEventActivity.this, getString(R.string.error_message));
            }
        });
    }
}
