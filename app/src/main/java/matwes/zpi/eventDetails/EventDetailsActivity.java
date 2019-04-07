package matwes.zpi.eventDetails;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.Event;
import matwes.zpi.domain.Location;
import matwes.zpi.domain.Place;
import matwes.zpi.domain.Price;
import matwes.zpi.utils.LoadingDialog;


public class EventDetailsActivity extends AppCompatActivity {
    private LoadingDialog dialog;

    private Event event;
    private String userId;

    private ApiInterface api;

    private boolean interested, nInterested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        api = RestService.getApiInstance();
        initializeMap();

        Intent intent = getIntent();
        final String eventId = intent.getStringExtra("eventId");
        userId = Common.getCurrentUserId(this);

        event = getEvent(eventId);

        dialog = new LoadingDialog(this);
        ImageView eImage = findViewById(R.id.eventImage);
        TextView eName = findViewById(R.id.eventName);
        TextView eType = findViewById(R.id.eventType);
        TextView eTime = findViewById(R.id.eventTime);
        TextView ePlace = findViewById(R.id.eventPlace);
        TextView ePlace2 = findViewById(R.id.eventAddress);
        TextView eMoney = findViewById(R.id.eventMoney);
        final TextView eMembers = findViewById(R.id.eventMembers);
        final Button btnInt = findViewById(R.id.btnInterested);
        final Button btnNInt = findViewById(R.id.btnNotInterested);

        eName.setText(event.getName());
        eType.setText(event.getType() + ", " + event.getPromoter());
        eTime.setText(event.getDateWithTimeString());
        ePlace.setText(event.getPlace().getName());
        ePlace2.setText(event.getPlace().getAddress());
        eMembers.setText(getString(R.string.people_interested) + " " + event.getInterested());

        if (event.getPrice().isEmpty()) {
            eMoney.setText("???");
        } else {
            Price price = event.getPrice().get(0);
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

        btnInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interested) {
                    deactivateButton(btnInt);
                    interested = false;
                    event.decreaseInterested();
                } else {
                    activateButton(btnInt);
                    if (nInterested) {
                        deactivateButton(btnNInt);
                        nInterested = false;
                    }
                    interested = true;
                    event.increaseInterested();
                }
                updateInterestedNote(eMembers);
            }
        });

        btnNInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nInterested) {
                    deactivateButton(btnNInt);
                    nInterested = false;
                } else {
                    activateButton(btnNInt);
                    if (interested) {
                        deactivateButton(btnInt);
                        interested = false;
                        event.decreaseInterested();
                    }
                    nInterested = true;
                }
                updateInterestedNote(eMembers);
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

    @Nullable
    private Event getEvent(String id) {
        SharedPreferences prefs = getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
        ArrayList<Event> events = Event.jsonEventsToList(prefs.getString("EVENTS_JSON", "[]"));
        for (Event e : events) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
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