package matwes.zpi.events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
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
import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.RequestAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class AddEventActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, AsyncTaskCompleteListener<String> {
    private static final String CREATE_EVENT = Common.URL + "/events";
    String sDate, sTime;
    String placeId = "";
    private EditText name, description, members, date;
    private AutoCompleteTextView autoCompleteTextView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initGooglePlaceApi();

        name = findViewById(R.id.etEventName);
        description = findViewById(R.id.etEventDescription);
        members = findViewById(R.id.etEventMembers);
        date = findViewById(R.id.etEventDate);

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
                new TimePickerDialog(AddEventActivity.this, timePicker, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                sDate = String.format("%02d-%02d-%02d", year, month + 1, day);
                date.setText(day + "-" + (month + 1) + "-" + year);
            }
        };


        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    new DatePickerDialog(AddEventActivity.this, datePicker, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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

        PlaceAutocompleteFragment placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_fragment);
        placeAutocompleteFragment.setHint("Miejsce");
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
        new AlertDialog
                .Builder(this)
                .setTitle("Error")
                .setMessage("Problem with connection")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onTaskComplete(String result) {
        if (result.equals("200"))
            finish();
        else
            showAlert(getString(R.string.required_fields));
    }

    private void createEvent() {
        long id = Common.getCurrentUserId(this);

        JSONObject json = new JSONObject();
        try {
            json.put("creator_id", id);
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
        Log.i("NEW_EVENT", json.toString());
        new RequestAPI(this, "POST", json.toString(), this, true).execute(CREATE_EVENT);
    }

    private void showAlert(String message) {
        new AlertDialog
                .Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
