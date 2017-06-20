package matwes.zpi.EventDetails;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;

import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.Classes.Event;
import matwes.zpi.Classes.Member;
import matwes.zpi.GetMethodAPI;
import matwes.zpi.R;

public class MembersFragment extends Fragment implements AsyncTaskCompleteListener<String>{
    private Event event;
    private MembersListAdapter adapter;
    private ArrayList<Member> members;
    private SwipeRefreshLayout swipe;


    public MembersFragment() {}

    public static MembersFragment newInstance(String event) {
        Bundle args = new Bundle();
        args.putString("eventJson", event);

        MembersFragment fragment = new MembersFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        members = new ArrayList<>();
        String membersJson = getArguments().getString("eventJson", "");
        Gson gson = new GsonBuilder().create();
        event = gson.fromJson(membersJson, Event.class);

        if(Common.isOnline(getContext())) {
            updateMembers();
        }else
            members = event.getMembers();

        boolean isOwner = event.getCreatorId() == Common.getCurrentUserId(getContext());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new MembersListAdapter(getContext(), members, isOwner, this);
        recyclerView.setAdapter(adapter);

        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipeEvents);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!updateMembers()) {
                    swipe.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onTaskComplete(String result)
    {
        String json;
        try {
            json = new JSONObject(result).getJSONArray("content").toString();
            members.clear();
            members.addAll(Member.jsonMembersToList(json));
            adapter.notifyDataSetChanged();

            if (swipe.isRefreshing())
                swipe.setRefreshing(false);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    boolean updateMembers()
    {
        if(Common.isOnline(getContext()))
        {
            new GetMethodAPI(getContext(), this, false).execute("https://zpiapi.herokuapp.com/events/"+event.getId()+"/members");
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.findItem(R.id.change_view).setIcon(R.drawable.map);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
