package matwes.zpi.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Iterator;
import java.util.List;

import matwes.zpi.R;
import matwes.zpi.domain.Event;

/**
 * Created by Mateusz Wesołowski
 */

public class MyEventsFragment extends MainFragment implements EventListAdapter.EventItemListener {
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


        getActivity().setTitle(R.string.my_events);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        adapter = new EventListAdapter(view.getContext(), events, this);
        recyclerView.setAdapter(adapter);

        swipe = view.findViewById(R.id.swipeEvents);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadEvents(true, false);
            }
        });
        getEvents();
    }

    @Override
    public void onApiResponse() {
        if (swipe.isRefreshing()) {
            swipe.setRefreshing(false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    void updateList(List<Event> e) {
        myFilterEvents(e);
        super.updateList(e);
        adapter.notifyDataSetChanged();
    }

    private void myFilterEvents(List<Event> events) {
        Iterator<Event> i = events.iterator();
        while (i.hasNext()) {
            Event event = i.next();
            if (!event.isCreator()) {
                i.remove();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.change_view).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void refreshView() {
        downloadEvents(false, true);
    }
}