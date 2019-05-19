package matwes.zpi.eventDetails;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.api.ApiInterface;
import matwes.zpi.api.RestService;
import matwes.zpi.domain.Event;
import matwes.zpi.domain.Member;
import matwes.zpi.utils.CustomDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembersFragment extends Fragment {
    private MembersListAdapter adapter;
    private SwipeRefreshLayout swipe;

    private Event event;
    private ArrayList<Member> members;

    private ApiInterface api;

    public MembersFragment() {
        api = RestService.getApiInstance();
    }

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


//        members = new ArrayList<>();
//        String membersJson = getArguments().getString("eventJson", "");
//        Gson gson = new GsonBuilder().create();
//        event = gson.fromJson(membersJson, Event.class);
//
//        if (Common.isOnline(getContext())) {
//            updateMembers();
//        } else {
//            members = event.getMembers();
//        }
//
//        boolean isOwner = event.getCreatorId().equals(Common.getCurrentUserId(getContext()));
//
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        adapter = new MembersListAdapter(getContext(), members, isOwner);
//        recyclerView.setAdapter(adapter);
//
//        swipe = view.findViewById(R.id.swipeEvents);
//        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (!updateMembers()) {
//                    swipe.setRefreshing(false);
//                }
//            }
//        });
    }

    boolean updateMembers() {
        if (Common.isOnline(getContext())) {
            //handleApiResponse(api.getMemebers(event.getId()));
            return true;
        } else {
            Snackbar.make(getView(), R.string.noInternet, Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    private void handleApiResponse(Call<List<Member>> call) {
        call.enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                List<Member> members = response.body();

                members.clear();
                members.addAll(members);
                adapter.notifyDataSetChanged();

                if (swipe.isRefreshing()) {
                    swipe.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<Member>> call, Throwable t) {
                if (swipe.isRefreshing()) {
                    swipe.setRefreshing(false);
                }
                CustomDialog.showError(getContext(), getString(R.string.error_message));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.findItem(R.id.change_view).setIcon(R.drawable.map);
        super.onCreateOptionsMenu(menu, inflater);
    }
}