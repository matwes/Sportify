package matwes.zpi.events;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.domain.Event;
import matwes.zpi.eventDetails.EventDetailsActivity;

/**
 * Created by Mateusz Wesołowski
 */

class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.Holder> {
    EventFragmentType type;
    private Context context;
    private List<Event> events;
    private EventItemListener listener;

    EventListAdapter(Context context, List<Event> events, EventItemListener listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Event event = events.get(position);
        holder.title.setText(event.getName());
        holder.date.setText(event.getDateWithoutTimeString());
        holder.members.setText(event.getInterested() + "");
        holder.place.setText(event.getPlace() != null ? " - " + event.getPlace().getName() : "");
        if (event.getImage() == null || event.getImage().equals("")) {
            Picasso.get()
                    .load(Common.getEventPlaceholder())
                    .placeholder(Common.getEventPlaceholder())
                    .into(holder.eventImage);
        } else {
            Picasso.get()
                    .load(event.getImage())
                    .placeholder(Common.getEventPlaceholder())
                    .into(holder.eventImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                if (now - holder.mLastClickTime < Holder.CLICK_TIME_INTERVAL) {
                    return;
                }
                holder.mLastClickTime = now;
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventId", event.get_id());
                context.startActivity(intent);
            }
        });

        switch (type){
            case blocked: holder.blockedIcon.setImageResource(R.drawable.blocked_event); break;
            case interesting: holder.blockedIcon.setImageResource(R.drawable.unblocked_event); break;
            case unblocked: {
                if(event.isInterested()) {
                    holder.blockedIcon.setImageResource(R.drawable.unblocked_event);
                } else {
                    holder.blockedIcon.setImageResource(0);
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public interface EventItemListener {
        void refreshView();
    }

    class Holder extends RecyclerView.ViewHolder {
        static final long CLICK_TIME_INTERVAL = 1000;
        TextView title, date, members, place;
        CardView eventCard;
        ImageView eventImage;
        View eventInfoView;
        ImageView blockedIcon;
        long mLastClickTime = System.currentTimeMillis();

        Holder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvEventName);
            date = itemView.findViewById(R.id.tvEventDate);
            members = itemView.findViewById(R.id.tvEventMembers);
            place = itemView.findViewById(R.id.tvEventPlace);
            eventCard = itemView.findViewById(R.id.eventCard);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventInfoView = itemView.findViewById(R.id.eventInfoView);
            blockedIcon = itemView.findViewById(R.id.blocked_icon);
            blockedIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (type) {
                        case blocked:
                            // post to block if success than in callback do this
                            System.out.println("set as Unblock");
                            listener.refreshView();
                            break;
                        case unblocked:
                            // post to block if success than in callback do this
                            System.out.println("set as Block");
                            listener.refreshView();
                            break;
                    }
                }
            });
        }
    }
}