package matwes.zpi.eventDetails;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.Event;
import matwes.zpi.domain.Location;
import matwes.zpi.domain.Place;
import matwes.zpi.messages.MessageActivity;
import matwes.zpi.utils.CustomDialog;
import matwes.zpi.utils.LoadingDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventDetailsActivity extends AppCompatActivity {
    private ImageView location, members, messages;
    private Button join;
    private LoadingDialog dialog;

    private Event event;
    private String userId;

    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        api = RestService.getApiInstance();

        Intent intent = getIntent();
        final String eventId = intent.getStringExtra("eventId");
        userId = Common.getCurrentUserId(this);

        event = getEvent(eventId);

        dialog = new LoadingDialog(this);
        TextView eName = findViewById(R.id.eventName);
        TextView eSport = findViewById(R.id.eventSport);
        TextView eTime = findViewById(R.id.eventTime);
        TextView eDescription = findViewById(R.id.eventDescription);
        TextView eMembers = findViewById(R.id.eventMembers);
        location = findViewById(R.id.ivLocation);
        members = findViewById(R.id.ivMembers);
        messages = findViewById(R.id.ivMessages);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeMap();
            }
        });
        location.callOnClick();
        members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new GsonBuilder().create();
                replaceFragment(MembersFragment.newInstance(gson.toJson(event)), members);
            }
        });
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra("eventId", event.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

        join = findViewById(R.id.joinButton);
        if (userId.equals(event.getCreatorId())) {
            {
                join.setText(R.string.update);
                join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), UpdateEventActivity.class);
                        intent.putExtra("eventId", event.getId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                });
            }
        } /*else {
            if (isMember()) {
                join.setText("LEAVE");
                join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.isOnline(getApplicationContext())) {
                            dialog.showLoadingDialog(getString(R.string.loading));
                            handleApiResponse(api.cancelInterested(eventId));
                        } else {
                            Snackbar.make(findViewById(R.id.eventDetailView), R.string.noInternet, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.isOnline(getApplicationContext())) {
                            dialog.showLoadingDialog(getString(R.string.loading));
                            handleApiResponse(api.interested(eventId));
                        } else {
                            Snackbar.make(findViewById(R.id.eventDetailView), R.string.noInternet, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }*/

        eName.setText(event.getName());
//        if (event.getSportName() != null) {
//            eSport.setText(event.getSportName());
//        }
        eTime.setText(event.getDateWithTimeString());
//        eDescription.setText(event.getDescription());
        eMembers.setText(event.getInterested() + "");
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
        replaceFragment(fragment, location);
    }

    void replaceFragment(Fragment fragment, ImageView iv) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, fragment);
        ft.commit();

        location.setSelected(false);
        members.setSelected(false);
        messages.setSelected(false);
        iv.setSelected(true);
    }

    private void handleApiResponse(Call<Void> call) {
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                join.setEnabled(false);
                join.setText("ZOSTAŁEŚ ZAAKCEPTOWANY");
                if (members.isSelected()) {
                    MembersFragment fragment = (MembersFragment) getSupportFragmentManager().findFragmentById(R.id.frame);
                    fragment.updateMembers();
                }
                dialog.hideLoadingDialog();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                dialog.hideLoadingDialog();
                CustomDialog.showError(EventDetailsActivity.this, getString(R.string.error_message));
            }
        });
    }
}