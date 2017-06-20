package matwes.zpi.Events;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import matwes.zpi.Common;
import matwes.zpi.Classes.Event;
import matwes.zpi.Classes.Place;
import matwes.zpi.EventDetails.EventDetailsActivity;
import matwes.zpi.R;

/**
 * Created by mateu on 04.04.2017.
 */

public class MapFragment extends MainFragment implements OnMapReadyCallback {
    private ClusterManager<MyItem> clusterManager;
    private GoogleMap googleMap;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        setHasOptionsMenu(true);

        MapView mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.EVENTS));
        getEvents();
    }


    private class EventRenderer extends DefaultClusterRenderer<MyItem> {

        EventRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<MyItem> cluster) {
            return true;
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.03, 18.25), 6));

        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterManager.setRenderer(new EventRenderer(getContext(), googleMap, clusterManager));

        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                if (googleMap.getCameraPosition().zoom >= 15 || cluster.getSize() < 2)
                    showAlertDialog(cluster.getItems());
                else {
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (ClusterItem item : cluster.getItems()) {
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
            Place p = event.getPlace();

            if (p != null) {
                LatLng marker = new LatLng(p.getLatitude(), p.getLongitude());
                MyItem offsetItem = new MyItem(marker, event.getId(), i);
                clusterManager.addItem(offsetItem);
            }
        }
    }

    private void showAlertDialog(final Collection<MyItem> items) {
        final long[] ids = new long[items.size()];
        int i = 0;

        List<Event> list = new ArrayList<>();
        for (MyItem item : items) {
            Event event = events.get(item.getLocalId());
            ids[i] = event.getId();
            list.add(event);
            i++;
        }

        final ListAdapter arrayAdapter = new ArrayAdapter<Event>(getContext(), R.layout.dialog_list_row, list) {
            ViewHolder holder;

            class ViewHolder {
                ImageView icon;
                TextView name, date, members;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.dialog_list_row, null);
                    holder = new ViewHolder();
                    holder.icon = (ImageView) convertView.findViewById(R.id.imageView2);
                    holder.name = (TextView) convertView.findViewById(R.id.textView2);
                    holder.date = (TextView) convertView.findViewById(R.id.textView4);
                    holder.members = (TextView) convertView.findViewById(R.id.textView3);
                    convertView.setTag(holder);
                } else
                    holder = (ViewHolder) convertView.getTag();

                Event event = getItem(position);
                holder.name.setText(event.getName());
                holder.date.setText(event.getDateWithTimeString());
                holder.members.setText(event.getMembersStatus());
                holder.icon.setImageResource(Common.getIcon(event.getSportName()));

                return convertView;
            }

        };

        new AlertDialog.Builder(getContext())
                .setTitle(items.size() + " " + getString(R.string.eventsInLocation))
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
    void updateList(ArrayList<Event> e) {
        super.updateList(e);
        if (filtered) {
            clusterManager.clearItems();
            addItems();
            clusterManager.cluster();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.findItem(R.id.change_view).setIcon(R.drawable.list);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    void removeOldEvents(ArrayList<Event> events) {
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
    void filterEvents(ArrayList<Event> events) {
        super.filterEvents(events);
        LatLng latLng;
        float zoom;
        switch (selectedCity) {
            case "Wrocław":
                latLng = new LatLng(51.1136, 17.0320);
                zoom = 12;
                break;
            case "Białcz":
                latLng = new LatLng(52.67, 14.93);
                zoom = 14;
                break;
            case "Warszawa":
                latLng = new LatLng(52.234, 21.02);
                zoom = 11;
                break;
            case "Racibórz":
                latLng = new LatLng(50.092, 18.219);
                zoom = 13;
                break;
            case "Kraków":
                latLng = new LatLng(50.062, 19.946);
                zoom = 11;
                break;
            case "Gdańsk":
                latLng = new LatLng(54.36, 18.63);
                zoom =12;
                break;
            default:
                latLng = new LatLng(52.03, 18.25);
                zoom=6;
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
}
