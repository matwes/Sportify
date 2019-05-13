package matwes.zpi.events;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import matwes.zpi.R;
import matwes.zpi.domain.Event;
import matwes.zpi.domain.Place;
import matwes.zpi.eventDetails.EventDetailsActivity;

/**
 * Created by Mateusz Wesołowski
 */

public class MapFragment extends MainFragment implements OnMapReadyCallback {
    private static final int CAMERA_ZOOM = 14;

    private ClusterManager<EventsCluster> clusterManager;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private double dLat, dLong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        setHasOptionsMenu(true);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        MapView mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.EVENTS);
        getEvents();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.03, 18.25), 6));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLat, dLong), CAMERA_ZOOM));

        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterManager.setRenderer(new EventRenderer(getContext(), googleMap, clusterManager));

        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<EventsCluster>() {
            @Override
            public boolean onClusterClick(Cluster<EventsCluster> eventsCluster) {
                if (googleMap.getCameraPosition().zoom >= 15 || eventsCluster.getSize() < 2)
                    showAlertDialog(eventsCluster.getItems());
                else {
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (ClusterItem item : eventsCluster.getItems()) {
                        builder.include(item.getPosition());
                    }

                    final LatLngBounds bounds = builder.build();

                    try {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        addItems();
        clusterManager.cluster();
    }

    private void addItems() {
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            Place place = event.getPlace();

            if (place != null) {
                matwes.zpi.domain.Location location = place.getLocation();
                LatLng marker = new LatLng(location.getLatitude(), location.getLongitude());
                EventsCluster offsetItem = new EventsCluster(marker, i);
                clusterManager.addItem(offsetItem);
            }
        }
    }

    private void showAlertDialog(final Collection<EventsCluster> items) {
        final String[] ids = new String[items.size()];
        int i = 0;

        List<Event> list = new ArrayList<>();
        for (EventsCluster item : items) {
            Event event = events.get(item.getLocalId());
            ids[i] = event.get_id();
            list.add(event);
            i++;
        }

        final ListAdapter arrayAdapter = new ArrayAdapter<Event>(getContext(), R.layout.dialog_list_row, list) {
            ViewHolder holder;

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.dialog_list_row, null);
                    holder = new ViewHolder();
                    holder.icon = convertView.findViewById(R.id.imageView2);
                    holder.name = convertView.findViewById(R.id.textView2);
                    holder.date = convertView.findViewById(R.id.textView4);
                    holder.members = convertView.findViewById(R.id.textView3);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                Event event = getItem(position);
                holder.name.setText(event.getName());
                holder.date.setText(event.getDateWithTimeString());
                holder.members.setText(event.getInterested() + "");
                if (event.getImage() != null && event.getImage() != "") {
                    Picasso.get().load(event.getImage()).into(holder.icon);
                }

                return convertView;
            }

            class ViewHolder {
                ImageView icon;
                TextView name, date, members;
            }

        };

        new AlertDialog.Builder(getContext())
                .setTitle(String.format("%d %s", items.size(), getString(R.string.eventsInLocation)))
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                        intent.putExtra("eventId", ids[which]);
                        getContext().startActivity(intent);
                    }
                })
                .show();
    }

    @Override
    void updateList(List<Event> e) {
        super.updateList(e);
        clusterManager.clearItems();
        addItems();
        clusterManager.cluster();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.findItem(R.id.change_view).setIcon(R.drawable.list);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    void removeOldEvents(List<Event> events) {
        Date date = new Date();
        Iterator<Event> i = events.iterator();
        while (i.hasNext()) {
            Event event = i.next();
            if (event.getDateWithTime() == null || event.getDateWithTime().before(date)) {
                i.remove();
            }
        }
    }

    @Override
    List<Event> filterEvents(List<Event> events) {
        List<Event> dest = new ArrayList<Event>();
        dest.addAll(EventService.getInstance().originalListOfEvents);

        List<Event> filteredEvents = super.filterEvents(dest);
        this.events = filteredEvents;
        LatLng latLng = new LatLng(51.1136, 17.0320);
        float zoom = 12;
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
        return filteredEvents;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                dLat = location.getLatitude();
                dLong = location.getLongitude();
            }
        }
    }

    private class EventRenderer extends DefaultClusterRenderer<EventsCluster> {

        EventRenderer(Context context, GoogleMap map, ClusterManager<EventsCluster> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected boolean shouldRenderAsCluster(com.google.maps.android.clustering.Cluster cluster) {
            return true;
        }
    }
}
