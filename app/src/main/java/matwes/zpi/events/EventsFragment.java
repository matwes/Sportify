package matwes.zpi.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.domain.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Mateusz Wesołowski
 */

public class EventsFragment extends MainFragment {

    private EventListAdapter adapter;
    private SwipeRefreshLayout swipe;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getString(R.string.EVENTS));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        adapter = new EventListAdapter(view.getContext(), events);
        recyclerView.setAdapter(adapter);

        swipe = view.findViewById(R.id.swipeEvents);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isOnline(getContext()))
                    downloadEvents();
                else {
                    swipe.setRefreshing(false);
                    Snackbar.make(parentView, "No Internet connection", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        getEvents();
    }

    @Override
    public void onTaskComplete(String result) {
        super.onTaskComplete(result);

        if (swipe.isRefreshing())
            swipe.setRefreshing(false);
    }

    @Override
    void updateList(ArrayList<Event> e) {
        super.updateList(e);
        adapter.notifyDataSetChanged();
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
}
