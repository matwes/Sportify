package matwes.zpi.events;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import matwes.zpi.Common;
import matwes.zpi.R;
import matwes.zpi.domain.Event;
import matwes.zpi.eventDetails.EventDetailsActivity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mateusz Wesołowski
 */

class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.Holder> {
    private Context context;
    private ArrayList<Event> events;

    EventListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final Event event = events.get(position);
        holder.title.setText(event.getName());
        holder.date.setText(event.getDateWithTimeString());
        holder.members.setText(event.getMembersStatus());
        holder.place.setText(event.getPlace() != null ? event.getPlace().getName() : "");
        holder.icon.setImageResource(Common.getIcon(event.getSportName()));
        if (event.getDateWithTime() == null || event.getDateWithTime().before(new Date())) {
            holder.changeCardColor();
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventId", event.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView title, date, members = itemView.findViewById(R.id.tvEventMembers), place;
        ImageView icon;
        CardView eventCard;

        Holder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvEventName);
            date = itemView.findViewById(R.id.tvEventDate);
            place = itemView.findViewById(R.id.tvEventPlace);
            icon = itemView.findViewById(R.id.ivEventIcon);
            eventCard = itemView.findViewById(R.id.eventCard);
        }

        void changeCardColor() {
            eventCard.setBackgroundColor(Color.parseColor("#f44336"));
        }
    }
}
