package matwes.zpi.eventDetails;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.Event;
import matwes.zpi.domain.Location;
import matwes.zpi.domain.Place;
import matwes.zpi.domain.Price;
import matwes.zpi.events.AddEventActivity;
import matwes.zpi.utils.CustomDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {
    String eventId = "";
    @BindView(R.id.editButton)
    Button editButtonImage;
    private Event event;
    private ApiInterface api;
    private ImageView eImage;
    private TextView eName, eType, eTime, ePlace, ePlace2, eMoney, eMembers;
    private Button btnInt, btnNInt;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);

        api = RestService.getApiInstance();

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");

        getEvent(eventId);

        editButtonImage.setVisibility(View.GONE);

        eImage = findViewById(R.id.eventImage);
        eName = findViewById(R.id.eventName);
        eType = findViewById(R.id.eventType);
        eTime = findViewById(R.id.eventTime);
        ePlace = findViewById(R.id.eventPlace);
        ePlace2 = findViewById(R.id.eventAddress);
        eMoney = findViewById(R.id.eventMoney);
        eMembers = findViewById(R.id.eventMembers);

        btnInt = findViewById(R.id.btnInterested);
        btnNInt = findViewById(R.id.btnNotInterested);

        final String token = Common.getToken(getApplicationContext());

        btnInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event.isInterested()) {
                    deactivateButton(btnInt);
                    event.setInterested(false);
                    event.decreaseInterested();
                    handleResponse(api.cancelInterested(token, event.get_id()));
                } else {
                    activateButton(btnInt);
                    if (event.isNotInterested()) {
                        deactivateButton(btnNInt);
                        event.setNotInterested(false);
                    }
                    event.setInterested(true);
                    event.increaseInterested();
                    handleResponse(api.interested(token, event.get_id()));
                }
                updateInterestedNote(eMembers);
            }
        });

        btnNInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event.isNotInterested()) {
                    deactivateButton(btnNInt);
                    event.setNotInterested(false);
                    handleResponse(api.cancelInterested(token, event.get_id()));
                } else {
                    activateButton(btnNInt);
                    if (event.isInterested()) {
                        deactivateButton(btnInt);
                        event.setInterested(false);
                        event.decreaseInterested();
                    }
                    event.setNotInterested(true);
                    handleResponse(api.notInterested(token, event.get_id()));
                }
                updateInterestedNote(eMembers);
            }
        });

    }

    @OnClick(R.id.editButton)
    public void editEvent() {
        Intent intent = new Intent(this, AddEventActivity.class);
        intent.putExtra("event", event);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null) {
            Bundle b = intent.getExtras();
            String str = b.getString("deleted");
            if (str != null) {
                finish();
                return;
            }
        }
        getEvent(eventId);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void handleResponse(Call<ResponseBody> call) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.errorBody() != null) {
                    try {
                        CustomDialog.showError(EventDetailsActivity.this, response.errorBody().string());
                    } catch (IOException e) {
                        CustomDialog.showError(EventDetailsActivity.this, getString(R.string.error_message));
                        e.printStackTrace();
                    }
                } else if (response.code() != 200 || response.body() == null) {
                    CustomDialog.showError(EventDetailsActivity.this, getString(R.string.error_message));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                CustomDialog.showError(EventDetailsActivity.this, getString(R.string.error_message));
            }
        });
    }

    private void updateInterestedNote(TextView textView) {
        textView.setText(getString(R.string.people_interested) + " " + event.getInterested());
    }

    private void activateButton(Button button) {
        button.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        button.setTextColor(Color.WHITE);
    }

    private void deactivateButton(Button button) {
        button.setBackground(getDrawable(R.drawable.my_button));
        button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_gray));
    }

    private void getEvent(String id) {
        System.out.println("TOKEN!!!");
        System.out.println(Common.getToken(getApplicationContext()));
        api.getEvent(Common.getToken(getApplicationContext()), id).enqueue(new Callback<Event>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {

                event = response.body();

                if (event == null) {
                    CustomDialog.showError(EventDetailsActivity.this, getString(R.string.error_message));
                }

                eName.setText(event.getName());
                eType.setText(event.getType() + ", " + event.getPromoter());
                eTime.setText(event.getDateWithTimeString());
                Place place = event.getPlace();
                if (place != null) {
                    if (place.getName() != null) {
                        ePlace.setText(place.getName());
                    }
                    if (place.getAddress() != null) {
                        ePlace2.setText(place.getAddress());
                    }
                }

                eMembers.setText(getString(R.string.people_interested) + " " + event.getInterested());

                if (event.isInterested()) {
                    activateButton(btnInt);
                } else if (event.isNotInterested()) {
                    activateButton(btnNInt);
                }

                if (event.getPrice() == null) {
                    eMoney.setText("???");
                } else {
                    Price price = event.getPrice();
                    eMoney.setText(price.getMin() + " - " + price.getMax() + " " + price.getCurrency());
                }

                if (event.getImage() == null || event.getImage().equals("")) {
                    Picasso.get()
                            .load(Common.getEventPlaceholder())
                            .placeholder(Common.getEventPlaceholder())
                            .into(eImage);
                } else {
                    Picasso.get()
                            .load(event.getImage())
                            .placeholder(Common.getEventPlaceholder())
                            .into(eImage);
                }

                updateInterestedNote(eMembers);

                System.out.println(event);

                initializeMap();
                if (event.isCreator()) {
                    editButtonImage.setVisibility(View.VISIBLE);
                } else {
                    editButtonImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                CustomDialog.showError(EventDetailsActivity.this, getString(R.string.error_message));
            }
        });
    }

    private void initializeMap() {
        SupportMapFragment fragment = new SupportMapFragment();

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (event.getPlace() != null) {
                    Place place = event.getPlace();
                    Location location = place.getLocation();

                    LatLng marker = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions()
                            .position(marker)
                            .title(event.getName())
                            .snippet(event.getDateWithTimeString() + "\n" + event.getInterested())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(marker)
                            .zoom(15)
                            .build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }
}