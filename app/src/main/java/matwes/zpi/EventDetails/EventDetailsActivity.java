package matwes.zpi.eventDetails;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.RequestAPI;
import matwes.zpi.domain.Event;
import matwes.zpi.domain.Member;
import matwes.zpi.domain.Place;
import matwes.zpi.messages.MessageActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class EventDetailsActivity extends AppCompatActivity
        implements AsyncTaskCompleteListener<String> {
    private static final String JOIN_URL = Common.URL + "/events/";
    private static final String URL = Common.URL + "/members/";
    private Event event;
    private ImageView location, members, messages;
    private Button join;
    private long userId;
    private long memberId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        final long eventId = intent.getLongExtra("eventId", -1);
        userId = Common.getCurrentUserId(this);

        event = getEvent(eventId);

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
        if (userId == event.getCreatorId()) {
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
        } else {
            if (isMember()) {
                join.setText("LEAVE");
                join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.isOnline(getApplicationContext())) {
                            System.out.println(jsonToLeave());
                            new RequestAPI(v.getContext(), "PATCH", jsonToLeave(), new AsyncTaskCompleteListener<String>() {
                                @Override
                                public void onTaskComplete(String result) {
                                    System.out.println(result);
                                    if (result.equals("200"))
                                        join.setText("JOIN");
                                }
                            }, true).execute(URL + memberId);
                        } else
                            Snackbar.make(findViewById(R.id.eventDetailView), "No Internet connection", Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.isOnline(getApplicationContext())) {
                            new RequestAPI(v.getContext(), "POST", jsonRequest(), EventDetailsActivity.this, false).execute(JOIN_URL + eventId + "/members");
                        } else
                            Snackbar.make(findViewById(R.id.eventDetailView), "No Internet connection", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }

        eName.setText(event.getName());
        if (event.getSportName() != null)
            eSport.setText(event.getSportName());
        eTime.setText(event.getDateWithTimeString());
        eDescription.setText(event.getDescription());
        eMembers.setText(event.getMembersStatus());
    }

    @Nullable
    private Event getEvent(long id) {
        SharedPreferences prefs = getSharedPreferences("EVENTS", Context.MODE_PRIVATE);
        ArrayList<Event> events = Event.jsonEventsToList(prefs.getString("EVENTS_JSON", "[]"));
        for (Event e : events) {
            if (e.getId() == id)
                return e;
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

                    LatLng marker = new LatLng(place.getLatitude(), place.getLongitude());
                    googleMap.addMarker(new MarkerOptions()
                            .position(marker)
                            .title(event.getName())
                            .snippet(event.getDateWithTimeString() + "\n" + event.getCurrMembers() + "\\" + event.getMaxMembers())
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

    @Override
    public void onTaskComplete(String result) {
        if (result.equals("200")) {
            join.setEnabled(false);
            join.setText("ZOSTAŁEŚ ZAAKCEPTOWANY");
            if (members.isSelected()) {
                MembersFragment fragment = (MembersFragment) getSupportFragmentManager().findFragmentById(R.id.frame);
                fragment.updateMembers();
            }
        }
    }

    private String jsonRequest() {
        JSONObject json = new JSONObject();
        try {
            json.put("status", "PENDING");
            json.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private boolean isMember() {
        for (Member member : event.getMembers()) {
            if (member.getUser().getId() == userId) {
                memberId = member.getId();
                return true;
            }
        }
        return false;
    }

    private String jsonToLeave() {
        return "{\"status\": \"CANCELED\", \"user_id\":" + userId + "}";
    }
}


